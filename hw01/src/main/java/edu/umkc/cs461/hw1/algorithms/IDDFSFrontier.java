package edu.umkc.cs461.hw1.algorithms;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IDDFSFrontier implements Frontier<SearchState.Node>{
    final List<SearchState.Node> activeFrontier = new LinkedList<SearchState.Node>();
    final List<SearchState.Node> backQueue = new LinkedList<SearchState.Node>();

    public void init(final SearchState searchState){
        activeFrontier.clear();
        backQueue.clear();
    }

    public void add(SearchState srchState, List<SearchState.Node> nodes){
        List<SearchState.Node> addAsStack = new ArrayList<>();

        for(SearchState.Node node : nodes){
            int nodeDepth = node.findDepth();

            if(nodeDepth % 2 == 0){
                backQueue.add(node);
            }else{
                addAsStack.add(node);
            }
        }
        // System.out.println("Adding " + addAsQueue.size() + " to queue and " + addAsStack.size() + " to stack");
        for(SearchState.Node node : addAsStack.reversed()){activeFrontier.add(0, node);}

        //Print out the frontier
        // System.out.println();
        // System.out.print("F:");
        // for(SearchState.Node node : activeFrontier){
        //     System.out.print(node.city.getName() + "(" + node.findDepth()+"), ");
        // }
        // System.out.println();
        // System.out.print("Q:");
        // for(SearchState.Node node : backQueue){
        //     System.out.print(node.city.getName() + "(" + node.findDepth()+"), ");
        // }
        // System.out.println();
    }

    public SearchState.Node pull(SearchState srchState){
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
