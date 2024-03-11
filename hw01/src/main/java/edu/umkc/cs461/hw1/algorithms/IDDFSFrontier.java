package edu.umkc.cs461.hw1.algorithms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*
 * A frontier that tries to emulate the behavior of IDDFS by alternating between a stack and a queue.
 */
public class IDDFSFrontier implements Frontier<SearchState.Node>{
    final List<SearchState.Node> activeFrontier = new LinkedList<SearchState.Node>();
    final List<SearchState.Node> backQueue = new LinkedList<SearchState.Node>();

    /*
     * Clears the frontier.
     */
    public void init(final SearchState searchState){
        activeFrontier.clear();
        backQueue.clear();
    }

    /*
     * Adds the passed nodes to the frontier. The nodes are added to the stack if their depth is even and to the queue if their depth is odd.
     * @param srchState the current search state
     * @param nodes the nodes to add to the frontier
     */
    public void add(SearchState srchState, List<SearchState.Node> nodes){
        List<SearchState.Node> addAsStack = new ArrayList<>();

        for(SearchState.Node node : nodes){
            int nodeDepth = node.findDepth();

            //If the depth is even children are added to the back of the queue
            //If the depth is odd children are added to the stack
            //In effect, we alternate between a BFS and a DFS
            //Performing DFS on the even levels and BFS on the odd levels
            //The modulus can be changed to any number to change the depth at which the algorithm switches between BFS and DFS
            //For example, if the modulus is 3, the algorithm will perform DFS for two iterations
            //and then perform BFS for one iteration
            //In other words, it will step forward two steps and then accumulate the next step as a BFS.
            if(nodeDepth % 2 == 0){
                backQueue.add(node);
            }else{
                addAsStack.add(node);
            }
        }
        for(SearchState.Node node : addAsStack.reversed()){activeFrontier.add(0, node);}
    }

    /*
     * Removes and returns the next node from the frontier.
     * @param srchState the current search state
     * @return the next node from the frontier
     */
    public SearchState.Node pull(SearchState srchState){

        //If the active frontier is empty, grab everything from the back queue
        if(activeFrontier.isEmpty()){
            activeFrontier.addAll(backQueue);
            backQueue.clear();
        }
        return activeFrontier.removeFirst();
    }
    public boolean isEmpty(SearchState srchState){
        return activeFrontier.isEmpty() && backQueue.isEmpty();
    }
}
