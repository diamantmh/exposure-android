package io.github.getExposure.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Filter is a utility class that selects desired ExposureLocations and
 * discards ones that do not match the given criteria.
 */
public class Filter {

    /**
     * Returns a new array of ExposureLocation with only locations that have
     * at least one of the categories in filterCriteria.
     *
     * Returns null if there are no results
     *
     * @param locations array of ExposureLocation of locations to be filtered
     * @param filterCriteria set of Categories to filter with
     * @return an array of ExposureLocations that match the filter criteria or
     * null if there are no results.
     */
    public static ExposureLocation[] filterLocations(ExposureLocation[] locations, Set<Category> filterCriteria) {
        List<ExposureLocation> filtered = new ArrayList<>();

        if (filterCriteria.isEmpty()) {
            return null;
        }

        // include any locations that match the filter criteria
        for (ExposureLocation loc : locations) {
            for (Category matchCat : filterCriteria) {
                if (match(loc, matchCat)) {
                    filtered.add(loc);
                }
            }
        }

        if (filtered.isEmpty()) {
            return null;
        }

        // copy to array
        ExposureLocation[] filteredArray = new ExposureLocation[filtered.size()];
        for (int i = 0; i < filtered.size(); i++) {
            filteredArray[i] = filtered.get(i);
        }

        return filteredArray;
    }

    /**
     * Returns true iff the given location has the given Category.
     *
     * @param loc location to match on criteria
     * @param matchCat the desired category
     * @return true iff the given location has the given Category.
     */
    private static boolean match(ExposureLocation loc, Category matchCat) {
        for (Category locCat : loc.getCategories()) {
            if (locCat.getId() == matchCat.getId()) {
                return true;
            }
        }
        return false;
    }
}