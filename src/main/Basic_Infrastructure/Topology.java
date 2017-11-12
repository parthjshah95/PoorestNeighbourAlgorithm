package main.Basic_Infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static java.lang.Integer.min;

/**
 * Created by Xorcist on 10-04-2017.
 */
public class Topology {
    public HashMap<Integer, List<Integer>> connections = new HashMap<>();
    private List<Integer> visitedNodes = new ArrayList<>();
    private List<Integer> visitedNodesFT = new ArrayList<>();
    private List<Integer> costList = new ArrayList<>();

    public void printTopology() {
        System.out.println(connections.entrySet().toString());
    }

    public int numberOfLinks() {
        int n = 0;
        for (Integer key : connections.keySet()) {
            n += connections.get(key).size();
        }
        return n / 2;
    }

    public int hops(Edge edge) {
        return hops(edge.node1, edge.node2);
    }

    public boolean checkConnection(Edge edge) {
        return checkConnection(edge.node1, edge.node2);
    }

    public boolean checkConnection(Integer node1, Integer node2) {
        if (connections.containsKey(node1) && connections.containsKey(node2)) {
            boolean a = connections.get(node1).contains(node2);
            boolean b = connections.get(node2).contains(node1);
            if (a && b) return true;
            else if (!a && !b) return false;
            System.out.println("Corrupted topology!");
            return false;

        }
        return false;
    }

    public int hops(Integer node1, Integer node2) {
        if (!connections.containsKey(node1) || !connections.containsKey(node1)) {
            return -1;
        } else if (node1 == node2) {
            return 0;
        } else {
            visitedNodes.clear();
            int hopsIterator = hopsIterator(node1, node2, 0);
            if (hopsIterator == 0) return -1;
            return hopsIterator;
        }
    }

    private int hopsIterator(Integer node1, Integer node2, int hops) {
        hops++;
        int ret = 0;
        visitedNodes.add(node1);
        List nodelist = connections.get(node1);
        if (nodelist.contains(node2)) {
            return hops;
        }
        Iterator itr = nodelist.iterator();
        while (itr.hasNext()) {
            Integer nextNode = Integer.valueOf(itr.next().toString());
            if (!visitedNodes.contains(nextNode)) {
                int temp = hopsIterator(nextNode, node2, hops);
                if (temp > 0) {
                    if (ret > 0) ret = min(ret, temp);
                    else ret = temp;
                }
            }
        }
        return ret;
    }

    public void addConnection(Edge edge) {
        addConnection(edge.node1, edge.node2);
    }

    public void addConnection(Integer a, Integer b) {
        if (!checkConnection(a, b)) {
            if (connections.containsKey(a)) {
                connections.get(a).add(b);
            } else {
                List<Integer> list = new ArrayList<>();
                list.add(b);
                connections.put(a, list);
            }

            if (connections.containsKey(b)) {
                connections.get(b).add(a);
            } else {
                List<Integer> list = new ArrayList<>();
                list.add(a);
                connections.put(b, list);
            }
        }
    }

    public boolean removeConnection(Edge edge) {
        return removeConnection(edge.node1, edge.node2);
    }

    public boolean removeConnection(Integer a, Integer b) {
        return connections.get(a).remove(b) && connections.get(b).remove(a);
    }

    public Topology clone() {
        Topology output = new Topology();
        HashMap<Integer, List<Integer>> newConnections = new HashMap<>();
        for (Integer key : connections.keySet()) {
            List values = new ArrayList<Integer>();
            values.addAll(connections.get(key));
            newConnections.put(key, values);
        }
        output.connections = newConnections;
        return output;
    }
}
