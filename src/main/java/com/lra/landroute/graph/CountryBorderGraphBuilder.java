package com.lra.landroute.graph;

import com.lra.landroute.model.Country;
import com.lra.landroute.util.StringUtil;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builds an immutable in-memory adjacency graph of country land borders.
 *
 * <p>Each country is represented by its normalized CCA3 code, and each land
 * border is stored as a bidirectional edge because land travel is possible in
 * both directions.</p>
 *
 * <p>The builder ignores invalid or blank country codes and only registers
 * borders between countries that exist in the source dataset.</p>
 */
@Component
public class CountryBorderGraphBuilder {

    public Map<String, List<String>> build(List<Country> countries) {
        Map<String, Country> countriesByCode = mapCountriesByCode(countries);
        Map<String, Set<String>> graph = initializeGraph(countriesByCode);

        registerBorders(countriesByCode, graph);
        return toImmutableAdjacencyList(graph);
    }

    private Map<String, Country> mapCountriesByCode(List<Country> countries) {
        Map<String, Country> countriesByCode = new LinkedHashMap<>();

        for (Country country : countries) {
            String countryCode = StringUtil.normalize(country.getCca3());

            if (!countryCode.isBlank()) {
                countriesByCode.putIfAbsent(countryCode, country);
            }
        }

        return countriesByCode;
    }

    private Map<String, Set<String>> initializeGraph(Map<String, Country> countriesByCode) {
        Map<String, Set<String>> graph = new LinkedHashMap<>();

        for (String countryCode : countriesByCode.keySet()) {
            graph.put(countryCode, new LinkedHashSet<>());
        }

        return graph;
    }

    private void registerBorders(Map<String, Country> countriesByCode, Map<String, Set<String>> graph) {

        for (Map.Entry<String, Country> entry : countriesByCode.entrySet()) {
            String countryCode = entry.getKey();
            Country country = entry.getValue();

            if (country.getBorders() == null) {
                continue;
            }

            for (String border : country.getBorders()) {
                String borderCode = StringUtil.normalize(border);

                if (graph.containsKey(borderCode)) {
                    addBidirectionalBorder(countryCode, borderCode, graph);
                }
            }
        }
    }

    /**
     * Adds a bidirectional border relationship between two countries in the graph.
     *
     * <p>Country borders are treated as undirected edges because land travel is
     * possible in both directions. For example, if {@code CZE} borders {@code AUT},
     * then both relationships must be registered:</p>
     *
     * <pre>
     * CZE -> AUT
     * AUT -> CZE
     * </pre>
     *
     * @param countryCode normalized CCA3 code of the source country
     * @param borderCode  normalized CCA3 code of the bordering country
     * @param graph       mutable adjacency graph where borders are registered
     */
    private void addBidirectionalBorder(String countryCode, String borderCode, Map<String, Set<String>> graph) {
        graph.get(countryCode).add(borderCode);
        graph.get(borderCode).add(countryCode);
    }

    private Map<String, List<String>> toImmutableAdjacencyList(Map<String, Set<String>> graph) {
        return graph.entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(entry.getValue())
                ));
    }

}