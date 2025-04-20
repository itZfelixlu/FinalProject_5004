package filters;

import model.Recipe;

/**
 * Interface defining the contract for recipe filtering.
 * Each implementation represents a different filtering strategy.
 */
public interface IRecipeFilter {
    /**
     * Determines if a recipe matches the given filter criteria.
     *
     * @param recipe The recipe to check
     * @param filterValue The value to filter against
     * @return true if the recipe matches the filter criteria, false otherwise
     */
    boolean matches(Recipe recipe, String filterValue);
} 