package filters;

import model.Recipe;

/**
 * Filter implementation for calorie-based recipe filtering.
 */
public class CalorieRangeFilter implements IRecipeFilter {
    @Override
    public boolean matches(Recipe recipe, String range) {
        if (range == null || range.isEmpty() || "All".equals(range)) {
            return true;
        }

        int calories = recipe.getCalories();
        
        try {
            switch (range) {
                case "0-300":
                    return calories >= 0 && calories <= 300;
                case "301-600":
                    return calories >= 301 && calories <= 600;
                case "601-900":
                    return calories >= 601 && calories <= 900;
                case "901+":
                    return calories >= 901;
                default:
                    return true;
            }
        } catch (Exception e) {
            return true; // In case of any parsing errors, don't filter out the recipe
        }
    }
} 