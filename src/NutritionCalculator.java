import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class NutritionCalculator {
  private Map<String, Map<String, NutritionInfo>> nutritionData;

  public NutritionCalculator() {
    this.nutritionData = new HashMap<>();
    loadNutritionData();
  }

  private void loadNutritionData() {
    try {
      nutritionData = JSONParser.readMicroNutritionData("src/ingredientsSource/micro_nutrition.json");
    } catch (IOException e) {
      System.err.println("Error loading nutrition data: " + e.getMessage());
    }
  }

  public NutritionInfo calculateNutritionForIngredient(String category, String ingredient, double quantity) {
    Map<String, NutritionInfo> categoryMap = nutritionData.get(category);
    if (categoryMap == null) {
      return new NutritionInfo(0, 0, 0, 0, 0, 0, 0);
    }

    NutritionInfo baseNutrition = categoryMap.get(ingredient);
    if (baseNutrition == null) {
      return new NutritionInfo(0, 0, 0, 0, 0, 0, 0);
    }

    // Adjust nutrition values based on quantity
    return baseNutrition.multiply(quantity);
  }

  public NutritionInfo calculateNutritionForRecipe(Recipe recipe) {
    NutritionInfo totalNutrition = new NutritionInfo(0, 0, 0, 0, 0, 0, 0);

    for (Ingredient ingredient : recipe.getIngredients()) {
      NutritionInfo ingredientNutrition = calculateNutritionForIngredient(
          ingredient.getCategory(),
          ingredient.getName(),
          ingredient.getQuantity()
      );
      totalNutrition = totalNutrition.add(ingredientNutrition);
    }

    return totalNutrition;
  }

  public NutritionInfo calculateNutritionForMeal(Map<String, Recipe> meals) {
    NutritionInfo totalNutrition = new NutritionInfo(0, 0, 0, 0, 0, 0, 0);

    for (Recipe recipe : meals.values()) {
      totalNutrition = totalNutrition.add(calculateNutritionForRecipe(recipe));
    }

    return totalNutrition;
  }
} 