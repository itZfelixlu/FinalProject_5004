package filters;

import model.Recipe;

/**
 * Filter implementation for preparation time-based recipe filtering.
 */
public class PrepTimeFilter implements IRecipeFilter {
    @Override
    public boolean matches(Recipe recipe, String range) {
        if (range == null || range.isEmpty() || "All".equals(range)) {
            return true;
        }

        int prepTime = recipe.getPrepTime();
        
        try {
            switch (range) {
                case "0-15 min":
                    return prepTime >= 0 && prepTime <= 15;
                case "16-30 min":
                    return prepTime >= 16 && prepTime <= 30;
                case "31-45 min":
                    return prepTime >= 31 && prepTime <= 45;
                case "46+ min":
                    return prepTime >= 46;
                default:
                    return true;
            }
        } catch (Exception e) {
            return true; // In case of any parsing errors, don't filter out the recipe
        }
    }
} 