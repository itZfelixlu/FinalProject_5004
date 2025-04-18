import java.util.ArrayList;
import java.util.List;

/**
 * A simple test class to demonstrate the Ingredient class functionality.
 */
public class IngredientTest {

  public static void main(String[] args) {
    System.out.println("Ingredient Test Program");
    System.out.println("====================");

    // Display all available categories
    System.out.println("Available ingredient categories:");
    List<String> categories = Ingredient.getAllCategories();
    for (String category : categories) {
      System.out.println("- " + category);
    }
    System.out.println();

    // Display meat ingredients
    System.out.println("Meat ingredients:");
    List<String> meatNames = Ingredient.getIngredientNamesByCategory("meat");
    for (String name : meatNames) {
      System.out.println("- " + name);
    }
    System.out.println();

    // Create some ingredients and display detailed information
    System.out.println("Chicken breast details:");
    Ingredient chicken = Ingredient.getIngredientByName("Chicken Breast", 2);
    System.out.println(chicken.getDetailedInfo());
    System.out.println();

    // Change cooking method and display calorie changes
    System.out.println("Calorie changes after changing cooking method:");
    System.out.println("Raw chicken breast: " + chicken.getTotalCalories() + " calories");

    chicken.setCookingMethod("grilled");
    System.out.println("Grilled chicken breast: " + chicken.getTotalCalories() + " calories");

    chicken.setCookingMethod("fried");
    System.out.println("Fried chicken breast: " + chicken.getTotalCalories() + " calories");
    System.out.println();

    // Test creating a recipe
    System.out.println("Creating a simple recipe:");
    Recipe grilledChickenSalad = new Recipe(
        "Grilled Chicken Salad",
        "Fresh",
        "American",
        20,
        new ArrayList<>()
    );

    Ingredient grilledChicken = Ingredient.getIngredientByName("Chicken Breast", 1);
    if (grilledChicken != null) {
      grilledChicken.setCookingMethod("grilled");
      grilledChickenSalad.addIngredient(grilledChicken);
    } else {
      System.out.println("Warning: Chicken breast ingredient not found");
    }

    Ingredient spinach = Ingredient.getIngredientByName("Spinach", 2);
    if (spinach != null) {
      grilledChickenSalad.addIngredient(spinach);
    } else {
      System.out.println("Warning: Spinach ingredient not found");
    }

    Ingredient tomato = Ingredient.getIngredientByName("Tomato", 1);
    if (tomato != null) {
      grilledChickenSalad.addIngredient(tomato);
    } else {
      System.out.println("Warning: Tomato ingredient not found");
    }

    // Olive oil might not be in predefined ingredients
    try {
      // Manually create olive oil ingredient
      Ingredient oliveOil = new Ingredient(
          "Olive Oil", 0.5, "tbsp", 120, 0.25, "seasoning",
          "Cold-pressed extra virgin olive oil - healthy fat source");
      grilledChickenSalad.addIngredient(oliveOil);
    } catch (Exception e) {
      System.out.println("Warning: Cannot create olive oil ingredient: " + e.getMessage());
    }

    System.out.println(grilledChickenSalad.getDetailedInfo());
  }
}