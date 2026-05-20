package com.lra.landroute.algorithm;

import com.lra.landroute.exception.RouteNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ShortestRouteFinder {

    /**
     * Finds the shortest land route between the origin and destination countries.
     *
     * <p>This method uses Breadth-First Search. Each country is treated as a node, and each land border is treated
     * as an edge with equal cost. Therefore, BFS returns the route with the minimum
     * number of border crossings.</p>
     *
     * <p>The {@code previousCountry} map is used to remember how each country was
     * reached during traversal. Once the destination is found, the final route is
     * reconstructed from destination back to origin.</p>
     *
     * @param origin      normalized CCA3 code of the origin country
     * @param destination normalized CCA3 code of the destination country
     * @param borderGraph adjacency graph where each country maps to its bordering countries
     * @return ordered list of country codes representing the shortest land route
     * @throws RouteNotFoundException if no land route exists between origin and destination
     */
    public List<String> findShortestRoute(String origin, String destination, Map<String, List<String>> borderGraph) {
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> previousCountry = new HashMap<>();

        initializeSearch(origin, queue, visited, previousCountry);
        return searchRoute(origin, destination, borderGraph, queue, visited, previousCountry);
    }

    /**
     * Initializes the BFS traversal state with the origin country.
     *
     * <p>The origin country is added to the queue as the first country to process,
     * marked as visited to avoid reprocessing, and stored in the previous-country
     * map with a {@code null} parent because it is the starting point of the route.</p>
     *
     * @param origin          normalized CCA3 code of the origin country
     * @param queue           BFS queue containing countries waiting to be processed
     * @param visited         set of already visited country codes
     * @param previousCountry map tracking how each country was reached during traversal
     */
    private void initializeSearch(
            String origin,
            Queue<String> queue,
            Set<String> visited,
            Map<String, String> previousCountry) {

        queue.add(origin);
        visited.add(origin);
        previousCountry.put(origin, null);
    }

    /**
     * Performs the BFS traversal over the country border graph.
     *
     * <p>The method processes countries from the queue one by one. If the current
     * country is the destination, the route is reconstructed immediately. Otherwise,
     * all unvisited bordering countries are added to the queue for later processing.</p>
     *
     * @param origin          normalized CCA3 code of the origin country
     * @param destination     normalized CCA3 code of the destination country
     * @param borderGraph     adjacency graph of country borders
     * @param queue           BFS queue containing countries waiting to be processed
     * @param visited         set of already visited country codes
     * @param previousCountry map tracking how each country was reached during traversal
     * @return ordered list of country codes representing the shortest land route
     * @throws RouteNotFoundException if the destination cannot be reached by land
     */
    private List<String> searchRoute(
            String origin,
            String destination,
            Map<String, List<String>> borderGraph,
            Queue<String> queue,
            Set<String> visited,
            Map<String, String> previousCountry) {

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (isDestinationReached(current, destination)) {
                return reconstructPath(destination, previousCountry);
            }

            visitUnvisitedBorders(current, borderGraph, queue, visited, previousCountry);
        }

        throw new RouteNotFoundException(
                "No land route found from " + origin + " to " + destination
        );
    }

    private boolean isDestinationReached(String current, String destination) {
        return current.equals(destination);
    }

    /**
     * Visits all unvisited bordering countries of the current country.
     *
     * <p>Each newly discovered border country is marked as visited, linked to the
     * current country in the previous-country map, and added to the BFS queue for
     * later processing.</p>
     *
     * <p>The expression {@code visited.add(neighbor)} is used intentionally because
     * it returns {@code true} only when the country was not already present in the
     * visited set.</p>
     *
     * @param current         currently processed country code
     * @param borderGraph     adjacency graph of country borders
     * @param queue           BFS queue containing countries waiting to be processed
     * @param visited         set of already visited country codes
     * @param previousCountry map tracking how each country was reached during traversal
     */
    private void visitUnvisitedBorders(
            String current,
            Map<String, List<String>> borderGraph,
            Queue<String> queue,
            Set<String> visited,
            Map<String, String> previousCountry) {

        for (String neighbor : borderGraph.getOrDefault(current, List.of())) {
            if (visited.add(neighbor)) {
                previousCountry.put(neighbor, current);
                queue.add(neighbor);
            }
        }
    }

    /**
     * Reconstructs the final route from destination back to origin.
     *
     * <p>The BFS traversal stores each discovered country with the country from
     * which it was reached. This method walks backward from the destination using
     * that parent relationship, then reverses the result to return the route in
     * origin-to-destination order.</p>
     *
     * @param destination     normalized CCA3 code of the destination country
     * @param previousCountry map tracking how each country was reached during traversal
     * @return ordered list of country codes from origin to destination
     */
    private List<String> reconstructPath(
            String destination,
            Map<String, String> previousCountry) {

        List<String> route = new ArrayList<>();
        String current = destination;

        while (current != null) {
            route.add(current);
            current = previousCountry.get(current);
        }

        Collections.reverse(route);
        return route;
    }

}