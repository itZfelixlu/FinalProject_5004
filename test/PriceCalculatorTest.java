package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PriceCalculator class.
 */
class PriceCalculatorTest {

  private PriceCalculator priceCalculator;
  private Ingredient chickenIngredient;
  private Ingredient broccoliIngredient;
  private Ingredient oilIngredient;
  private Recipe testRecipe;

  @BeforeEach
  void setUp() {
    priceCalculator = new PriceCalculator();

    chickenIngredient = new Ingredient(
        "TestChicken", 200, "g", 165, 4.99,
        "meat", "grilled", "Test chicken", 4.99
    );

    broccoliIngredient = new Ingredient(
        "TestBroccoli", 100, "g", 34, 2.49,
        "vegetable", "steamed", "Test broccoli", 2.49
    );

    oilIngredient = new Ingredient(
        "TestOil", 15, "tablespoon", 120, 0.45,
        "seasoning", "raw", "Test oil", 0.45
    );

    List<Ingredient> ingredients = new ArrayList<>();
    ingredients.add(chickenIngredient);
    ingredients.add(broccoliIngredient);
    ingredients.add(oilIngredient);

    testRecipe = new Recipe(
        "Test Stir Fry",
        "savory",
        List.of("healthy", "quick", "protein-rich"),
        "asian",
        25,
        ingredients
    );
  }

  /**
   * Tests calculateRecipePrice method.
   */
  @Test
  void testCalculateRecipePrice() {
    double recipePrice = priceCalculator.calculateRecipePrice(testRecipe);

    assertTrue(recipePrice > 0);

    double expectedPrice =
        priceCalculator.calculateIngredientPrice(chickenIngredient) +
            priceCalculator.calculateIngredientPrice(broccoliIngredient) +
            priceCalculator.calculateIngredientPrice(oilIngredient);

    assertEquals(expectedPrice, recipePrice, 0.01);
  }

  /**
   * Tests calculateRecipePrice method with null recipe.
   */
  @Test
  void testCalculateRecipePriceWithNullRecipe() {
    double price = priceCalculator.calculateRecipePrice(null);

    assertEquals(0.0, price);
  }

  /**
   * Tests calculateIngredientPrice method for weight-based ingredient.
   */
  @Test
  void testCalculateIngredientPriceForWeightBasedIngredient() {
    double chickenPrice = priceCalculator.calculateIngredientPrice(chickenIngredient);

    assertTrue(chickenPrice > 0);

    double expectedPrice = (4.99 / 100.0) * 200;
    assertEquals(expectedPrice, chickenPrice, 0.01);
  }

  /**
   * Tests calculateIngredientPrice method for volume-based ingredient.
   */
  @Test
  void testCalculateIngredientPriceForVolumeBasedIngredient() {
    double oilPrice = priceCalculator.calculateIngredientPrice(oilIngredient);

    assertTrue(oilPrice > 0);

    double basePrice = 0.45 / 100.0;
    double expectedPrice = basePrice * 15 * 15;
    assertEquals(expectedPrice, oilPrice, 0.01);
  }

  /**
   * Tests calculateIngredientPrice method with null ingredient.
   */
  @Test
  void testCalculateIngredientPriceWithNullIngredient() {
    double price = priceCalculator.calculateIngredientPrice(null);

    assertEquals(0.0, price);
  }

  /**
   * Tests calculateIngredientPrice method with piece-based ingredient.
   */
  @Test
  void testCalculateIngredientPriceForPieceBasedIngredient() {
    Ingredient eggIngredient = new Ingredient(
        "TestEgg", 6, "piece", 70, 0.25,
        "dairy", "raw", "Test egg", 0.25
    );

    double eggPrice = priceCalculator.calculateIngredientPrice(eggIngredient);

    assertTrue(eggPrice > 0);

    double expectedPrice = 0.25 * 6;
    assertEquals(expectedPrice, eggPrice, 0.01);
  }
}