package main.Basic_Infrastructure;


import java.util.*;

/**
 * Created by Xorcist on 10-04-2017.
 */
public class TopologyEvaluator {
    public static int commCost(Graph graph, Topology topology) {
        ListIterator<Edge> itr = graph.edges.listIterator();
        int sum = 0;
        while (itr.hasNext()) {
            Edge edge = itr.next();
            int hops = topology.hops(edge);
            if (hops <= 0) {
                System.out.print("Invalid topology! ");
                System.out.println("Nodes" + edge.node1 + " & " + edge.node2 + " have no connection!");
                sum = 0;
                break;
            }
            sum += hops * edge.weight;
        }
        return sum;
    }

    public static float avgLinkUtilization(Graph graph, Topology topology) {
        HashMap<Integer, HashMap<Integer, Integer>> linkUtilMap = linkUtilizationGraph(graph, topology);
        float sum = 0;
        float denom = 0;
        Iterator itr1 = linkUtilMap.entrySet().iterator();
        while (itr1.hasNext()) {
            Iterator itr2 = ((HashMap<Integer, Integer>) ((Map.Entry) itr1.next()).getValue()).entrySet().iterator();
            while (itr2.hasNext()) {
                sum += (int) ((Map.Entry) itr2.next()).getValue();
                denom++;
            }
        }
        return sum / denom;
    }

    public static HashMap<Integer, HashMap<Integer, Integer>> linkUtilizationGraph(Graph graph, Topology topology) {
        HashMap<Integer, HashMap<Integer, Integer>> linkUtilMap = new HashMap<Integer, HashMap<Integer, Integer>>();
        //populate the LUM with zeros.
        Iterator itr = topology.connections.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            ArrayList<Integer> nList = (ArrayList<Integer>) entry.getValue();
            Iterator smlItr = nList.iterator();
            HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
            while (smlItr.hasNext()) {
                map.put((Integer) smlItr.next(), 0);
                linkUtilMap.put((Integer) entry.getKey(), map);
            }
        }
        //System.out.println(linkUtilMap);
        //Done
        //Now take each edge and mark the links in the shortest path
        Iterator edgeItr = graph.edges.iterator();
        while (edgeItr.hasNext()) {
            Edge edge = (Edge) edgeItr.next();
            HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> results = LUM_Iterator(edge.node1, edge.node2, new ArrayList<Integer>(), 0, linkUtilMap);
            Integer shortestPath = new TreeSet<Integer>(results.keySet()).first();
            linkUtilMap = results.get(shortestPath);
        }
        //This gives faulty result because it assumes link(a,b) !== link(b,a)
        //We need to now rectify this.
        Iterator itr2 = linkUtilMap.entrySet().iterator();
        while (itr2.hasNext()) {
            Map.Entry entry = (Map.Entry) itr2.next();
            Integer a = (Integer) entry.getKey();
            HashMap<Integer, Integer> map = (HashMap) entry.getValue();
            Iterator itr3 = map.entrySet().iterator();
            while (itr3.hasNext()) {
                Integer b = (Integer) ((Map.Entry) itr3.next()).getKey();
                if (a < b) {
                    Integer util = linkUtilMap.get(a).get(b) + linkUtilMap.get(b).get(a);
                    linkUtilMap.get(a).put(b, util);
                    linkUtilMap.get(b).put(a, util);
                }
            }
        }
        return linkUtilMap;
    }

    private static HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> LUM_Iterator(Integer node1, Integer node2, List<Integer> visitedNodes, int hops, HashMap<Integer, HashMap<Integer, Integer>> LUM) {
        hops++;
        visitedNodes.add(node1);
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> ret = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>();
        HashMap<Integer, Integer> nodesList = LUM.get(node1);
        if (nodesList.containsKey(node2)) {
            Integer util = nodesList.get(node2);
            nodesList.put(node2, ++util);
            LUM.put(node1, nodesList);
            ret.put(hops, LUM);
            return ret;
        }
        Iterator itr = nodesList.keySet().iterator();
        ret = null;
        while (itr.hasNext()) {
            Integer nextNode = (Integer) itr.next();
            if (!visitedNodes.contains(nextNode)) {
                HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> temp = LUM_Iterator(nextNode, node2, visitedNodes, hops, LUM);
                if (temp != null) {
                    if (ret == null) {
                        ret = temp;

                    } else {
                        ret.putAll(temp);
                    }
                    Integer util = nodesList.get(nextNode);
                    nodesList.put(nextNode, ++util);
                    LUM.put(node1, nodesList);
                }
            }
        }
        return ret;
    }

    public static float linkFaultTolerance(Graph graph, Topology topology) {
        return faultTolerantLinks(graph, topology).size() * 100.0f / topology.numberOfLinks();
    }

    private static List<Edge> faultTolerantLinks(Graph graph, Topology topology) {
        List<Edge> faultTolerantLinks = new ArrayList<Edge>();
        List<Edge> edges = new ArrayList<Edge>();
        for (Map.Entry entry : topology.connections.entrySet()) {
            Integer node1 = (Integer) entry.getKey();
            for (Integer node2 : (List<Integer>) entry.getValue()) {
                if (node1 < node2) {
                    edges.add(new Edge(node1, node2));
                }
            }
        }
        for (Edge edge : edges) {
            try {
                if (checkLinkFaultTolerance(edge, topology)) faultTolerantLinks.add(edge);
            } catch (Exception e) {
            }
        }
        return faultTolerantLinks;
    }

    private static List<Edge> nonFaultTolerantLinks(Graph graph, Topology topology) {
        List<Edge> faultTolerantLinks = new ArrayList<Edge>();
        List<Edge> edges = new ArrayList<Edge>();
        for (Map.Entry entry : topology.connections.entrySet()) {
            Integer node1 = (Integer) entry.getKey();
            for (Integer node2 : (List<Integer>) entry.getValue()) {
                if (node1 < node2) {
                    edges.add(new Edge(node1, node2));
                }
            }
        }
        for (Edge edge : edges) {
            try {
                if (!checkLinkFaultTolerance(edge, topology)) faultTolerantLinks.add(edge);
            } catch (Exception e) {
            }
        }
        return faultTolerantLinks;
    }

    public static String printFaultTolerantLinks(Graph graph, Topology topology) {
        String ret = "";
        List<Edge> edges = faultTolerantLinks(graph, topology);
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            ret += edge.node1 + " - " + edge.node2 + ";  ";
        }
        return ret;
    }

    public static String printNonFaultTolerantLinks(Graph graph, Topology topology) {
        String ret = "";
        List<Edge> edges = nonFaultTolerantLinks(graph, topology);
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            ret += edge.node1 + " - " + edge.node2 + ";  ";
        }
        return ret;
    }

    public static boolean checkLinkFaultTolerance(Integer a, Integer b, Topology topology) throws Exception {
        return checkLinkFaultTolerance(new Edge(a, b), topology);
    }

    public static boolean checkLinkFaultTolerance(Edge edge, Topology inpTopology) throws Exception {
        Topology topology = inpTopology.clone();
        int hops = topology.hops(edge);
        if (hops == -1) return false;
        else if (hops == 1) {
            topology.removeConnection(edge);
            boolean ret = (topology.hops(edge) > 0);
            topology.addConnection(edge);
            return ret;
        } else throw new Exception("not a link");
    }

}
