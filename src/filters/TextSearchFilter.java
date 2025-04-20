package filters;

import model.Recipe;

/**
 * A filter that searches for text matches in various recipe fields.
 * Searches through recipe name, flavor, flavor tags, cuisine, and ingredients.
 */
public class TextSearchFilter implements IRecipeFilter {
    
    /**
     * Checks if the recipe matches the given search text.
     * The search is case-insensitive and looks through:
     * - Recipe name
     * - Flavor profile
     * - Flavor tags
     * - Cuisine type
     * - Ingredient names
     *
     * @param recipe The recipe to check
     * @param searchText The text to search for
     * @return true if the search text is found in any of the recipe's text fields
     */
    @Override
    public boolean matches(Recipe recipe, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return true; // Empty search matches all recipes
        }
        
        String searchLower = searchText.toLowerCase().trim();
        
        // Check recipe name
        if (recipe.getName().toLowerCase().contains(searchLower)) {
            return true;
        }
        
        // Check flavor profile
        if (recipe.getFlavor().toLowerCase().contains(searchLower)) {
            return true;
        }
        
        // Check flavor tags
        for (String tag : recipe.getFlavorTags()) {
            if (tag.toLowerCase().contains(searchLower)) {
                return true;
            }
        }
        
        // Check cuisine
        if (recipe.getCuisine().toLowerCase().contains(searchLower)) {
            return true;
        }
        
        // Check ingredients
        for (var ingredient : recipe.getIngredients()) {
            if (ingredient.getName().toLowerCase().contains(searchLower)) {
                return true;
            }
        }
        
        return false;
    }
}


