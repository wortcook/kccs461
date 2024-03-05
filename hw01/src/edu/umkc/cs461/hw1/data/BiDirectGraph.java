package edu.umkc.cs461.hw1.data;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.google.common.collect.BiMap;



public class BiDirectGraph<K extends Comparable<K> & Measureable<K>>{
    final BiMap<K, Integer> nodeMap = new HashMap<K, Integer>();
    final BiMap<Integer, K> reverseNodeMap = new HashMap<Integer, K>();
    double[][] edges;

    //We assume nodes is sorted
    protected BiDirectGraph(final  Map<K, Integer> nodeMap, final double[][] edges){
        this.nodeMap.putAll(nodeMap);
        this.edges = edges;
    }

    public Map<K,Double> getConnections(final K node){
        int index = this.nodeMap.get(node);
        Map<K,Double> ret = new HashMap<K,Double>();

        double[] indexRow = edges[index];
        for(int i = 0 ; i < indexRow.length; i++){
            if(indexRow[i] != Double.POSITIVE_INFINITY){
                ret.put( this.reverseNodeMap.get(i), indexRow[i]);
            }
        }

        for(int i = index + 1; i < edges.length; i++){
            if(edges[i][index] != Double.POSITIVE_INFINITY){
                ret.put( this.reverseNodeMap.get(i), edges[i][index]);
            }
        }

        return ret;
    }



    public static class BiDirectGraphBuilder<K extends Comparable<K> & Measureable<K>>{

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
            toAdd.sort(null);

            //Puts the list of nodes so we can map city name to array position
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
                    //Disconnected nodes are infinity away
                    edgeArray[i][j] = Double.POSITIVE_INFINITY;
                }
            }

            //Now handle the edges and set the distances
            for(NodePair<K> edge : edges){
                int a = nodeMap.get(edge.getANode());
                int b = nodeMap.get(edge.getBNode());

                //We need to swap so that a is the smaller number
                //we make the assumption here that the nodes are
                //comparable and that the comparison is consistent
                if(b > a){
                    int temp = a;
                    a = b;
                    b = temp;
                }

                double distance = edge.getANode().distanceFrom(edge.getBNode());

                //Set the distance, we only set one side of the distances
                //since the graph is bi-directional
                edgeArray[a][b] = distance;
            }

            return new BiDirectGraph<>(nodeMap, edgeArray);
        }

    }
}
