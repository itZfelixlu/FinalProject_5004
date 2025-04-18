import java.util.ArrayList;
import java.util.List;

public class Recipe {
  private String name;
  private int calories;
  private String flavor;
  private String cuisine;
  private int prepTime; // in minutes
  private List<Ingredient> ingredients;

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

  // Add an ingredient to the recipe and update total calories
  public void addIngredient(Ingredient ingredient) {
    if (ingredient == null) {
      throw new IllegalArgumentException("Ingredient cannot be null");
    }
    ingredients.add(ingredient);
    calories += ingredient.getTotalCalories();
  }

  // Getters
  public String getName() {
    return name;
  }

  public int getCalories() {
    return calories;
  }

  public String getFlavor() {
    return flavor;
  }

  public String getCuisine() {
    return cuisine;
  }

  public int getPrepTime() {
    return prepTime;
  }

  public List<Ingredient> getIngredients() {
    return new ArrayList<>(ingredients);
  }

  // New method to check if recipe is within calorie range
  public boolean isWithinCalorieRange(int minCalories, int maxCalories) {
    return calories >= minCalories && calories <= maxCalories;
  }

  // New method to get detailed recipe information
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

  @Override
  public String toString() {
    return String.format("%s (%d calories, %s cuisine, %s flavor, %d min prep, %d ingredients)",
        name, calories, cuisine, flavor, prepTime, ingredients.size());
  }
} 