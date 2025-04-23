package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Recipe class using JUnit 5.
 */
class RecipeTest {

  private Recipe testRecipe;
  private List<String> flavorTags;
  private List<Ingredient> ingredients;

  @BeforeEach
  void setUp() {
    ingredients = new ArrayList<>();
    ingredients.add(new Ingredient(
        "TestChicken", 200, "g", 165, 4.99,
        "meat", "grilled", "Test chicken", 4.99
    ));
    ingredients.add(new Ingredient(
        "TestBroccoli", 100, "g", 34, 2.49,
        "vegetable", "steamed", "Test broccoli", 2.49
    ));
    flavorTags = Arrays.asList("savory", "healthy", "protein-rich");
    testRecipe = new Recipe(
        "Test Stir Fry",
        "savory",
        flavorTags,
        "asian",
        25,
        ingredients
    );
  }

  /**
   * Tests constructor validation for empty name.
   */
  @Test
  void testConstructorValidationEmptyName() {
    // Should throw exception for empty name
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new Recipe("", "savory", flavorTags, "asian", 25, ingredients);
    });
    assertTrue(exception.getMessage().contains("name"));
  }

  /**
   * Tests constructor validation for null flavor tags.
   */
  @Test
  void testConstructorValidationNullFlavorTags() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new Recipe("Test Recipe", "savory", null, "asian", 25, ingredients);
    });
    assertTrue(exception.getMessage().contains("Flavor tags"));
  }

  /**
   * Tests constructor validation for negative prep time.
   */
  @Test
  void testConstructorValidationNegativePrepTime() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new Recipe("Test Recipe", "savory", flavorTags, "asian", -5, ingredients);
    });
    assertTrue(exception.getMessage().contains("Preparation time"));
  }

  /**
   * Tests getName method.
   */
  @Test
  void testGetName() {
    assertEquals("Test Stir Fry", testRecipe.getName());
    assertFalse(testRecipe.getName().isEmpty());
  }

  /**
   * Tests getCalories method.
   */
  @Test
  void testGetCalories() {
    int calories = testRecipe.getCalories();
    assertTrue(calories >= 0, "Calories should not be negative");
    assertTrue(calories > 0, "Calories should be calculated based on ingredients");
  }

  /**
   * Tests getFlavor method.
   */
  @Test
  void testGetFlavor() {
    // Verify flavor matches what was set
    assertEquals("savory", testRecipe.getFlavor());

    // Verify flavor is not empty
    assertFalse(testRecipe.getFlavor().isEmpty());
  }

  /**
   * Tests getCuisine method.
   */
  @Test
  void testGetCuisine() {
    // Verify cuisine matches what was set
    assertEquals("asian", testRecipe.getCuisine());
    assertFalse(testRecipe.getCuisine().isEmpty());
  }

  /**
   * Tests getPrepTime method.
   */
  @Test
  void testGetPrepTime() {
    assertEquals(25, testRecipe.getPrepTime());
    assertTrue(testRecipe.getPrepTime() > 0);
  }

  /**
   * Tests getIngredients method.
   */
  @Test
  void testGetIngredients() {
    List<Ingredient> recipeIngredients = testRecipe.getIngredients();
    assertNotSame(ingredients, recipeIngredients);
    assertEquals(2, recipeIngredients.size());
    assertEquals("TestChicken", recipeIngredients.get(0).getName());
  }

  /**
   * Tests getNutritionInfo method.
   */
  @Test
  void testGetNutritionInfo() {
    NutritionInfo info = testRecipe.getNutritionInfo();
    assertNotNull(info);
    assertTrue(info.getCalories() >= 0);
    assertTrue(info.getProtein() >= 0);
  }

  /**
   * Tests isWithinCalorieRange method.
   */
  @Test
  void testIsWithinCalorieRange() {
    int calories = testRecipe.getCalories();
    assertTrue(testRecipe.isWithinCalorieRange(calories - 50, calories + 50));
    assertFalse(testRecipe.isWithinCalorieRange(0, calories - 50));
  }

  /**
   * Tests getFlavorTags method.
   */
  @Test
  void testGetFlavorTags() {
    List<String> tags = testRecipe.getFlavorTags();

    // Verify defensive copy (not the same reference)
    assertNotSame(flavorTags, tags);
    assertEquals(3, tags.size());
    assertTrue(tags.contains("savory"));
  }

  /**
   * Tests getIngredientsText method.
   */
  @Test
  void testGetIngredientsText() {
    String text = testRecipe.getIngredientsText();
    assertTrue(text.contains("TestChicken"));
    assertTrue(text.contains("TestBroccoli"));

    // Verify text contains bullet points
    assertTrue(text.contains("•"));
  }

  /**
   * Tests getNutritionText method.
   */
  @Test
  void testGetNutritionText() {
    String text = testRecipe.getNutritionText();
    assertTrue(text.contains("Calories"));
    assertTrue(text.contains("Protein"));
    assertTrue(text.contains("Carbohydrates"));
  }

  /**
   * Tests getPrepText method.
   */
  @Test
  void testGetPrepText() {
    String text = testRecipe.getPrepText();
    // Verify text contains cuisine information
    assertTrue(text.contains("asian"));
    // Verify text contains cooking instructions
    assertTrue(text.contains("Cooking Instructions"));
    // Verify text contains ingredient cooking methods
    assertTrue(text.contains("grilled"));
  }

  /**
   * Tests toString method.
   */
  @Test
  void testToString() {
    String text = testRecipe.toString();
    assertTrue(text.contains("Test Stir Fry"));
    assertTrue(text.contains("asian"));
    assertTrue(text.contains("2"));
  }
}