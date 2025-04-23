package filters;

import model.Recipe;

/**
 * A filter implementation that matches recipes based on their cuisine type.
 * This filter performs case-insensitive matching and supports partial matches
 * between the recipe's cuisine and the selected cuisine.
 * 
 * <p>The filter is designed to be lenient in its matching:
 * <ul>
 *   <li>It converts both the recipe's cuisine and the selected cuisine to lowercase</li>
 *   <li>It trims whitespace from both strings</li>
 *   <li>It considers a match if either string contains the other</li>
 * </ul>
 * 
 * <p>If the cuisine is null, empty, or "All" (case-insensitive), the filter will
 * match all recipes.
 */
public class CuisineFilter implements IRecipeFilter {
    /**
     * Determines if a recipe matches the specified cuisine.
     * 
     * @param recipe The recipe to check against the cuisine filter
     * @param cuisine The cuisine to match against (e.g., "italian", "chinese", etc.)
     * @return true if the recipe's cuisine matches the specified cuisine (case-insensitive),
     *         or if the cuisine is invalid/unspecified; false otherwise
     */
    @Override
    public boolean matches(Recipe recipe, String cuisine) {
        if (cuisine == null || cuisine.isEmpty() || "All".equalsIgnoreCase(cuisine)) {
            return true;
        }

        String recipeCuisine = recipe.getCuisine().toLowerCase().trim();
        String selectedCuisine = cuisine.toLowerCase().trim();
        
        // More lenient comparison - check if either contains the other
        return recipeCuisine.contains(selectedCuisine) || 
               selectedCuisine.contains(recipeCuisine);
    }
} 