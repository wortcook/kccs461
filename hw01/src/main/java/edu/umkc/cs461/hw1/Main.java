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

        BreadthFirst bf = new BreadthFirst(cities.get(0), cities.get(cities.size()-1), graph);

        List<City> path = bf.findRoute();

        System.out.println("Path: ");
        for(City city : path){
            System.out.println(city.getName());
        }

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
