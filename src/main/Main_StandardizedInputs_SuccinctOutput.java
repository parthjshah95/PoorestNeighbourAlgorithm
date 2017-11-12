package main;

import main.Algorithms.*;
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
public class Main_StandardizedInputs_SuccinctOutput {

    //Create arrays for succinct outputs
    private static double[][] output = new double[43][21];


    public static void main(String[] args) {
        /***
         INPUT FORMAT
         * Each file starts with an integer :- the number of nodes N. Each of the next N lines have N columns each, forming and NxN table.
         * The entry at ith column and jth row signifies the communication bandwidth requirements between the nodes i and j.
         * If the entry is 'INF' tht signifies there is no connection between the two nodes.
         * Eg.:-
         16
         0  70 INF INF INF INF INF INF INF INF INF INF INF INF INF INF
         70   0 362 INF INF INF INF INF INF INF INF INF INF INF INF INF
         INF 362   0 362 INF INF INF INF INF INF INF INF INF INF INF INF
         INF INF 362   0 362 INF INF INF INF INF INF INF INF INF INF  49
         INF INF INF 362   0 357 INF INF INF INF INF INF INF INF INF  27
         INF INF INF INF 357   0 353 INF INF INF INF  16 INF INF INF INF
         INF INF INF INF INF 353   0 300 INF INF INF INF INF INF INF INF
         INF INF INF INF INF INF 300   0 313 500 INF INF INF INF INF INF
         INF INF INF INF INF INF INF 313   0 407 INF  16 INF INF INF INF
         INF INF INF INF INF INF INF 500 407   0 INF INF INF INF INF INF
         INF INF INF INF INF INF INF INF INF INF   0  16 INF INF  16 INF
         INF INF INF INF INF  16 INF INF  16 INF  16 0    16 INF INF INF
         INF INF INF INF INF INF INF INF INF INF INF  16   0 157  16 INF
         INF INF INF INF INF INF INF INF INF INF INF INF 157   0  16 INF
         INF INF INF INF INF INF INF INF INF INF  16 INF  16  16   0 INF
         INF INF INF  49  27 INF INF INF INF INF INF INF INF INF INF   0
         ***/


        for (int serialNo = 1; serialNo <= 43; serialNo++) {
            if (serialNo == 16 || serialNo == 24) continue;
            //taking input from a file and output to a file:
            try {
                System.setIn(new FileInputStream(new File("resources/inputGraphs/Graph" + serialNo + ".txt")));
                File out = new File("resources/outputGraphs/Graph" + serialNo + "Output.txt");
                out.createNewFile();
                System.setOut(new PrintStream(out));
            } catch (Exception e) {
                String ex = e.getMessage();
            }
            //comment out the above code to not redirect stdIn/stdOut from/to a file.


            // taking input from stdIn:
            Scanner input = new Scanner(System.in);
            Graph graph = new Graph();

            int N = input.nextInt();
            for (int n = 1; n <= N; n++) {
                graph.numberOfNodes = N;
                for (int m = 1; m <= N; m++) {
                    if (input.hasNextDouble() && m < n) {
                        Double w = input.nextDouble();
                        Edge edge = new Edge(m, n, w);
                        graph.insert(edge);
                    } else input.next();

                }
            }

            //Input graph(s) taken.
            // Now applying any of the algorithmName classes to generate topology.


            System.out.println("For graph " + serialNo);
            graph.printInputGraph();
            System.out.println();
            output[serialNo - 1][0] = graph.numberOfNodes;
            List<Algorithm> algorithms = new ArrayList<Algorithm>(5/*TODO change to 6 after adding algo*/);
            algorithms.add(new Native_GraphTopology());
            algorithms.add(new Basic_NonFaultTolerant());
            algorithms.add(new PoorestNeighbour_LinkFaultTolerant_PortUtil_PostFix_NoPortLimit());
            algorithms.add(new PoorestNeighbour_LinkFaultTolerant_PortUtil_PreFix_NoPortLimit());
            algorithms.add(new DeBruijn_LinkFaultTolerant());
            //TODO add newer poorest link algo
            for (int algo = 0; algo < algorithms.size(); algo++) {
                double[] results = AlgorithmRunner.run(algorithms.get(algo), graph);
                int algoStartColumn = algo * results.length + 1;
                for (int i = 0; i < results.length; i++){
                    output[serialNo - 1][algoStartColumn + i] = results[i];
                }
            }
        /*print succinct output*/

            try {
                File out = new File("resources/outputGraphs/00_SuccinctOutput.txt");
                out.createNewFile();
                System.setOut(new PrintStream(out));
            } catch (Exception e) {
                String ex = e.getMessage();
            }
            for (int i = 0; i < 43; i++) {
                System.out.print("{");
                for (int j = 0; j < 20; j++) {
                    System.out.print(output[i][j] + ", ");
                }
                System.out.println(output[i][20] + "}");
            }
        }

    }
}
