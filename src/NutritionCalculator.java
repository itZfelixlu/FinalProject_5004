import java.util.Map;
import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.FileReader;
import java.io.BufferedReader;

public class NutritionCalculator {
  private Map<String, Map<String, NutritionInfo>> nutritionData;

  public NutritionCalculator() {
    this.nutritionData = new HashMap<>();
    loadNutritionData();
  }

  private void loadNutritionData() {
    try {
      // Read the micro_nutrition.json file
      BufferedReader reader = new BufferedReader(new FileReader("src/ingredientsSource/micro_nutrition.json"));
      StringBuilder jsonContent = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        jsonContent.append(line);
      }
      reader.close();

      // Parse the JSON data
      JSONObject jsonObject = new JSONObject(jsonContent.toString());

      // Process each category
      for (String category : jsonObject.keySet()) {
        JSONObject categoryData = jsonObject.getJSONObject(category);
        Map<String, NutritionInfo> categoryMap = new HashMap<>();

        // Process each ingredient in the category
        for (String ingredient : categoryData.keySet()) {
          JSONObject ingredientData = categoryData.getJSONObject(ingredient);
          NutritionInfo nutritionInfo = new NutritionInfo(
              ingredientData.getDouble("protein"),
              ingredientData.getDouble("fat"),
              ingredientData.getDouble("carbohydrates"),
              ingredientData.getDouble("fiber"),
              ingredientData.getDouble("sugar"),
              ingredientData.getDouble("sodium"),
              ingredientData.getDouble("calories")
          );
          categoryMap.put(ingredient, nutritionInfo);
        }
        nutritionData.put(category, categoryMap);
      }
    } catch (Exception e) {
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