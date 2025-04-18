import java.util.ArrayList;
import java.util.List;

/**
 * Represents a recipe with ingredients, nutritional information and preparation details.
 * Tracks total calories based on included ingredients and their cooking methods.
 */
public class Recipe {
  private String name;
  private int calories;
  private String flavor;
  private String cuisine;
  private int prepTime; // in minutes
  private List<Ingredient> ingredients;

  /**
   * Creates a new recipe with the specified attributes.
   *
   * @param name Recipe name
   * @param flavor Flavor profile
   * @param cuisine Cuisine type
   * @param prepTime Preparation time in minutes
   * @param ingredients Initial ingredient list (may be null)
   * @throws IllegalArgumentException If any required parameters are invalid
   */
  public Recipe(String name, String flavor, String cuisine, int prepTime, List<Ingredient> ingredients) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Recipe name cannot be empty");
    }
    if (flavor == null || flavor.trim().isEmpty()) {
      throw new IllegalArgumentException("Flavor cannot be empty");
    }
    if (cuisine == null || cuisine.trim().isEmpty()) {
      throw new IllegalArgumentException("Cuisine cannot be empty");
    }
    if (prepTime <= 0) {
      throw new IllegalArgumentException("Preparation time must be positive");
    }

    this.name = name.trim();
    this.flavor = flavor.trim();
    this.cuisine = cuisine.trim();
    this.prepTime = prepTime;
    this.ingredients = new ArrayList<>();
    this.calories = 0; // Will be calculated based on ingredients
  }

  /**
   * Adds an ingredient to the recipe and updates total calories.
   *
   * @param ingredient Ingredient to add
   * @throws IllegalArgumentException If ingredient is null
   */
  public void addIngredient(Ingredient ingredient) {
    if (ingredient == null) {
      throw new IllegalArgumentException("Ingredient cannot be null");
    }
    ingredients.add(ingredient);
    calories += ingredient.getTotalCalories();
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
    return calories;
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
    return new ArrayList<>(ingredients);
  }

  /**
   * Checks if recipe is within specified calorie range.
   *
   * @param minCalories Minimum calories
   * @param maxCalories Maximum calories
   * @return True if within range, false otherwise
   */
  public boolean isWithinCalorieRange(int minCalories, int maxCalories) {
    return calories >= minCalories && calories <= maxCalories;
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
            "Cuisine: %s\n" +
            "Preparation Time: %d minutes\n" +
            "Ingredients:\n",
        name, calories, flavor, cuisine, prepTime));

    for (Ingredient ingredient : ingredients) {
      sb.append("- ").append(ingredient.toString()).append("\n");
    }

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
        name, calories, cuisine, flavor, prepTime, ingredients.size());
  }
} 