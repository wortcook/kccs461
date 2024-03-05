package edu.umkc.cs461.hw1.data;

public class NodePair<K> {
    private K a_node;
    private K b_node;

    public NodePair(K a_node, K b_node) {
        this.a_node = a_node;
        this.b_node = b_node;
    }

    public K getANode() {
        return a_node;
    }

    public K getBNode() {
        return b_node;
    }
    
}
