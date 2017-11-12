package main.Algorithms;

import main.Basic_Infrastructure.Graph;
import main.Basic_Infrastructure.Topology;
import main.Basic_Infrastructure.TopologyEvaluator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Xorcist on 11-04-2017.
 */
public class PoorestNeighbour_LinkFaultTolerant_LinkUtil_PostFix_NoPortLimit implements Algorithm {

    public String algorithmName() {
        return "poorest neighbour - post fix, link util, no port limit, bandwidth priority";
    }

    public Topology generateTopology(Graph graph) {
        Native_GraphTopology tempObject = new Native_GraphTopology();
        Topology topology = tempObject.generateTopology(graph);
        HashMap<Integer, HashMap<Integer, Integer>> linkUtilMap = TopologyEvaluator.linkUtilizationGraph(graph, topology);
        HashMap<Integer, Integer> newLinkages = new HashMap<Integer, Integer>();
        Iterator itr = topology.connections.entrySet().iterator();
        while (itr.hasNext()) {//instead of going in ascending order of routers, we should go increasing order of communication requirements
            //Also, since we are using the native topology as the foundation, we can use the edges of input graph for iteration purposes.
            Map.Entry entry = (Map.Entry) itr.next();
            Integer a = (Integer) entry.getKey();
            Iterator itr2 = ((List) entry.getValue()).iterator();
            while (itr2.hasNext()) {
                Integer b = (Integer) itr2.next();
                if (b > a) {//so that each link is visited only once
                    boolean temp = false;
                    try {
                        temp = TopologyEvaluator.checkLinkFaultTolerance(a, b, topology);
                    } catch (Exception e) {
                    }
                    if (!temp) {
                        int lowestUtil = 2147483647;
                        Integer poorestNeighbour = 0;
                        Integer joinee = a;
                        if (linkUtilMap.get(b).size() > linkUtilMap.get(a).size()) {
                            Iterator itr_lum_b = linkUtilMap.get(b).entrySet().iterator();
                            while (itr_lum_b.hasNext()) {
                                Map.Entry entry_b = (Map.Entry) itr_lum_b.next();
                                Integer neighbour = (Integer) (entry_b).getKey();
                                Integer util = linkUtilMap.get(neighbour).get(b);
                                if (util < lowestUtil && neighbour != a) {
                                    poorestNeighbour = neighbour;
                                    lowestUtil = util;
                                }
                            }
                        } else {
                            joinee = b;
                            Iterator itr_lum_a = linkUtilMap.get(a).entrySet().iterator();
                            while (itr_lum_a.hasNext()) {
                                Map.Entry entry_a = (Map.Entry) itr_lum_a.next();
                                Integer neighbour = (Integer) (entry_a).getKey();
                                Integer util = linkUtilMap.get(neighbour).get(a);
                                if (util < lowestUtil && neighbour != b) {
                                    poorestNeighbour = neighbour;
                                    lowestUtil = util;
                                }
                            }
                        }
                        if (poorestNeighbour != 0) {
                            newLinkages.put(joinee, poorestNeighbour);
                            linkUtilMap.get(joinee).put(poorestNeighbour, 0);
                            linkUtilMap.get(poorestNeighbour).put(joinee, 0);
                        } else {//case of isolated pair
                            //get the (highest?) node with lowest port utilization in the rest of the topology
                            Integer poorestNode = getPoorestNode(linkUtilMap, a, b);
                            //connect 'a' to poorestNode.
                            newLinkages.put(a, poorestNode);
                            linkUtilMap.get(poorestNode).put(a, 0);
                            linkUtilMap.get(a).put(poorestNode, 0);
                            newLinkages.put(b, poorestNode);
                            linkUtilMap.get(poorestNode).put(b, 0);
                            linkUtilMap.get(b).put(poorestNode, 0);

                            /*//new benchmark and poorestNeighbourOfPNode
                            int benchmark1 = 2147483647;
                            Integer poorestNeighbourOfPNode = 0;
                            Iterator itr_lum_pNode = linkUtilMap.get(poorestNode).entrySet().iterator();
                            while (itr_lum_pNode.hasNext()) {
                                Map.Entry entry_pNode = (Map.Entry) itr_lum_pNode.next();
                                Integer neighbour = (Integer) (entry_pNode).getKey();
                                int util1 = linkUtilMap.get(neighbour).size();
                                if (util1 < benchmark1 && neighbour != a && neighbour != b) {
                                    poorestNeighbourOfPNode = neighbour;
                                    benchmark1 = util1;
                                }
                            }
                            if (poorestNeighbourOfPNode != 0) {
                                newLinkages.put(b, poorestNeighbourOfPNode);
                                linkUtilMap.get(poorestNeighbourOfPNode).put(b, 0);
                                linkUtilMap.get(b).put(poorestNeighbourOfPNode, 0);
                            }else {
                                newLinkages.put(b, poorestNode);
                            }*/
                        }
                    }
                }
            }
        }

        for (Map.Entry entry : newLinkages.entrySet()) {
            Integer node1 = (Integer) entry.getKey();
            Integer node2 = (Integer) entry.getValue();
            topology.addConnection(node1, node2);
        }
        return topology;
    }

    public static Integer getPoorestNode(HashMap<Integer, HashMap<Integer, Integer>> LUM, Integer exception1, Integer exception2) {
        int benchmark = 2147483647;
        Integer poorestNode = 0;
        Iterator itr = LUM.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            Integer currentNode = (Integer) entry.getKey();
            int ports = LUM.get(currentNode).size();
            if (ports <= benchmark && currentNode != exception1 && currentNode != exception2) {
                //exceptions refer to the nodes of the link which we want to make fault tolerant.
                benchmark = ports;
                poorestNode = currentNode;
            }
        }
        return poorestNode;
    }
}
