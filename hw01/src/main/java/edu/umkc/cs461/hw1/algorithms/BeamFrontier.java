package edu.umkc.cs461.hw1.algorithms;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Stack;
import java.util.Queue;

/*
 * A frontier attempts to emulate the behavior of a beam search algorithm.
 * Experimental
 */
public class BeamFrontier implements Frontier<SearchState.Node>{
    private final Stack<SearchState.Node> stack = new Stack<>();
    private final Queue<SearchState.Node> queue = new ArrayDeque<>();
    // private final PriorityQueue<SearchState.Node> pqueue;

    public BeamFrontier(){}

    public void init(final SearchState searchState){
        stack.clear();
        queue.clear();
    }

    public void add(SearchState srchState, List<SearchState.Node> nodes){
        if(nodes.size() == 1){
            stack.push(nodes.get(0));
        }else if(nodes.size() >=2){
            stack.push(nodes.get(1));
            stack.push(nodes.get(0));
        }else if(nodes.size()>2){
            queue.addAll(nodes.reversed().subList(0, nodes.size()-2));
        }
        printStackAnQueue();        
    }

    public SearchState.Node pull(SearchState srchState){
        try{
            if(stack.isEmpty()){
                stack.addAll(queue);
            }
            return stack.pop();
        }finally{
            printStackAnQueue();
        }
    }
    public boolean isEmpty(SearchState srchState){
        return stack.isEmpty() && queue.isEmpty();
    }


    private void printStackAnQueue(){
        // System.out.print("S:");
        // for(SearchState.Node n: stack){
        //     System.out.print(n.city.getName() + " ");
        // }
        // System.out.println();
        // System.out.print("Q: ");
        // for(SearchState.Node n: queue){
        //     System.out.print(n.city.getName() + " ");
        // }
        // System.out.println();
    }
}
