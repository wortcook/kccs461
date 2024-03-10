package edu.umkc.cs461.hw1;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umkc.cs461.hw1.data.*;
import edu.umkc.cs461.hw1.algorithms.*;

public class Main {
    private static final String CITIES_LIST    = "/Users/wortcook/Workspace/kccs461/hw01/data/coordinates.csv";
    private static final String ADJACENCY_LIST = "/Users/wortcook/Workspace/kccs461/hw01/data/Adjacencies.txt";

    private static final int MAX_ITERATIONS = 100000;


    public static void main(String[] args) {

        boolean findAllRoutes = false;

        if(args.length < 3){
            System.out.println("Must enter at least a start city and end city and an output file name.  Optionally, add 'findAll' at the end to find all routes.");
            System.exit(1);
        }else if(args.length == 4){
            findAllRoutes = args[3].equals("findAll");
        }

        final String startCity = args[0];
        final String endCity = args[1];

        if(startCity.equals(endCity)){
            System.out.println("Start and end cities cannot be same.");
            System.exit(1);
        }

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
        // for(City city : cities){
        //     System.out.println("Connections for " + city.getName());
        //     Map<City,Double> connections = graph.getConnections(city);
        //     for(Map.Entry<City,Double> entry : connections.entrySet()){
        //         System.out.println("\t" + entry.getKey().getName() + " : " + entry.getValue());
        //     }
        // }

        // final int startIdx = cities.size()-1;
        // final int endIdx   = 0;
        final int startIdx = graph.getIndex(new City(startCity));

        if(startIdx == -1){
            System.out.println("Start city not found.");
            System.exit(1);
        }

        final int endIdx = graph.getIndex(new City(endCity));
        if(endIdx == -1){
            System.out.println("End city not found.");
            System.exit(1);
        }

        try(FileWriter jsonOutput = new FileWriter(args[2])){
            System.out.println("Finding route from " + cities.get(startIdx).getName() + " to " + cities.get(endIdx).getName());

            jsonOutput.write("{");
            jsonOutput.write("\"start\":\""+cities.get(startIdx).getName()+"\",");
            jsonOutput.write("\"end\":\""+cities.get(endIdx).getName()+"\",");
            jsonOutput.write("\"findAllPaths\":"+findAllRoutes+",");
            jsonOutput.write("\"searches\":[");

            jsonOutput.write("{\"name\":\"BSF\",");
            System.out.println("BFS: ");
            runSearch(new BreadthFirst(cities.get(startIdx), cities.get(endIdx), graph), null, findAllRoutes, jsonOutput);
            jsonOutput.write("},");
    
            jsonOutput.write("{\"name\":\"FlexBSF\",");
            System.out.println("Flexi BFS: ");
            runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new QueueFrontier(), findAllRoutes, jsonOutput);
            jsonOutput.write("},");
    
            jsonOutput.write("{\"name\":\"DSF\",");
            System.out.println("DFS: ");
            runSearch(new DepthFirst(cities.get(startIdx), cities.get(endIdx), graph), null, findAllRoutes, jsonOutput);
            jsonOutput.write("},");
    
            jsonOutput.write("{\"name\":\"FlexDSF\",");
            System.out.println("Flexi DFS: ");
            runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new StackFrontier(), findAllRoutes, jsonOutput);
            jsonOutput.write("},");
    
            jsonOutput.write("{\"name\":\"IDDSF\",");
            System.out.println("IDDFS: ");
            runSearch(new IDDFS(cities.get(startIdx), cities.get(endIdx), graph), null, findAllRoutes, jsonOutput);
            jsonOutput.write("},");
    
            jsonOutput.write("{\"name\":\"FlexiIDDSF\",");
            System.out.println("Flexi IDDFS: ");
            runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new IDDFSFrontier(), findAllRoutes, jsonOutput);
            jsonOutput.write("},");
    
            jsonOutput.write("{\"name\":\"BestFirst\",");
            System.out.println("Best First: ");
            runSearch(new BestFirst(cities.get(startIdx), cities.get(endIdx), graph), null, findAllRoutes, jsonOutput);
            jsonOutput.write("},");
    
            jsonOutput.write("{\"name\":\"FlexiBestFirst\",");
            System.out.println("Flexi Best First:");
            runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new BestFirstFrontier(cities.get(endIdx)), findAllRoutes, jsonOutput);
            jsonOutput.write("},");
    
            jsonOutput.write("{\"name\":\"AStar\",");
            System.out.println("A*: ");
            runSearch(new AStar(cities.get(startIdx), cities.get(endIdx), graph), null, findAllRoutes, jsonOutput);
            jsonOutput.write("},");
    
            jsonOutput.write("{\"name\":\"FlexAStar\",");
            System.out.println("Flexi A*");
            runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new AStarFrontier(cities.get(endIdx)), findAllRoutes, jsonOutput);
            jsonOutput.write("},");
    
            jsonOutput.write("}");


            jsonOutput.flush();

            // System.out.println("Beam Search: ");
            // runSearch(new FlexiSearch(cities.get(startIdx), cities.get(endIdx), graph), new BeamFrontier(), findAllRoutes);
    
        }catch(IOException writeError){
            //
        }
    }

    private static void runSearch(SearchState searchAlg, Frontier<SearchState.Node> frontier, boolean findAllRoutes, Writer fileOut)
    throws IOException
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
            fileOut.write("\"iterations\":"+MAX_ITERATIONS+",");
            fileOut.write("\"time\":"+(bfEnd-bfStart)+",");
            System.out.println("Time: " + (bfEnd - bfStart) + "ms for " + MAX_ITERATIONS + " iterations");
        }


        List<City> path = findResult.routes.get(0);

        System.out.println("First Route: ");
        fileOut.write("\"firstRouteFound\":[");
        fileOut.write(String.join(",", path.stream().map(c -> "\""+c.getName()+"\"").collect(Collectors.toList())));
        fileOut.write("],");

        System.out.println("Distance: " + City.distanceThrough(path));

        System.out.println(String.join(" -> ", path.stream().map(c -> c.getName()).collect(Collectors.toList())));

        // for(City city : path){
        //     System.out.print(city.getName() + " -> ");
        // }
        System.out.println();
        System.out.println("VISIT LIST: ");
        System.out.println(String.join(" -> ", findResult.visitList.stream().map(n -> n.city.getName()).collect(Collectors.toList())));

        if(findAllRoutes){
            System.out.println();
            System.out.println("All Routes: ");
            for(List<City> route : findResult.routes){
                System.out.println("Distance: " + City.distanceThrough(route));
                System.out.println("Route: ");
                System.out.println(String.join(" -> ", route.stream().map(c -> c.getName()).collect(Collectors.toList())));
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
