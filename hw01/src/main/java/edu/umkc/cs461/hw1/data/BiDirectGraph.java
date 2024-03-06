package edu.umkc.cs461.hw1.data;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


/**
 * A bi-directional graph that is used to store the distances between nodes
 * The graph is read-only once the builder has created it.
 * 
 */
public class BiDirectGraph<K extends Comparable<K> & Measureable<K>>{
    //Map of nodes to index values.
    final Map<K, Integer> nodeMap = new HashMap<K, Integer>();

    //Map of index values to nodes
    final Map<Integer, K> reverseNodeMap = new HashMap<Integer, K>();

    //The edges of the graph, i.e. distances or infinity if not connected
    final double[][] edges;


    //Memoization of the connections. We'll store connections here once requested
    final Map<K, Map<K,Double>> connections = new HashMap<K, Map<K,Double>>();


    //Constructor is package private so that only the builder can create it
    /*package*/ BiDirectGraph(final  Map<K, Integer> nodeMap, final double[][] edges){
        this.nodeMap.putAll(nodeMap);

        //We need to reverse the map so we can look up the nodes by index
        for(Map.Entry<K,Integer> entry : nodeMap.entrySet()){
            this.reverseNodeMap.put(entry.getValue(), entry.getKey());
        }

        this.edges = edges;
    }

    public Map<K,Double> getConnections(final K node){

        //memoization
        if(this.connections.containsKey(node)){
            return this.connections.get(node);
        }

        int index = this.nodeMap.get(node);

        Map<K,Double> ret = new HashMap<K,Double>();

        if(index>0){
            double[] indexRow = edges[index];
            for(int i = 0 ; i < indexRow.length; i++){
                if(indexRow[i] != Double.POSITIVE_INFINITY){
                    ret.put( this.reverseNodeMap.get(i), indexRow[i]);
                }
            }
        }

        for(int i = index + 1; i < edges.length; i++){
            if(edges[i][index] != Double.POSITIVE_INFINITY){
                ret.put( this.reverseNodeMap.get(i), edges[i][index]);
            }
        }

        this.connections.put(node, ret);

        return ret;
    }


    public static class BiDirectGraphBuilder<K extends Comparable<K> & Measureable<K>>{

        private ArrayList<K> toAdd = new ArrayList<K>();
        private List<NodePair<K>> edges = new ArrayList<NodePair<K>>();

        public BiDirectGraphBuilder(){
        }

        public BiDirectGraphBuilder<K> addNodes(List<K> nodes){
            this.toAdd.addAll(nodes);
            return this;
        }


        public BiDirectGraphBuilder<K> addNode(K node){
            this.toAdd.add(node);
            return this;
        }

        public BiDirectGraphBuilder<K> addEdge(NodePair<K> edge){
            this.edges.add(edge);
            return this;
        }

        public BiDirectGraphBuilder<K> addEdges(List<NodePair<K>> edges){
            this.edges.addAll(edges);
            return this;
        }

        public BiDirectGraph<K> build(){
            this.toAdd.sort(null);

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
