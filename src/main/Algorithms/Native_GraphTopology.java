package main.Algorithms;

import main.Basic_Infrastructure.Edge;
import main.Basic_Infrastructure.Graph;
import main.Basic_Infrastructure.Topology;

import java.util.ListIterator;

/**
 * Created by Xorcist on 11-04-2017.
 */
public class Native_GraphTopology implements Algorithm {

    public String algorithmName() {
        return "Copy as it is from input graph";
    }

    public Topology generateTopology(Graph inputGraph) {
        Topology topology = new Topology();
        ListIterator<Edge> itr = inputGraph.edges.listIterator();
        while (itr.hasNext()) {
            Edge edge = itr.next();
            topology.addConnection(edge);
        }
        return topology;
    }
}
