package main.Algorithms;

import main.Basic_Infrastructure.Graph;
import main.Basic_Infrastructure.Topology;

/**
 * Created by Xorcist on 13-04-2017.
 */
public class DeBruijn_LinkFaultTolerant implements Algorithm {

    public String algorithmName() {
        return "De Bruijn's sequence";
    }

    public Topology generateTopology(Graph graph) {
        int N = graph.numberOfNodes;
        Topology topology = new Topology();
        topology.addConnection(1, 2);
        int p = 2;
        for (int c = 3; c <= N; c++) {
            topology.addConnection(p, c);
            if (c % 2 == 0) {
                p++;
            }
        }
        topology.addConnection(N, N - 1);
        p = N - 1;
        int e = N % 2;
        for (int c = N - 2; c > 0; c--) {
            topology.addConnection(p, c);
            if (c % 2 != e) {
                p--;
            }
        }
        return topology;
    }
}
