package filters;

import model.Recipe;

/**
 * Filter implementation for cuisine-based recipe filtering.
 */
public class CuisineFilter implements IRecipeFilter {
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