import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import model.Recipe;
import model.Ingredient;
import filters.IRecipeFilter;
import filters.TextSearchFilter;
import filters.CuisineFilter;
import filters.CalorieRangeFilter;
import filters.PrepTimeFilter;

/**
 * Integration test class for testing all filter implementations together.
 * Tests the interaction between different filters and their combined behavior.
 */
public class FilterIntegrationTest {
    private List<Recipe> createSampleRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        
        // Recipe 1: Spaghetti Bolognese
        List<Ingredient> bologneseIngredients = new ArrayList<>();
        bologneseIngredients.add(new Ingredient("Ground Beef (80/20)", 150.0, "g", 254, 6.49, "meat", "80% lean, 20% fat ground beef - versatile for burgers, meatballs, etc.", 6.49));
        bologneseIngredients.add(new Ingredient("Tomato", 150.0, "g", 18, 3.49, "vegetable", "Fresh tomato", 3.49));
        bologneseIngredients.add(new Ingredient("Olive Oil", 15.0, "ml", 119, 0.45, "seasoning", "Extra virgin olive oil", 0.45));
        Recipe bolognese = new Recipe("Spaghetti Bolognese", "savory", List.of("heavy", "rich", "umami", "meaty"), 
                                     "italian", 45, bologneseIngredients);
        recipes.add(bolognese);

        // Recipe 2: Chicken Stir Fry
        List<Ingredient> stirFryIngredients = new ArrayList<>();
        stirFryIngredients.add(new Ingredient("Chicken Breast", 120.0, "g", 165, 4.29, "meat", "Boneless, skinless chicken breast - lean protein source", 4.29));
        stirFryIngredients.add(new Ingredient("Broccoli", 100.0, "g", 34, 2.49, "vegetable", "Nutritious green florets - high in vitamin C and fiber", 2.49));
        Recipe stirFry = new Recipe("Chicken Stir Fry", "savory", List.of("light", "fresh", "crisp", "balanced"), 
                                   "chinese", 30, stirFryIngredients);
        recipes.add(stirFry);

        return recipes;
    }

    @Test
    public void testAllFiltersCombined() {
        List<Recipe> recipes = createSampleRecipes();
        List<IRecipeFilter> filters = new ArrayList<>();
        filters.add(new TextSearchFilter());
        filters.add(new CuisineFilter());
        filters.add(new CalorieRangeFilter());
        filters.add(new PrepTimeFilter());

        // Test case 1: Search for "chicken" in Chinese cuisine, 0-300 calories, 16-30 min prep time
        List<Recipe> filteredRecipes = applyAllFilters(recipes, filters, "chicken", "chinese", "0-300", "16-30 min");
        assertEquals(1, filteredRecipes.size(), "Should find Chicken Stir Fry");
        assertEquals("Chicken Stir Fry", filteredRecipes.get(0).getName());

        // Test case 2: Search for "bolognese" in Italian cuisine, 301-600 calories, 31-45 min prep time
        filteredRecipes = applyAllFilters(recipes, filters, "bolognese", "italian", "301-600", "31-45 min");
        assertEquals(1, filteredRecipes.size(), "Should find Spaghetti Bolognese");
        assertEquals("Spaghetti Bolognese", filteredRecipes.get(0).getName());

        // Test case 3: Search for "stir" in Chinese cuisine, 0-300 calories, 16-30 min prep time
        filteredRecipes = applyAllFilters(recipes, filters, "stir", "chinese", "0-300", "16-30 min");
        assertEquals(1, filteredRecipes.size(), "Should find Chicken Stir Fry");
        assertEquals("Chicken Stir Fry", filteredRecipes.get(0).getName());
    }

    @Test
    public void testFiltersWithEmptyCriteria() {
        List<Recipe> recipes = createSampleRecipes();
        List<IRecipeFilter> filters = new ArrayList<>();
        filters.add(new TextSearchFilter());
        filters.add(new CuisineFilter());
        filters.add(new CalorieRangeFilter());
        filters.add(new PrepTimeFilter());

        // Test case: All filters set to "All" or empty
        List<Recipe> filteredRecipes = applyAllFilters(recipes, filters, "", "All", "All", "All");
        assertEquals(2, filteredRecipes.size(), "Should return all recipes when no filters are applied");
    }

    @Test
    public void testFiltersWithPartialCriteria() {
        List<Recipe> recipes = createSampleRecipes();
        List<IRecipeFilter> filters = new ArrayList<>();
        filters.add(new TextSearchFilter());
        filters.add(new CuisineFilter());
        filters.add(new CalorieRangeFilter());
        filters.add(new PrepTimeFilter());

        // Test case 1: Only cuisine filter applied
        List<Recipe> filteredRecipes = applyAllFilters(recipes, filters, "", "italian", "All", "All");
        assertEquals(1, filteredRecipes.size(), "Should find only Italian recipes");
        assertEquals("Spaghetti Bolognese", filteredRecipes.get(0).getName());

        // Test case 2: Only calorie range filter applied
        filteredRecipes = applyAllFilters(recipes, filters, "", "All", "0-300", "All");
        assertEquals(1, filteredRecipes.size(), "Should find only recipes in 0-300 calorie range");
        assertEquals("Chicken Stir Fry", filteredRecipes.get(0).getName());

        // Test case 3: Only prep time filter applied
        filteredRecipes = applyAllFilters(recipes, filters, "", "All", "All", "31-45 min");
        assertEquals(1, filteredRecipes.size(), "Should find only recipes with 31-45 min prep time");
        assertEquals("Spaghetti Bolognese", filteredRecipes.get(0).getName());
    }

    private List<Recipe> applyAllFilters(List<Recipe> recipes, List<IRecipeFilter> filters,
                                       String searchText, String cuisine, String calorieRange, String prepTimeRange) {
        List<Recipe> filteredRecipes = new ArrayList<>(recipes);
        
        for (IRecipeFilter filter : filters) {
            filteredRecipes = filteredRecipes.stream()
                .filter(recipe -> {
                    if (filter instanceof TextSearchFilter) {
                        return filter.matches(recipe, searchText);
                    } else if (filter instanceof CuisineFilter) {
                        return filter.matches(recipe, cuisine);
                    } else if (filter instanceof CalorieRangeFilter) {
                        return filter.matches(recipe, calorieRange);
                    } else if (filter instanceof PrepTimeFilter) {
                        return filter.matches(recipe, prepTimeRange);
                    }
                    return true;
                })
                .toList();
        }
        
        return filteredRecipes;
    }
} 