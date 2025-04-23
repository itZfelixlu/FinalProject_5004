package filters;

import model.Recipe;

/**
 * A filter implementation that matches recipes based on their preparation time.
 * This filter supports different time ranges and can be used to find recipes
 * that can be prepared within specific time constraints.
 * 
 * <p>The supported preparation time ranges are:
 * <ul>
 *   <li>"0-15 min": Recipes that take 0 to 15 minutes to prepare</li>
 *   <li>"16-30 min": Recipes that take 16 to 30 minutes to prepare</li>
 *   <li>"31-45 min": Recipes that take 31 to 45 minutes to prepare</li>
 *   <li>"46+ min": Recipes that take 46 or more minutes to prepare</li>
 * </ul>
 * 
 * <p>If the range is null, empty, or "All", the filter will match all recipes.
 * In case of any parsing errors, the filter will also match all recipes to ensure
 * no valid recipes are accidentally filtered out.
 */
public class PrepTimeFilter implements IRecipeFilter {
    /**
     * Determines if a recipe matches the specified preparation time range.
     * 
     * @param recipe The recipe to check against the preparation time range
     * @param range The preparation time range to match against (e.g., "0-15 min", "16-30 min", etc.)
     * @return true if the recipe's preparation time falls within the specified range,
     *         or if the range is invalid/unspecified; false otherwise
     */
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