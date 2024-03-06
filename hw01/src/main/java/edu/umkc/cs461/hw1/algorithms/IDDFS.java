package edu.umkc.cs461.hw1.algorithms;

import java.util.List;

import edu.umkc.cs461.hw1.data.BiDirectGraph;
import edu.umkc.cs461.hw1.data.City;


public class IDDFS extends SearchState {
    public IDDFS(final City start, final City end, final BiDirectGraph<City> graph) {
        super(start, end, graph);
    }

    @Override
    public List<City> findFirstRoute() {
        //iterative deepening depth first search
        int depth = 0;
        while (true) {
            List<City> result = recursiveDLS(getStart(), getEnd(), depth);
            if (result != null) {
                return result;
            }
            depth++;
        }
    }

    @Override
    public List<List<City>> findAllRoutes() {
        return null;
    }

    private List<City> recursiveDLS(City start, City end, int depth) {
        if (depth == 0 && start.equals(end)) {
            List<City> path = new java.util.ArrayList<City>();
            path.add(start);
            return path;
        } else if (depth > 0) {
            for (City neighbor : getGraph().getConnections(start).keySet()) {
                List<City> result = recursiveDLS(neighbor, end, depth - 1);
                if (result != null) {
                    result.add(0, start);
                    return result;
                }
            }
        }
        return null;
    }
}
