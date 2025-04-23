package filters;

import model.Recipe;

/**
 * A filter implementation that matches recipes based on their calorie content.
 * This filter supports different calorie ranges and can be used to find recipes
 * that fall within specific calorie thresholds.
 * 
 * <p>The supported calorie ranges are:
 * <ul>
 *   <li>"0-300": Recipes with 0 to 300 calories</li>
 *   <li>"301-600": Recipes with 301 to 600 calories</li>
 *   <li>"601-900": Recipes with 601 to 900 calories</li>
 *   <li>"901+": Recipes with 901 or more calories</li>
 * </ul>
 * 
 * <p>If the range is null, empty, or "All", the filter will match all recipes.
 * In case of any parsing errors, the filter will also match all recipes to ensure
 * no valid recipes are accidentally filtered out.
 */
public class CalorieRangeFilter implements IRecipeFilter {
    /**
     * Determines if a recipe matches the specified calorie range.
     * 
     * @param recipe The recipe to check against the calorie range
     * @param range The calorie range to match against (e.g., "0-300", "301-600", etc.)
     * @return true if the recipe's calorie content falls within the specified range,
     *         or if the range is invalid/unspecified; false otherwise
     */
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