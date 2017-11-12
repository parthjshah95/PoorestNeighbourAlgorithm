package main.Algorithms;

import main.Basic_Infrastructure.Edge;
import main.Basic_Infrastructure.Graph;
import main.Basic_Infrastructure.Topology;
import main.Basic_Infrastructure.TopologyEvaluator;

import java.util.*;

/**
 * Created by Xorcist on 11-04-2017.
 */
public class PoorestNeighbour_LinkFaultTolerant_PortUtil_PostFix_NoPortLimit implements Algorithm {

    public String algorithmName() {
        return "poorest neighbour - link fault tolerant, post fix, port util, no port limit, bandwidth priority";
    }

    public Topology generateTopology(Graph graph) {
        Native_GraphTopology tempObject = new Native_GraphTopology();
        Topology topology = tempObject.generateTopology(graph);
        HashMap<Integer, List<Integer>> connections = topology.connections;
        List<Edge> edges = graph.edges;
        List<Edge> isolatedEdges = new ArrayList<>();
        int size = edges.size();
        for (int i = 0; i < size; i++) {
            //instead of going in ascending order of routers, we should go increasing order of communication requirements
            //Also, since we are using the native topology as the foundation, we can use the edges of input graph for iteration purposes.
            Edge edge = edges.get(i);
            Integer a = edge.node1;
            Integer b = edge.node2;
            boolean tolerant = false;
            try {
                tolerant = TopologyEvaluator.checkLinkFaultTolerance(a, b, topology);
            } catch (Exception e) {
            }
            if (!tolerant) {
                int lowestUtil = 2147483647;
                Integer poorestNeighbour = 0;
                if (connections.get(b).size() > connections.get(a).size()) {
                    Iterator neighbours_of_b = connections.get(b).iterator();
                    while (neighbours_of_b.hasNext()) {
                        Integer neighbour = (Integer) neighbours_of_b.next();
                        int util = connections.get(neighbour).size();
                        if (util < lowestUtil && neighbour != a) {
                            poorestNeighbour = neighbour;
                            lowestUtil = util;
                        }
                    }
                    topology.addConnection(poorestNeighbour, a);
                } else {
                    Iterator neighbours_of_a = connections.get(a).iterator();
                    while (neighbours_of_a.hasNext()) {
                        Integer neighbour = (Integer) neighbours_of_a.next();
                        int util = connections.get(neighbour).size();
                        if (util < lowestUtil && neighbour != b) {
                            poorestNeighbour = neighbour;
                            lowestUtil = util;
                        }
                    }
                    if (poorestNeighbour != 0) {
                        topology.addConnection(poorestNeighbour, b);
                    } else {//case of isolated pair
                        isolatedEdges.add(edge);
                    }
                }
            }
        }
        for (Edge edge : isolatedEdges) {
            Integer a = edge.node1;
            Integer b = edge.node2;
            //get the node with lowest port utilization in the rest of the topology
            Integer poorestNode = getPoorestNode(connections, a, b);
            //connect a and b to poorestNode.
            topology.addConnection(a, poorestNode);
            topology.addConnection(b, poorestNode);
        }
        return topology;
    }

    public static Integer getPoorestNode(HashMap<Integer, List<Integer>> connections, Integer exception1, Integer exception2) {
        int benchmark = 2147483647;
        Integer poorestNode = 0;
        Iterator itr = connections.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            Integer currentNode = (Integer) entry.getKey();
            int ports = connections.get(currentNode).size();
            if (ports <= benchmark && currentNode != exception1 && currentNode != exception2) {
                //exceptions refer to the nodes of the link which we want to make fault tolerant.
                benchmark = ports;
                poorestNode = currentNode;
            }
        }
        return poorestNode;
    }
}
