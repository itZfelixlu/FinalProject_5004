package model;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a recipe with ingredients, nutritional information and preparation details.
 * Tracks total calories based on included ingredients and their cooking methods.
 */
public class Recipe {
  private final String name;
  private final String flavor;
  private final List<String> flavorTags;
  private final String cuisine;
  private final int prepTime; // in minutes
  private final List<Ingredient> ingredients;
  private final NutritionCalculator nutritionCalculator;
  private NutritionInfo nutritionInfo;

  /**
   * Creates a new recipe with the specified attributes.
   *
   * @param name Recipe name
   * @param flavor Flavor profile
   * @param flavorTags List of flavor tags
   * @param cuisine Cuisine type
   * @param prepTime Preparation time in minutes
   * @param ingredients Initial ingredient list (may be null)
   * @throws IllegalArgumentException If any required parameters are invalid
   */
  public Recipe(String name, String flavor, List<String> flavorTags, String cuisine, int prepTime, List<Ingredient> ingredients) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Recipe name cannot be empty");
    }
    if (flavor == null || flavor.trim().isEmpty()) {
      throw new IllegalArgumentException("Flavor cannot be empty");
    }
    if (flavorTags == null) {
      throw new IllegalArgumentException("Flavor tags cannot be null");
    }
    if (cuisine == null || cuisine.trim().isEmpty()) {
      throw new IllegalArgumentException("Cuisine cannot be empty");
    }
    if (prepTime <= 0) {
      throw new IllegalArgumentException("Preparation time must be positive");
    }

    this.name = name.trim();
    this.flavor = flavor.trim();
    this.flavorTags = new ArrayList<>(flavorTags); // Create defensive copy
    this.cuisine = cuisine.trim();
    this.prepTime = prepTime;
    this.ingredients = new ArrayList<>(ingredients); // Create defensive copy
    this.nutritionCalculator = new NutritionCalculator();

    calculateNutrition();
  }

  private void calculateNutrition() {
    if (nutritionCalculator != null) {
      this.nutritionInfo = nutritionCalculator.calculateNutritionForRecipe(this);
      if (nutritionInfo == null) {
        System.out.println("Warning: NutritionInfo is null for recipe: " + name);
      }
    } else {
      System.out.println("Warning: NutritionCalculator is null for recipe: " + name);
    }
  }

  /**
   * Gets the recipe name.
   *
   * @return Recipe name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the total calories of the recipe.
   *
   * @return Total calories
   */
  public int getCalories() {
    return nutritionInfo != null ? (int) Math.round(nutritionInfo.getCalories()) : 0;
  }

  /**
   * Gets the flavor profile of the recipe.
   *
   * @return Flavor description
   */
  public String getFlavor() {
    return flavor;
  }

  /**
   * Gets the cuisine type of the recipe.
   *
   * @return Cuisine type
   */
  public String getCuisine() {
    return cuisine;
  }

  /**
   * Gets the preparation time in minutes.
   *
   * @return Prep time in minutes
   */
  public int getPrepTime() {
    return prepTime;
  }

  /**
   * Gets a copy of the ingredients list.
   *
   * @return List of ingredients
   */
  public List<Ingredient> getIngredients() {
    return new ArrayList<>(ingredients); // Return defensive copy
  }

  /**
   * Gets the nutrition information for the recipe.
   *
   * @return Nutrition information
   */
  public NutritionInfo getNutritionInfo() {
    if (nutritionInfo == null) {
      calculateNutrition();
    }
    return nutritionInfo;
  }

  /**
   * Checks if recipe is within specified calorie range.
   *
   * @param minCalories Minimum calories
   * @param maxCalories Maximum calories
   * @return True if within range, false otherwise
   */
  public boolean isWithinCalorieRange(int minCalories, int maxCalories) {
    return getCalories() >= minCalories && getCalories() <= maxCalories;
  }

  /**
   * Gets the flavor tags of the recipe.
   *
   * @return List of flavor tags
   */
  public List<String> getFlavorTags() {
    return new ArrayList<>(flavorTags); // Return defensive copy
  }

  /**
   * Gets formatted detailed information about the recipe.
   *
   * @return Detailed recipe information including ingredients
   */
  public String getDetailedInfo() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format(
        "Recipe: %s\n" +
            "Total Calories: %d\n" +
            "Flavor: %s\n" +
            "Flavor Tags: %s\n" +
            "Cuisine: %s\n" +
            "Preparation Time: %d minutes\n" +
            "Ingredients:\n",
        name, getCalories(), flavor, String.join(", ", flavorTags), cuisine, prepTime));

    for (Ingredient ingredient : ingredients) {
      sb.append("- ").append(ingredient.toString()).append("\n");
    }

    return sb.toString();
  }

  /**
   * Gets formatted text of ingredients list.
   *
   * @return Formatted ingredients text
   */
  public String getIngredientsText() {
    StringBuilder sb = new StringBuilder();
    for (Ingredient ingredient : ingredients) {
      sb.append(String.format("• %s\n", ingredient.toString()));
    }
    return sb.toString();
  }

  /**
   * Gets formatted text of nutrition information.
   *
   * @return Formatted nutrition text
   */
  public String getNutritionText() {
    if (nutritionInfo == null) {
      calculateNutrition();
    }
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("Total Calories: %d\n", getCalories()));
    if (nutritionInfo != null) {
      sb.append(String.format("Protein: %.1fg\n", nutritionInfo.getProtein()));
      sb.append(String.format("Carbohydrates: %.1fg\n", nutritionInfo.getCarbohydrates()));
      sb.append(String.format("Fat: %.1fg\n", nutritionInfo.getFat()));
      sb.append(String.format("Fiber: %.1fg\n", nutritionInfo.getFiber()));
    }
    return sb.toString();
  }

  /**
   * Gets formatted text of preparation steps.
   *
   * @return Formatted preparation text
   */
  public String getPrepText() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("Cuisine: %s\n", cuisine));
    sb.append(String.format("Preparation Time: %d minutes\n\n", prepTime));
    sb.append("Cooking Instructions:\n");
    sb.append("1. Gather all ingredients\n");
    for (Ingredient ingredient : ingredients) {
      sb.append(String.format("2. Prepare %s using %s method\n", 
          ingredient.getName(), 
          ingredient.getCookingMethod()));
    }
    sb.append("3. Combine ingredients according to recipe\n");
    sb.append("4. Serve and enjoy!\n");
    return sb.toString();
  }

  /**
   * Returns a string representation of this recipe.
   *
   * @return Concise recipe summary
   */
  @Override
  public String toString() {
    return String.format("%s (%d calories, %s cuisine, %s flavor, %d min prep, %d ingredients)",
        name, getCalories(), cuisine, flavor, prepTime, ingredients.size());
  }
} 