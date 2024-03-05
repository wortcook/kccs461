package edu.umkc.cs461.hw1.data;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class BiDirectGraph<K extends Comparable<K>>{
    final ArrayList<K> nodes = new ArrayList<K>();
    double[][] edges;

    protected BiDirectGraph(final List<K> nodes, final double[][] edges){
        ;
    }

    public static class BiDirectGraphBuilder<K extends Comparable<K>>{

        private ArrayList<K> toAdd = new ArrayList<K>();
        private List<NodePair<K>> edges = new ArrayList<NodePair<K>>();

        public BiDirectGraphBuilder(){
        }

        public BiDirectGraphBuilder<K> addNodes(List<K> nodes){
            toAdd.addAll(nodes);
            return this;
        }


        public BiDirectGraphBuilder<K> addNode(K node){
            toAdd.add(node);
            return this;
        }

        public BiDirectGraphBuilder<K> addEdge(NodePair<K> edge){
            edges.add(edge);
            return this;
        }

        public BiDirectGraphBuilder<K> addEdges(List<NodePair<K>> edges){
            edges.addAll(edges);
            return this;
        }

        public BiDirectGraph<K> build(){

            BiDirectGraph<K> graph = null;

            toAdd.sort(null);

            Map<K, Integer> nodeMap = new HashMap<K, Integer>();

            //the following auto suggested by co-pilot and slightly edited
            for(int i = 0; i < toAdd.size(); i++){
                nodeMap.put(toAdd.get(i), i);
            }
            
            

            //the following auto suggested by co-pilot and slightly edited
            //by me to add the pyramid sizing of the array
            //we do this because the connections are bi-directional
            //hence then allows us to save the space of the array
            //we're creating a graph for fast traversal. Because
            //we know the locations we can pre-calculate all the necessary
            //distances and hold the remainder as infinity
            final double[][] edgeArray = new double[toAdd.size()][];
            edgeArray[0] = null;
            for(int i = 1; i < toAdd.size(); i++){
                edgeArray[i] = new double[i];
                for(int j = 0; j < i; j++){
                    edgeArray[i][j] = Double.POSITIVE_INFINITY;
                }
            }

            return graph;
        }

    }
}
