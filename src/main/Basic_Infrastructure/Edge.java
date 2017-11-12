package main.Basic_Infrastructure;

/**
 * Created by Xorcist on 2/28/2017.
 */
public class Edge {
    public Integer node1;
    public Integer node2;
    public Double weight;

    public Edge(Integer node1, Integer node2){
        this.node1 = node1;
        this.node2 = node2;
    }

    public Edge(Integer node1, Integer node2, Double weight) {
        this(node1, node2);
        this.weight = weight;
    }
}
