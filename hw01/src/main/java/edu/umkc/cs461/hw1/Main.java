package edu.umkc.cs461.hw1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import edu.umkc.cs461.hw1.data.*;
import edu.umkc.cs461.hw1.algorithms.*;

public class Main {
    private static final String CITIES_LIST    = "/Users/thomasjones/Workspace/kccs461/hw01/data/coordinates.csv";
    private static final String ADJACENCY_LIST = "/Users/thomasjones/Workspace/kccs461/hw01/data/Adjacencies.txt";

    private static final int MAX_ITERATIONS = 1;


    public static void main(String[] args) {
        System.out.println("Loading data...");

        //Load the data
        final List<City> cities = DataLoader.loadCities(CITIES_LIST);

        final List<NodePair<City>> adjacencies = DataLoader.loadAdjacencies(ADJACENCY_LIST, cities);

        BiDirectGraph.BiDirectGraphBuilder<City> builder = new BiDirectGraph.BiDirectGraphBuilder<City>();
        builder.addNodes(cities);
        builder.addEdges(adjacencies);

        BiDirectGraph<City> graph = builder.build();

        System.out.println("Data loaded.");

        //Print out the connections
        for(City city : cities){
            System.out.println("Connections for " + city.getName());
            Map<City,Double> connections = graph.getConnections(city);
            for(Map.Entry<City,Double> entry : connections.entrySet()){
                System.out.println("\t" + entry.getKey().getName() + " : " + entry.getValue());
            }
        }

        final int startIdx = cities.size()-1;
        final int endIdx   = 0;
        // final int startIdx = 0;
        // final int endIdx   = cities.size()-1;

        System.out.println("Finding route from " + cities.get(startIdx).getName() + " to " + cities.get(endIdx).getName());

        System.out.println("BFS: ");
        runSearch(new BreadthFirst(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        System.out.println("Flexi BFS: ");
        runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new QueueFrontier(), false);

        System.out.println("DFS: ");
        runSearch(new DepthFirst(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        System.out.println("Flexi DFS: ");
        runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new StackFrontier(), false);

        System.out.println("IDDFS: ");
        runSearch(new IDDFS(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        System.out.println("Flexi IDDFS: ");
        FlexiSearch fsiddfs = new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph);

        final List<SearchState.Node> iddfsFrontier = new LinkedList<SearchState.Node>();
        final List<SearchState.Node> iddfsFrontierBackQueue = new LinkedList<SearchState.Node>();

        Frontier<SearchState.Node> iddfsStyleFrontier = new Frontier<SearchState.Node>(){
            public void init(SearchState srchState){
                iddfsFrontier.clear();
            }
            public void add(SearchState srchState, List<SearchState.Node> nodes){
                List<SearchState.Node> addAsStack = new ArrayList<>();

                for(SearchState.Node node : nodes){
                    int nodeDepth = node.findDepth();

                    if(nodeDepth % 2 == 0){
                        iddfsFrontierBackQueue.add(node);
                    }else{
                        addAsStack.add(node);
                    }
                }
                // System.out.println("Adding " + addAsQueue.size() + " to queue and " + addAsStack.size() + " to stack");
                for(SearchState.Node node : addAsStack.reversed()){iddfsFrontier.add(0, node);}

                //Print out the frontier
                System.out.println();
                System.out.print("F:");
                for(SearchState.Node node : iddfsFrontier){
                    System.out.print(node.city.getName() + "(" + node.findDepth()+"), ");
                }
                System.out.println();
                System.out.print("Q:");
                for(SearchState.Node node : iddfsFrontierBackQueue){
                    System.out.print(node.city.getName() + "(" + node.findDepth()+"), ");
                }
                System.out.println();
            }
            public SearchState.Node pull(SearchState srchState){
                if(iddfsFrontier.isEmpty()){
                    iddfsFrontier.addAll(iddfsFrontierBackQueue);
                    iddfsFrontierBackQueue.clear();
                }
                return iddfsFrontier.removeFirst();
            }
            public boolean isEmpty(SearchState srchState){
                return iddfsFrontier.isEmpty() && iddfsFrontierBackQueue.isEmpty();
            }
        };
        
        // (
        //     (srchState) -> {iddfsFrontier.clear();return null;},
        //     (srchState, nodes) -> {
        //         List<SearchState.Node> addAsStack = new ArrayList<>();
        //         List<SearchState.Node> addAsQueue = new ArrayList<>();

        //         for(SearchState.Node node : nodes){
        //             int nodeDepth = node.findDepth();

        //             if(nodeDepth % 2 == 0){
        //                 addAsQueue.add(node);
        //             }else{
        //                 addAsStack.add(node);
        //             }
        //         }
        //         // System.out.println("Adding " + addAsQueue.size() + " to queue and " + addAsStack.size() + " to stack");
        //         iddfsFrontier.addAll(addAsQueue);
        //         for(SearchState.Node node : addAsStack.reversed()){iddfsFrontier.add(0, node);}

        //         //Print out the frontier
        //         System.out.println();
        //         for(SearchState.Node node : iddfsFrontier){
        //             System.out.print(node.city.getName() + "(" + node.findDepth()+"), ");
        //         }
        //         System.out.println();

        //         return null;
        //     },
        //     (srchState) -> iddfsFrontier.remove(0),
        //     (srchState) -> iddfsFrontier.isEmpty()
        // );

        System.out.println("Flexi IDDFS: ");
        runSearch(fsiddfs, iddfsStyleFrontier, false);

        System.out.println("Best First: ");
        runSearch(new BestFirst(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        // System.out.println("Flexi Best First: ");
        // FlexiSearch fsbestfirst = new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph);
        // final PriorityQueue<SearchState.Node> bestFirstFrontier = new PriorityQueue<SearchState.Node>((n1, n2) -> {
        //     return Double.compare(n1.city.distanceFrom(fsbestfirst.getEnd()), n2.city.distanceFrom(fsbestfirst.getEnd()));
        // });

        // Frontier<SearchState.Node> bestFirstStyleFrontier = new Frontier<SearchState.Node>(
        //     (srchState) -> {bestFirstFrontier.clear();return null;},
        //     (srchState, nodes) -> {bestFirstFrontier.addAll(nodes);return null;},
        //     (srchState) -> bestFirstFrontier.remove(),
        //     (srchState) -> bestFirstFrontier.isEmpty()
        // );

        // runSearch(fsbestfirst, bestFirstStyleFrontier, false);


        System.out.println("A*: ");
        runSearch(new AStar(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        // System.out.println("Flexi A*: ");
        // FlexiSearch fsastar = new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph);
        // final PriorityQueue<SearchState.Node> astarFrontier = new PriorityQueue<SearchState.Node>((n1, n2) -> {
        //     //f(n)         =       g(n)    +      h(n)
        //     //total cost   =   distance to node + distance from node to goal
        //     double n1Value = SearchState.costFromStart(n1) + n1.city.distanceFrom(fsastar.getEnd());
        //     double n2Value = SearchState.costFromStart(n2) + n2.city.distanceFrom(fsastar.getEnd());
        //     return Double.compare(n1Value, n2Value);
        // });

        // Frontier<SearchState.Node> astarStyleFrontier = new Frontier<SearchState.Node>(
        //     (srchState) -> {astarFrontier.clear();return null;},
        //     (srchState, nodes) -> {astarFrontier.addAll(nodes);return null;},
        //     (srchState) -> astarFrontier.remove(),
        //     (srchState) -> astarFrontier.isEmpty()
        // );

        // runSearch(fsastar, astarStyleFrontier, false);


        //Beam Search
        // System.out.println("Beam Search: ");
        // FlexiSearch fsbeam = new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph);
        // final List<SearchState.Node> beamFrontier = new ArrayList<SearchState.Node>();

        // Frontier<SearchState.Node> beamStyleFrontier = new Frontier<SearchState.Node>(
        //     (srchState) -> {beamFrontier.clear();return null;},
        //     (srchState, nodes) -> {
        //         return null;
        //     },
        //     (srchState) -> beamFrontier.remove(0),
        //     (srchState) -> beamFrontier.isEmpty()
        // );


    }

    private static void runSearch(SearchState searchAlg, Frontier<SearchState.Node> frontier, boolean findAllRoutes)
    {
        SearchState.FindResult findResult = null;

        if(findAllRoutes){
            findResult = searchAlg.find(true, frontier);
        }else{
            final long bfStart = System.currentTimeMillis();
            for(int i = 0; i < MAX_ITERATIONS; i++){
                findResult = searchAlg.find(false, frontier);
            }
            final long bfEnd = System.currentTimeMillis();
            System.out.println("Time: " + (bfEnd - bfStart) + "ms for " + MAX_ITERATIONS + " iterations");
        }

        System.out.println("First Route: ");
        List<City> path = findResult.routes.get(0);
        System.out.println("Distance: " + City.distanceThrough(path));
        for(City city : path){
            System.out.print(city.getName() + " -> ");
        }
        System.out.println();
        System.out.println("VISIT LIST: ");
        for(SearchState.Node node : findResult.visitList){
            System.out.print(node.city.getName() + " -> ");
        }

        if(findAllRoutes){
            System.out.println("All Routes (other than first): ");
            for(List<City> route : findResult.routes){
                System.out.println("Distance: " + City.distanceThrough(route));
                System.out.println("Route: " + route);
                for(City city : route){
                    System.out.print(city.getName() + " -> ");
                }
                System.out.println();
            }
        }
        System.out.println();
        System.out.println();
    }

    private static class DataLoader {
        public static List<City> loadCities(final String filename) {
            List<City> cities = new ArrayList<City>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    cities.add(new City(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2])));
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return cities;
        }

        public static List<NodePair<City>> loadAdjacencies(final String filename, final List<City> cities) {

            Map<String, City> cityMap = new java.util.HashMap<String, City>();
            for(City city : cities){
                cityMap.put(city.getName(), city);
            }

            List<NodePair<City>> adjacencies = new ArrayList<NodePair<City>>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" ");                    
                    adjacencies.add(new NodePair<City>(cityMap.get(parts[0]), cityMap.get(parts[1])));
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return adjacencies;
        }
    }
}
