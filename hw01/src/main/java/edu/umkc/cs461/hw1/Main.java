package edu.umkc.cs461.hw1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import edu.umkc.cs461.hw1.data.*;
import edu.umkc.cs461.hw1.algorithms.*;

public class Main {
    private static final String CITIES_LIST    = "/Users/thomasjones/Workspace/kccs461/hw01/data/coordinates.csv";
    private static final String ADJACENCY_LIST = "/Users/thomasjones/Workspace/kccs461/hw01/data/Adjacencies.txt";

    private static final int MAX_ITERATIONS = 10000;


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
        final int maxIterations = 10000;

        System.out.println("Finding route from " + cities.get(startIdx).getName() + " to " + cities.get(endIdx).getName());

        System.out.println("BFS: ");
        runSearch(new BreadthFirst(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        
        System.out.println("Flexi BFS: ");
        FlexiSearch fs = new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph);
        final Queue<SearchState.Node> bfFrontier = new ArrayDeque<SearchState.Node>();
        Frontier<SearchState.Node> bfStyleFrontier = new Frontier<SearchState.Node>(
            (srchState) -> {bfFrontier.clear();return null;},
            (srchState, nodes) -> {bfFrontier.addAll(nodes);return null;},
            (srchState) -> bfFrontier.remove(),
            (srchState) -> bfFrontier.isEmpty()
        );

        runSearch(fs, bfStyleFrontier, false);


        // FlexiSearch fsBF = new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph);
        // {
        //     SearchState.FindResult findResult = null;

        //     final Queue<SearchState.Node> bfFrontier = new ArrayDeque<SearchState.Node>();
        //     Frontier<SearchState.Node> bfStyleFrontier = new Frontier<SearchState.Node>(
        //         (srchState) -> {bfFrontier.clear();return null;},
        //         (srchState, nodes) -> {bfFrontier.addAll(nodes);return null;},
        //         (srchState) -> bfFrontier.remove(),
        //         (srchState) -> bfFrontier.isEmpty()
        //     );
    

        //     final long bfStart = System.currentTimeMillis();
        //     for(int i = 0; i < maxIterations; i++){
        //         findResult = fsBF.find(false, bfStyleFrontier);
        //     }
        //     final long bfEnd = System.currentTimeMillis();

        //     List<City> bfpath = findResult.routes.get(0);

        //     System.out.println("Time: " + (bfEnd - bfStart) + "ms");
        //     System.out.println("Flexi BFS Path: ");
        //     // System.out.println("Time: " + (bfEnd - bfStart) + "ms");
        //     System.out.println("Distance: " + City.distanceThrough(bfpath));
        //     for(City city : bfpath){
        //         System.out.print(city.getName()+" -> ");
        //     }

        //     System.out.println("VISIT LIST: ");
        //     for(SearchState.Node node : findResult.visitList){
        //         System.out.print(node.city.getName() + " -> ");
        //     }

        //     System.out.println();
        // }


        // DepthFirst df = new DepthFirst(cities.get(startIdx), cities.get(endIdx), graph);
        // {
        //     SearchState.FindResult dFindResult = null;
        //     final long dfStart = System.currentTimeMillis();
        //     for(int i = 0; i < maxIterations; i++){
        //         dFindResult = df.find(false, null);
        //     }
        //     final long dfEnd = System.currentTimeMillis();

        //     List<City> dfpath = dFindResult.routes.get(0);

        //     System.out.println("Time: " + (dfEnd - dfStart) + "ms");
        //     System.out.println("DFS Path: ");
        //     // System.out.println("Time: " + (dfEnd - dfStart) + "ms");
        //     System.out.println("Distance: " + City.distanceThrough(dfpath));
        //     for(City city : dfpath){
        //         System.out.print(city.getName() + " -> ");
        //     }

        //     System.out.println("VISIT LIST: ");
        //     for(SearchState.Node node : dFindResult.visitList){
        //         System.out.print(node.city.getName() + " -> ");
        //     }

        //     System.out.println();
        // }

        // FlexiSearch fsDF = new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph);
        // {
        //     SearchState.FindResult findResult = null;

        //     final Stack<SearchState.Node> dfFrontier = new Stack<SearchState.Node>();
        //     Frontier<SearchState.Node> bfStyleFrontier = new Frontier<SearchState.Node>(
        //         (srchState) -> {dfFrontier.clear();return null;},
        //         (srchState, nodes) -> {for(SearchState.Node node : nodes){dfFrontier.push(node);}return null;},
        //         (srchState) -> dfFrontier.pop(),
        //         (srchState) -> dfFrontier.isEmpty()
        //     );
    

        //     final long fStart = System.currentTimeMillis();
        //     for(int i = 0; i < maxIterations; i++){
        //         findResult = fsDF.find(false, bfStyleFrontier);
        //     }
        //     final long fEnd = System.currentTimeMillis();

        //     List<City> fpath = findResult.routes.get(0);

        //     System.out.println("Time: " + (fEnd - fStart) + "ms");
        //     System.out.println("Flexi BFS Path: ");
        //     // System.out.println("Time: " + (bfEnd - bfStart) + "ms");
        //     System.out.println("Distance: " + City.distanceThrough(fpath));
        //     for(City city : fpath){
        //         System.out.print(city.getName()+" -> ");
        //     }

        //     System.out.println("VISIT LIST: ");
        //     for(SearchState.Node node : findResult.visitList){
        //         System.out.print(node.city.getName() + " -> ");
        //     }

        //     System.out.println();
        //     System.out.println();
        // }


        // IDDFS iddfs = new IDDFS(cities.get(startIdx), cities.get(endIdx), graph);
        // {
        //     SearchState.FindResult iFindResult = null;
            
        //     final long iddfsStart = System.currentTimeMillis();
        //     for(int i = 0; i < maxIterations; i++){
        //         iFindResult = iddfs.find(false);
        //     }
        //     final long iddfsEnd = System.currentTimeMillis();

        //     List<City> iddfsPath = iFindResult.routes.get(0);

        //     System.out.println("Time: " + (iddfsEnd - iddfsStart) + "ms");
        //     System.out.println("IDDFS Path: ");
        //     System.out.println("Distance: " + City.distanceThrough(iddfsPath));
        //     for(City city : iddfsPath){
        //         System.out.print(city.getName() + " -> ");
        //     }
        //     System.out.println("VISIT LIST: ");
        //     for(SearchState.Node node : iFindResult.visitList){
        //         System.out.print(node.city.getName() + " -> ");
        //     }

        //     System.out.println();
        // }

        // AStar astar = new AStar(cities.get(startIdx), cities.get(endIdx), graph);
        // {
        //     SearchState.FindResult aFindResult = null;
        //     final long astarStart = System.currentTimeMillis();
        //     for(int i = 0; i < maxIterations; i++){
        //         aFindResult = astar.find(false);
        //     }
        //     final long astarEnd = System.currentTimeMillis();

        //     List<City> astarPath = aFindResult.routes.get(0);

        //     System.out.println("Time: " + (astarEnd - astarStart) + "ms");
        //     System.out.println("A* Path: ");
        //     System.out.println("Distance: " + City.distanceThrough(astarPath));
        //     for(City city : astarPath){
        //         System.out.print(city.getName() + " -> ");
        //     }
        //     System.out.println("VISIT LIST: ");
        //     for(SearchState.Node node : aFindResult.visitList){
        //         System.out.print(node.city.getName() + " -> ");
        //     }

        //     System.out.println();
        // }

        // FlexiSearch astarFlex = new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph);
        // {
        //     SearchState.FindResult findResult = null;

        //     final City end = astarFlex.getEnd();

        //     PriorityQueue<SearchState.Node> queue = new PriorityQueue<SearchState.Node>((n1, n2) -> {
        //         //f(n)         =       g(n)    +      h(n)
        //         //total cost   =   distance to node + distance from node to goal
        //         double n1Value = SearchState.costFromStart(n1) + n1.city.distanceFrom(end);
        //         double n2Value = SearchState.costFromStart(n2) + n2.city.distanceFrom(end);
        //         return Double.compare(n1Value, n2Value);
        //     });

        //     Frontier<SearchState.Node> bfStyleFrontier = new Frontier<SearchState.Node>(
        //         (srchState) -> {queue.clear();return null;},
        //         (srchState, nodes) -> {queue.addAll(nodes);return null;},
        //         (srchState) -> queue.remove(),
        //         (srchState) -> queue.isEmpty()
        //     );
    

        //     final long fStart = System.currentTimeMillis();
        //     for(int i = 0; i < maxIterations; i++){
        //         findResult = astarFlex.find(false, bfStyleFrontier);
        //     }
        //     final long fEnd = System.currentTimeMillis();

        //     List<City> fpath = findResult.routes.get(0);

        //     System.out.println("Time: " + (fEnd - fStart) + "ms");
        //     System.out.println("Flexi A* Path: ");
        //     // System.out.println("Time: " + (bfEnd - bfStart) + "ms");
        //     System.out.println("Distance: " + City.distanceThrough(fpath));
        //     for(City city : fpath){
        //         System.out.print(city.getName()+" -> ");
        //     }

        //     System.out.println("VISIT LIST: ");
        //     for(SearchState.Node node : findResult.visitList){
        //         System.out.print(node.city.getName() + " -> ");
        //     }

        //     System.out.println();
        // }


        // if( false ){
        //     {
        //         List<List<City>> allbfroutes = bf.find(false,true);

        //         Map<Double, List<City>> shortestRoutes = new java.util.TreeMap<>();
        //         for(List<City> route : allbfroutes){
        //             shortestRoutes.putIfAbsent(City.distanceThrough(route), route);
        //         }

        //         System.out.println("\nBFS All Routes: ");
        //         int routeCount = 0;
        //         for(Map.Entry<Double, List<City>> entry : shortestRoutes.entrySet()){
        //             System.out.println("\n\nRoute " + routeCount + " Distance: " + entry.getKey());
        //             for(City city : entry.getValue()){
        //                 System.out.print(city.getName() + " -> ");
        //             }
        //             routeCount++;
        //         }
        //     }
        //     {
                // SearchState.FindResult alldfroutes = df.find(false,true);

                // Map<Double, List<City>> shortestRoutes = new java.util.TreeMap<>();
                // for(List<City> route : alldfroutes.routes){
                //     shortestRoutes.putIfAbsent(City.distanceThrough(route), route);
                // }

                // System.out.println("\nDFS All Routes: ");
                // int routeCount = 0;
                // for(Map.Entry<Double, List<City>> entry : shortestRoutes.entrySet()){
                //     System.out.println("\n\nRoute " + routeCount + " Distance: " + entry.getKey());
                //     for(City city : entry.getValue()){
                //         System.out.print(city.getName() + " -> ");
                //     }
                //     routeCount++;
                // }

                // SearchState.FindResult allroutes = iddfs.find(false,true);

                // Map<Double, List<City>> shortestRoutes = new java.util.TreeMap<>();
                // for(List<City> route : allroutes.routes){
                //     shortestRoutes.putIfAbsent(City.distanceThrough(route), route);
                // }

                // System.out.println("IDDFS All Routes: ");
                // int routeCount = 0;
                // for(Map.Entry<Double, List<City>> entry : shortestRoutes.entrySet()){
                //     System.out.println("\n\nRoute " + routeCount + " Distance: " + entry.getKey());
                //     for(City city : entry.getValue()){
                //         System.out.print(city.getName() + " -> ");
                //     }
                //     routeCount++;
                // }


                // SearchState.FindResult allStarroutes = astar.find(false,true);

                // System.out.println("A* All Routes: ");
                // for(List<City> route : allStarroutes.routes){
                //     System.out.println("Distance: " + City.distanceThrough(route));
                //     for(City city : route){
                //         System.out.print(city.getName() + " -> ");
                //     }
                //     System.out.println();
                // }




                //     }
        // }
    }

    private static void runSearch(SearchState searchAlg, Frontier<SearchState.Node> frontier, boolean findAllRoutes)
    {
        SearchState.FindResult findResult = null;

        if(findAllRoutes){
            findResult = searchAlg.find(true, frontier);
        }else{
            final long bfStart = System.currentTimeMillis();
            for(int i = 0; i < MAX_ITERATIONS; i++){
                findResult = searchAlg.find(false);
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
