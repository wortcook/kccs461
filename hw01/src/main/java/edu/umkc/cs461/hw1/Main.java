package edu.umkc.cs461.hw1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.umkc.cs461.hw1.data.*;
import edu.umkc.cs461.hw1.algorithms.*;

public class Main {
    private static final String CITIES_LIST    = "/Users/wortcook/Workspace/kccs461/hw01/data/coordinates.csv";
    private static final String ADJACENCY_LIST = "/Users/wortcook/Workspace/kccs461/hw01/data/Adjacencies.txt";

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
        // final int startIdx = 22;
        // final int endIdx   = 23;

        System.out.println("Finding route from " + cities.get(startIdx).getName() + " to " + cities.get(endIdx).getName());

        System.out.println("BFS: ");
        runSearch(new BreadthFirst(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        System.out.println("Flexi BFS: ");
        runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new QueueFrontier(), false);

        System.out.println("DFS: ");
        runSearch(new DepthFirst(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        System.out.println("Flexi DFS: ");
        runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new StackFrontier(), true);

        System.out.println("IDDFS: ");
        runSearch(new IDDFS(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        System.out.println("Flexi IDDFS: ");
        runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new IDDFSFrontier(), false);

        System.out.println("Best First: ");
        runSearch(new BestFirst(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        System.out.println("Flexi Best First:");
        runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new BestFirstFrontier(cities.get(endIdx)), false);

        System.out.println("A*: ");
        runSearch(new AStar(cities.get(startIdx), cities.get(endIdx), graph), null, false);

        System.out.println("Flexi A*");
        runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new AStarFrontier(cities.get(endIdx)), false);

        System.out.println("Beam Search: ");
        runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new BeamFrontier(), true);

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
            System.out.println();
            System.out.println("All Routes: ");
            for(List<City> route : findResult.routes){
                System.out.println("Distance: " + City.distanceThrough(route));
                System.out.println("Route: ");
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
