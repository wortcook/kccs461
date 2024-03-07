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
    private static final String CITIES_LIST    = "/Users/wortcook/Workspace/kccs461/hw01/data/coordinates.csv";
    private static final String ADJACENCY_LIST = "/Users/wortcook/Workspace/kccs461/hw01/data/Adjacencies.txt";

    private static final int MAX_ITERATIONS = 100000;


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
        FlexiSearch fsbfs = new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph);
        final Queue<SearchState.Node> bfFrontier = new ArrayDeque<SearchState.Node>();
        Frontier<SearchState.Node> bfStyleFrontier = new Frontier<SearchState.Node>(
            (srchState) -> {bfFrontier.clear();return null;},
            (srchState, nodes) -> {bfFrontier.addAll(nodes);return null;},
            (srchState) -> bfFrontier.remove(),
            (srchState) -> bfFrontier.isEmpty()
        );

        runSearch(fsbfs, bfStyleFrontier, false);

        System.out.println("DFS: ");
        runSearch(new DepthFirst(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        System.out.println("Flexi DFS: ");
        FlexiSearch fsdfs = new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph);
        final Stack<SearchState.Node> dfFrontier = new Stack<SearchState.Node>();
        Frontier<SearchState.Node> dfStyleFrontier = new Frontier<SearchState.Node>(
            (srchState) -> {dfFrontier.clear();return null;},
            (srchState, nodes) -> {for(SearchState.Node node : nodes){dfFrontier.push(node);}return null;},
            (srchState) -> dfFrontier.pop(),
            (srchState) -> dfFrontier.isEmpty()
        );

        runSearch(fsdfs, dfStyleFrontier, false);


        System.out.println("IDDFS: ");
        runSearch(new IDDFS(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        System.out.println("Flexi IDDFS: ");
        FlexiSearch fsiddfs = new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph);

        final List<SearchState.Node> iddfsFrontier = new LinkedList<SearchState.Node>();
        Frontier<SearchState.Node> iddfsStyleFrontier = new Frontier<SearchState.Node>(
            (srchState) -> {iddfsFrontier.clear();return null;},
            (srchState, nodes) -> {
                List<SearchState.Node> addAsStack = new ArrayList<>();
                List<SearchState.Node> addAsQueue = new ArrayList<>();

                for(SearchState.Node node : nodes){
                    int nodeDepth = node.findDepth();

                    if(nodeDepth % 3 == 0){
                        addAsQueue.add(node);
                    }else{
                        addAsStack.add(node);
                    }
                }
                iddfsFrontier.addAll(addAsQueue);
                iddfsFrontier.addAll(0, addAsStack);

                return null;
            },
            (srchState) -> iddfsFrontier.remove(0),
            (srchState) -> iddfsFrontier.isEmpty()
        );

        runSearch(fsiddfs, iddfsStyleFrontier, false);

        runSearch(new AStar(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        FlexiSearch fsastar = new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph);
        final PriorityQueue<SearchState.Node> astarFrontier = new PriorityQueue<SearchState.Node>((n1, n2) -> {
            //f(n)         =       g(n)    +      h(n)
            //total cost   =   distance to node + distance from node to goal
            double n1Value = SearchState.costFromStart(n1) + n1.city.distanceFrom(fsastar.getEnd());
            double n2Value = SearchState.costFromStart(n2) + n2.city.distanceFrom(fsastar.getEnd());
            return Double.compare(n1Value, n2Value);
        });

        Frontier<SearchState.Node> astarStyleFrontier = new Frontier<SearchState.Node>(
            (srchState) -> {astarFrontier.clear();return null;},
            (srchState, nodes) -> {astarFrontier.addAll(nodes);return null;},
            (srchState) -> astarFrontier.remove(),
            (srchState) -> astarFrontier.isEmpty()
        );

        runSearch(fsastar, astarStyleFrontier, false);
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
