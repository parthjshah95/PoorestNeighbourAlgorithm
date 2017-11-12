package main;

import main.Algorithms.Algorithm;
import main.Basic_Infrastructure.Graph;
import main.Basic_Infrastructure.Topology;
import main.Basic_Infrastructure.TopologyEvaluator;

/**
 * Created by parshah on 12/09/17.
 */
public class AlgorithmRunner {


    public static double[] run(Algorithm algorithm, Graph graph) {
        double[] output = new double[4];
        //Topology generator:
        System.out.println("Using topology generator: " + algorithm.algorithmName());
        Topology topology = algorithm.generateTopology(graph);
        //print the topology:-
        System.out.print("Topology: ");
        topology.printTopology();
        //Test this topology:-
        int NoL = topology.numberOfLinks();
        System.out.println("Number of Links:- " + NoL);

        double commCost = TopologyEvaluator.commCost(graph, topology);

        System.out.println("Communication cost: " + commCost);
        System.out.println("Link utilization graph: " + TopologyEvaluator.linkUtilizationGraph(graph, topology));
        double avgLinkUtil = TopologyEvaluator.avgLinkUtilization(graph, topology);
        System.out.println("Average link utilization: " + avgLinkUtil);

        System.out.println("Non-fault tolerant links: " + TopologyEvaluator.printNonFaultTolerantLinks(graph, topology));
        double linkFaultTolerance = TopologyEvaluator.linkFaultTolerance(graph, topology);
        System.out.println("Link fault tolerance: " + linkFaultTolerance + "%");

        output[0] = NoL;
        output[1] = commCost;
        output[2] = avgLinkUtil;
        output[3] = linkFaultTolerance;
        System.out.println();
        return output;
    }
}
