package main;

import main.Algorithms.Basic_NonFaultTolerant;
import main.Algorithms.DeBruijn_LinkFaultTolerant;
import main.Algorithms.Native_GraphTopology;
import main.Algorithms.PoorestNeighbour_LinkFaultTolerant_PortUtil_PostFix_NoPortLimit;
import main.Basic_Infrastructure.Edge;
import main.Basic_Infrastructure.Graph;
import main.Basic_Infrastructure.Topology;
import main.Basic_Infrastructure.TopologyEvaluator;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Created by Xorcist on 10-04-2017.
 */
public class Main_CompactInputs_VerboseOutput {

    public static void main(String[] args) {
        List<Graph> graphList = new ArrayList<>();
        /*
        * input format:-
        * First line contains number of input graphs.For each graph, its first line contains two integers N(number of nodes) and M(number of edges).
        * For next M lines, each line contains three integers n1, n2 and w[n1, n2] where
        * w[n1, n2] is the weight or the communication requirement between the two nodes n1 and n2.
        1
        4 4
        1 2 2
        1 3 7
        2 3 5
        2 4 1
        */
        //taking input from a file and output to a file:
        try {
            System.setIn(new FileInputStream(new File("resources/input.txt")));
            System.setOut(new PrintStream(new File("resources/output.txt")));
        } catch (Exception e) {
        }
        //comment out the above code to not redirect stdIn/stdOut from/to a file.


        // taking input from stdIn:
        Scanner input = new Scanner(System.in);

        int T = input.nextInt();
        for (int t = 0; t < T; t++) {
            int N = input.nextInt();
            int M = input.nextInt();

            Graph graph = new Graph();
            graph.numberOfNodes = N;
            for (int m = 0; m < M; m++) {
                Integer n1 = input.nextInt();
                Integer n2 = input.nextInt();
                Double w = input.nextDouble();
                Edge edge = new Edge(n1, n2, w);
                graph.insert(edge);
            }
            graphList.add(graph);
        }

        //Input graph(s) taken.

        //to print all graphs:
       /* for (Graph graph : graphList) {
            graph.printInputGraph();
        }*/

        // Now applying any of the algorithmName classes to generate topology.*/

        int g = 0;
        for (Graph graph : graphList) {
            System.out.println("For graph " + ++g);
            AlgorithmRunner.run(new Native_GraphTopology(), graph);
        }

    }

}
