package model;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

/**
 * A utility class for calculating nutritional information for recipes and meals.
 * Handles unit conversions, cooking method modifications, and nutritional value calculations.
 * Uses a comprehensive database of nutritional values and cooking method modifiers.
 */
public class NutritionCalculator {
  private Map<String, Map<String, NutritionInfo>> nutritionData;
  private Map<String, Double> cookingModifiers;

  public NutritionCalculator() {
    this.nutritionData = new HashMap<>();
    this.cookingModifiers = new HashMap<>();
    loadNutritionData();
  }

  private void loadNutritionData() {
    try {
      // Get the current directory
      File currentDir = new File(".");
      String currentPath = currentDir.getAbsolutePath();
      System.out.println("Starting directory: " + currentPath);
      
      // Try both possible paths due to nested folder structure
      String[] possiblePaths = {
          currentPath + File.separator + "src" + File.separator + "ingredientsSource" + File.separator + "micro_nutrition.json",
          currentPath + File.separator + "FinalProject_5004" + File.separator + "src" + File.separator + "ingredientsSource" + File.separator + "micro_nutrition.json"
      };
      
      boolean fileFound = false;
      for (String path : possiblePaths) {
          File nutritionFile = new File(path);
          System.out.println("Trying path: " + nutritionFile.getAbsolutePath());
          
          if (nutritionFile.exists()) {
              System.out.println("Found nutrition data at: " + nutritionFile.getAbsolutePath());
              nutritionData = JSONParser.readMicroNutritionData(nutritionFile.getAbsolutePath());
              fileFound = true;
              break;
          }
      }
      
      if (!fileFound) {
          throw new IOException("Could not find micro_nutrition.json in any of the expected locations");
      }

    } catch (IOException e) {
      System.err.println("Error loading nutrition data: " + e.getMessage());
      System.err.println("Current directory: " + new File(".").getAbsolutePath());
      // Initialize with empty data instead of throwing an exception
      nutritionData = new HashMap<>();
    }
  }

  /**
   * Calculates the total nutritional information for a recipe, taking into account
   * ingredient quantities, unit conversions, and cooking method modifications.
   *
   * @param recipe The recipe to calculate nutrition for
   * @return A NutritionInfo object containing the total protein, fat, carbs, fiber,
   *         sugar, and sodium for the recipe
   */
  public NutritionInfo calculateNutritionForRecipe(Recipe recipe) {
    if (recipe == null) {
      System.out.println("Warning: Null recipe provided to calculateNutritionForRecipe");
      return new NutritionInfo(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    double totalProtein = 0;
    double totalCarbs = 0;
    double totalFat = 0;
    double totalFiber = 0;
    double totalSugar = 0;
    double totalSodium = 0;

    for (Ingredient ingredient : recipe.getIngredients()) {
      if (ingredient == null) {
        System.out.println("Warning: Null ingredient found in recipe");
        continue;
      }

      // Get the base nutrition values for this ingredient
      Map<String, Double> nutritionValues = getNutritionValues(ingredient.getName());
      if (nutritionValues == null) {
        System.out.println("Warning: No nutrition values found for ingredient: " + ingredient.getName());
        continue;
      }

      // Calculate nutrition based on quantity and unit
      double quantity = ingredient.getQuantity();
      String unit = ingredient.getUnit().toLowerCase();
      
      // Convert quantity to grams if needed
      double quantityInGrams = quantity;
      if (unit.equals("ml")) {
        // For liquids, assume density of 1g/ml
        quantityInGrams = quantity;
      } else if (unit.equals("tablespoon")) {
        quantityInGrams = quantity * 15; // 1 tbsp = 15g
      } else if (unit.equals("teaspoon")) {
        quantityInGrams = quantity * 5; // 1 tsp = 5g
      } else if (unit.equals("cup")) {
        quantityInGrams = quantity * 240; // 1 cup = 240g
      } else if (unit.equals("piece") || unit.equals("medium")) {
        // For pieces, use the quantity as is since nutrition data is per piece
        quantityInGrams = quantity;
      }

      // Special handling for ingredients that expand when cooked
      String ingredientName = ingredient.getName().toLowerCase();
      if (ingredientName.contains("pasta") || ingredientName.contains("noodle") || 
          ingredientName.contains("rice") || ingredientName.contains("dough")) {
        // These ingredients typically double in weight when cooked
        quantityInGrams *= 2;
      }

      // Calculate nutrition per 100g
      double protein = (nutritionValues.getOrDefault("protein", 0.0) * quantityInGrams) / 100.0;
      double carbs = (nutritionValues.getOrDefault("carbohydrates", 0.0) * quantityInGrams) / 100.0;
      double fat = (nutritionValues.getOrDefault("fat", 0.0) * quantityInGrams) / 100.0;
      double fiber = (nutritionValues.getOrDefault("fiber", 0.0) * quantityInGrams) / 100.0;
      double sugar = (nutritionValues.getOrDefault("sugar", 0.0) * quantityInGrams) / 100.0;
      double sodium = (nutritionValues.getOrDefault("sodium", 0.0) * quantityInGrams) / 100.0;

      // Apply cooking method modifier
      double modifier = getCookingMethodModifier(ingredient.getCookingMethod());
      protein *= modifier;
      carbs *= modifier;
      fat *= modifier;
      fiber *= modifier;
      sugar *= modifier;
      sodium *= modifier;

      totalProtein += protein;
      totalCarbs += carbs;
      totalFat += fat;
      totalFiber += fiber;
      totalSugar += sugar;
      totalSodium += sodium;

      System.out.println(String.format("Ingredient: %s, Quantity: %.2f %s (%.2f g), Protein: %.2f, Carbs: %.2f, Fat: %.2f, Modifier: %.2f",
        ingredient.getName(), quantity, unit, quantityInGrams, protein, carbs, fat, modifier));
    }

    // Create NutritionInfo with the total values
    NutritionInfo totalNutrition = new NutritionInfo(totalProtein, totalFat, totalCarbs, totalFiber, totalSugar, totalSodium);
    
    System.out.println(String.format("Total nutrition for recipe %s: Calories=%.2f, Protein=%.2f, Carbs=%.2f, Fat=%.2f, Fiber=%.2f",
      recipe.getName(), totalNutrition.getCalories(), totalProtein, totalCarbs, totalFat, totalFiber));

    return totalNutrition;
  }

  private Map<String, Double> getNutritionValues(String ingredientName) {
    if (ingredientName == null || ingredientName.trim().isEmpty()) {
      return null;
    }

    String normalizedName = ingredientName.toLowerCase().trim();
    
    // First try to find the ingredient in each category
    for (Map.Entry<String, Map<String, NutritionInfo>> categoryEntry : nutritionData.entrySet()) {
      String category = categoryEntry.getKey();
      Map<String, NutritionInfo> categoryMap = categoryEntry.getValue();
      
      // Check if this category contains the ingredient
      for (Map.Entry<String, NutritionInfo> ingredientEntry : categoryMap.entrySet()) {
        String ingredientKey = ingredientEntry.getKey().toLowerCase();
        if (ingredientKey.contains(normalizedName) || normalizedName.contains(ingredientKey)) {
          NutritionInfo nutritionInfo = ingredientEntry.getValue();
          System.out.println(String.format("Found nutrition data for %s in category %s", ingredientName, category));
          
          // Convert NutritionInfo to Map<String, Double>
          Map<String, Double> values = new HashMap<>();
          values.put("protein", nutritionInfo.getProtein());
          values.put("carbohydrates", nutritionInfo.getCarbohydrates());
          values.put("fat", nutritionInfo.getFat());
          values.put("fiber", nutritionInfo.getFiber());
          values.put("sugar", nutritionInfo.getSugar());
          values.put("sodium", nutritionInfo.getSodium());

          System.out.println(String.format("Nutrition values for %s: Protein=%.2f, Carbs=%.2f, Fat=%.2f, Calories=%.2f",
              ingredientName, nutritionInfo.getProtein(), nutritionInfo.getCarbohydrates(), 
              nutritionInfo.getFat(), nutritionInfo.getCalories()));

          return values;
        }
      }
    }
    
    System.out.println(String.format("No nutrition data found for ingredient: %s", ingredientName));
    return null;
  }

  /**
   * Gets the cooking method modifier for a specific cooking method.
   * Returns a multiplier that affects the nutritional values based on how the
   * ingredient is cooked (e.g., frying increases calories, steaming preserves them).
   *
   * @param cookingMethod The cooking method to get the modifier for
   * @return A double representing the multiplier for the cooking method
   *         (1.0 for no change, >1.0 for increased values, <1.0 for decreased values)
   */
  private double getCookingMethodModifier(String cookingMethod) {
    if (cookingMethod == null || cookingMethod.trim().isEmpty()) {
      return 1.0;
    }

    String normalizedMethod = cookingMethod.toLowerCase().trim();
    return cookingModifiers.getOrDefault(normalizedMethod, 1.0);
  }

  /**
   * Calculates the total nutritional information for a meal consisting of multiple recipes.
   * Aggregates the nutritional values from all recipes in the meal.
   *
   * @param meals A map of recipe names to Recipe objects representing the meal
   * @return A NutritionInfo object containing the total protein, fat, carbs, fiber,
   *         sugar, and sodium for the entire meal
   */
  public NutritionInfo calculateNutritionForMeal(Map<String, Recipe> meals) {
    NutritionInfo totalNutrition = new NutritionInfo(0, 0, 0, 0, 0, 0);

    for (Recipe recipe : meals.values()) {
      totalNutrition = totalNutrition.add(calculateNutritionForRecipe(recipe));
    }

    return totalNutrition;
  }
} 