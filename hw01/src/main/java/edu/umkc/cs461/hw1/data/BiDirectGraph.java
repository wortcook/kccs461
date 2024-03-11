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
    private final Map<K, Integer> nodeMap = new HashMap<K, Integer>();

    //Map of index values to nodes
    private final Map<Integer, K> reverseNodeMap = new HashMap<Integer, K>();

    //The edges of the graph, i.e. distances or infinity if not connected
    private final double[][] edges;

    //Memoization of the connections. We'll store connections here once requested
    private final Map<K, Map<K,Double>> connections = new HashMap<K, Map<K,Double>>();

    //Constructor is package private so that only the builder can create it
    /*package*/ BiDirectGraph(final  Map<K, Integer> nodeMap, final double[][] edges){
        this.nodeMap.putAll(nodeMap);

        //We need to reverse the map so we can look up the nodes by index
        for(Map.Entry<K,Integer> entry : nodeMap.entrySet()){
            this.reverseNodeMap.put(entry.getValue(), entry.getKey());
        }

        this.edges = edges;
    }

    /*
     * Get the connections for a given node
     * @param node the node to get the connections for
     */
    public Map<K,Double> getConnections(final K node){

        //memoization
        if(this.connections.containsKey(node)){
            return this.connections.get(node);
        }

        //Get the index of the node
        int index = this.nodeMap.get(node);

        //Create the return map
        Map<K,Double> ret = new HashMap<K,Double>();

        //If the index is 0 then we only need to look at the row
        //if the index is greater than 0 then we need to look at the row and the column

        //Column scan
        if(index>0){
            //We only need to look at the lower triangle of the matrix
            double[] indexRow = edges[index];

            for(int i = 0 ; i < indexRow.length; i++){
                if(indexRow[i] != Double.POSITIVE_INFINITY){
                    ret.put( this.reverseNodeMap.get(i), indexRow[i]);
                }
            }
        }

        //Row scan
        for(int i = index + 1; i < edges.length; i++){
            if(edges[i][index] != Double.POSITIVE_INFINITY){
                ret.put( this.reverseNodeMap.get(i), edges[i][index]);
            }
        }

        this.connections.put(node, ret);

        return ret;
    }

    /*
     * Get the number of nodes in the graph
     * @return The number of nodes in the graph
     */
    public int getNodeCount(){
        return this.nodeMap.size();
    }

    /*
     * Get the index of a node
     * @param node The node to get the index of
     * @return The index of the node
     */
    public int getIndex(final K node){
        if(null == node){
            return -1;
        }else if(!this.nodeMap.containsKey(node)){
            return -1;
        }
        return this.nodeMap.get(node);
    }

    /*
     * Graph builder class
     */
    public static class BiDirectGraphBuilder<K extends Comparable<K> & Measureable<K>>{

        private ArrayList<K> toAdd = new ArrayList<K>();
        private List<NodePair<K>> edges = new ArrayList<NodePair<K>>();

        /*
         * Constructor for the graph builder
         */
        public BiDirectGraphBuilder(){
        }

        /*
         * Add a list of nodes to the graph
         * @param nodes The nodes to add
         * @return The builder
         */
        public BiDirectGraphBuilder<K> addNodes(List<K> nodes){
            this.toAdd.addAll(nodes);
            return this;
        }


        /*
         * Add a node to the graph
         * @param node The node to add
         * @return The builder
         */
        public BiDirectGraphBuilder<K> addNode(K node){
            this.toAdd.add(node);
            return this;
        }

        /*
         * Add a list of edges to the graph
         * @param edges The edges to add
         */
        public BiDirectGraphBuilder<K> addEdge(NodePair<K> edge){
            this.edges.add(edge);
            return this;
        }

        /*
         * Add a list of edges to the graph
         * @param edges The edges to add
         */
        public BiDirectGraphBuilder<K> addEdges(List<NodePair<K>> edges){
            this.edges.addAll(edges);
            return this;
        }

        /*
         * Build the graph based on the nodes and edges added
         */
        public BiDirectGraph<K> build(){
            this.toAdd.sort(null);

            //Puts the list of nodes so we can map city name to array position
            Map<K, Integer> indexMap = new HashMap<K, Integer>();

            //the following auto suggested by co-pilot and slightly edited
            for(int i = 0; i < toAdd.size(); i++){
                indexMap.put(toAdd.get(i), i);
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
                int a = indexMap.get(edge.getANode());
                int b = indexMap.get(edge.getBNode());

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

            return new BiDirectGraph<>(indexMap, edgeArray);
        }
    }
}
