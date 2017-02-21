import java.util.*;

public class GraphAlgorithms {
    /**
     * Find the shortest distance between the start vertex and all other
     * vertices given a weighted graph.
     * You should use the adjacency list representation of the graph.
     *
     * Assume the adjacency list contains adjacent nodes of each node in the
     * order they should be visited. There are no negative edge weights in the
     * graph.
     *
     * If there is no path from from the start vertex to a given vertex,
     * have the distance be INF as seen in the graphs class.
     *
     * @throws IllegalArgumentException if graph or start vertex is null
     * @param graph the graph to search
     * @param start the starting vertex
     * @return map of the shortest distance between start and all other vertices
     */
    public static Map<Vertex, Integer> dijkstraShortestPath(Graph graph,
                                                            Vertex start) {
        if (graph == null || start == null) {
            throw new IllegalArgumentException(
                    "One or more of the inputs is null."
            );
        }

        ArrayList<VertexDistancePair> d = new ArrayList<>();
        PriorityQueue<VertexDistancePair> pq = new PriorityQueue<>();
        Map<Vertex, Integer> cloud = new HashMap<>();

        for (Vertex v: graph.getVertices()) {
            if (v.equals(start)) {
                d.add(new VertexDistancePair(v, 0));
            } else {
                d.add(new VertexDistancePair(v, Integer.MAX_VALUE));
            }
            pq.add(d.get(d.size() - 1));
        }

        while (!pq.isEmpty()) {
            VertexDistancePair u = pq.remove();
            cloud.put(u.getVertex(), u.getDistance());

            for (Map.Entry<Vertex, Integer> x : graph.getAdjacencyList().get(u.getVertex()).entrySet()) {
                VertexDistancePair comp = new VertexDistancePair(x.getKey(), x.getValue());
                if (u.distance + comp.getDistance() < ) {

                }
            }
        }

        return cloud;
    }

    /**
     * Run Floyd Warshall on the given graph to find the length of all of the
     * shortest paths between vertices.
     *
     * You will also detect if there are negative cycles in the
     * given graph.
     *
     * You should use the adjacency matrix representation of the graph.
     *
     * If there is no path from from the start vertex to a given vertex,
     * have the distance be INF as seen in the graphs class.
     *
     * @throws IllegalArgumentException if graph is null
     * @param graph the graph
     * @return the distances between each vertex. For example, given {@code arr}
     * represents the 2D array, {@code arr[i][j]} represents the distance from
     * vertex i to vertex j. Return {@code null} if there is a negative cycle.
     */
    public static int[][] floydWarshall(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException(
                    "One or more of the inputs is null."
            );
        }

        int numberofvertices = graph.getVertices().size();
        int[][] distancematrix = new int[numberofvertices][numberofvertices];
        int[][] adjacencymatrix = graph.getAdjacencyMatrix();

        for (int source = 0; source < numberofvertices; source++)
        {
            for (int destination = 0; destination < numberofvertices; destination++)
            {
                distancematrix[source][destination] = adjacencymatrix[source][destination];
            }
        }

        for (int intermediate = 0; intermediate < numberofvertices; intermediate++)
        {
            for (int source = 0; source < numberofvertices; source++)
            {
                for (int destination = 0; destination < numberofvertices; destination++)
                {
                    if (distancematrix[source][intermediate] + distancematrix[intermediate][destination]
                            < distancematrix[source][destination])
                        distancematrix[source][destination] = distancematrix[source][intermediate]
                                + distancematrix[intermediate][destination];
                }
            }
        }

        return distancematrix;
    }

    /**
     * A topological sort is a linear ordering of vertices with the restriction
     * that for every edge uv, vertex u comes before v in the ordering.
     *
     * You should use the adjacency list representation of the graph.
     * When considering which vertex to visit next while exploring the graph,
     * choose the next vertex in the adjacency list.
     *
     * You should start your topological sort with the smallest vertex
     * and if you need to select another starting vertex to continue
     * with your sort (like with a disconnected graph),
     * you should choose the next smallest applicable vertex.
     *
     * @throws IllegalArgumentException if the graph is null
     * @param graph a directed acyclic graph
     * @return a topological sort of the graph
     */
    public static List<Vertex> topologicalSort(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException(
                    "One or more of the inputs is null."
            );
        }

        int n = graph.getVertices().size();
        Map<Vertex, Boolean> used = new HashMap<>();

        for (Vertex x : graph.getVertices()) {
            used.put(x, false);
        }

        LinkedList<Vertex> list = new LinkedList<>();

        for (Vertex z : graph.getAdjacencyList().keySet()) {
            if (!used.get(z)) list = dfs(graph, used, list, z);
        }
        return list;
    }

    /**
     * Recursive Method to aid topological sort
     * @param graph The graph
     * @param used Matrix of used vertexes
     * @param list Current List of vertexes
     * @param cur Current Vertex
     * @return List of vertexes topologically sorted
     */
    private static LinkedList<Vertex> dfs(Graph graph, Map<Vertex, Boolean> used, LinkedList<Vertex> list, Vertex cur) {
        used.put(cur, true);
        if (graph.getAdjacencyList().containsKey(cur)) {
            for (Map.Entry<Vertex, Integer> x : graph.getAdjacencyList().get(cur).entrySet()) {
                if (!used.get(x.getKey())) {
                    list = dfs(graph, used, list, x.getKey());
                }
            }
        }
        list.addFirst(cur);
        return list;
    }


    /**
     * A class that pairs a vertex and a distance. Hint: might be helpful
     * for Dijkstra's.
     */
    private static class VertexDistancePair implements
        Comparable<VertexDistancePair> {
        private Vertex vertex;
        private int distance;

        /**
         * Creates a vertex distance pair
         * @param vertex the vertex to store in this pair
         * @param distance the distance to store in this pair
         */
        public VertexDistancePair(Vertex vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        /**
         * Gets the vertex stored in this pair
         * @return the vertex stored in this pair
         */
        public Vertex getVertex() {
            return vertex;
        }

        /**
         * Sets the vertex to be stored in this pair
         * @param vertex the vertex to be stored in this pair
         */
        public void setVertex(Vertex vertex) {
            this.vertex = vertex;
        }

        /**
         * Gets the distance stored in this pair
         * @return the distance stored in this pair
         */
        public int getDistance() {
            return distance;
        }

        /**
         * Sets the distance to be stored in this pair
         * @param distance the distance to be stored in this pair
         */
        public void setDistance(int distance) {
            this.distance = distance;
        }

        @Override
        public int compareTo(VertexDistancePair v) {
            return (distance < v.getDistance() ? -1
                                          : distance > v.getDistance() ? 1 : 0);
        }
    }
}
