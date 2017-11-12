package main.Algorithms;

import main.Basic_Infrastructure.Graph;
import main.Basic_Infrastructure.Topology;

/**
 * Created by parshah on 12/09/17.
 */
public interface Algorithm {

    String algorithmName();

    Topology generateTopology(Graph inputGraph);
}
