import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.File;

public class Main {
  private static List<Recipe> recipes;
  private static UserInfoGUI userInfoGUI;
  private static RecipeGUI recipeGUI;
  private static NutritionCalculator nutritionCalculator;
  private static Map<String, Object> userData;

  public static void main(String[] args) {
    try {
      // Initialize components
      initializeComponents();

      // Start with user info GUI
      SwingUtilities.invokeLater(() -> {
        userInfoGUI = new UserInfoGUI();
        userInfoGUI.setVisible(true);
      });
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Error initializing application: " + e.getMessage(),
          "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
  }

  private static void initializeComponents() throws IOException {
    // Initialize nutrition calculator
    nutritionCalculator = new NutritionCalculator();

    // Load recipes
    loadRecipes();
  }

  private static void loadRecipes() throws IOException {
    recipes = new ArrayList<>(); // Initialize the list
    
    // Get the current directory
    File currentDir = new File(".");
    String currentPath = currentDir.getAbsolutePath();
    System.out.println("Starting directory for recipes: " + currentPath);
    
    // Try both possible paths due to nested folder structure
    String[] possiblePaths = {
        "src" + File.separator + "ingredientsSource" + File.separator + "recipes.json",
        "FinalProject_5004" + File.separator + "src" + File.separator + "ingredientsSource" + File.separator + "recipes.json",
        currentPath + File.separator + "src" + File.separator + "ingredientsSource" + File.separator + "recipes.json",
        currentPath + File.separator + "FinalProject_5004" + File.separator + "src" + File.separator + "ingredientsSource" + File.separator + "recipes.json"
    };
    
    boolean fileFound = false;
    IOException lastException = null;
    
    for (String path : possiblePaths) {
        File recipeFile = new File(path);
        System.out.println("Trying to load recipes from: " + recipeFile.getAbsolutePath());
        
        if (recipeFile.exists()) {
            try {
                recipes = JSONParser.readRecipesFromFile(recipeFile.getAbsolutePath());
                System.out.println("Successfully loaded " + recipes.size() + " recipes from: " + recipeFile.getAbsolutePath());
                for (Recipe recipe : recipes) {
                    System.out.println("Loaded recipe: " + recipe.getName() + 
                                     " (Cuisine: " + recipe.getCuisine() + 
                                     ", Calories: " + recipe.getCalories() + 
                                     ", Prep Time: " + recipe.getPrepTime() + " min)");
                }
                fileFound = true;
                break;
            } catch (IOException e) {
                lastException = e;
                System.err.println("Error reading file: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("File not found at: " + recipeFile.getAbsolutePath());
        }
    }
    
    if (!fileFound) {
        String errorMsg = "Could not find or read recipes.json in any of the expected locations";
        if (lastException != null) {
            errorMsg += ": " + lastException.getMessage();
            lastException.printStackTrace();
        }
        throw new IOException(errorMsg);
    }

    if (recipes.isEmpty()) {
        System.out.println("Warning: No recipes were loaded from the file!");
    }
  }

  public static void showRecipeGUI(Map<String, Object> userData) {
    Main.userData = userData;  // Store user data
    
    // Ensure we have recipes
    if (recipes == null || recipes.isEmpty()) {
        try {
            System.out.println("Attempting to reload recipes...");
            loadRecipes();
        } catch (IOException e) {
            System.err.println("Error loading recipes: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error loading recipes: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    System.out.println("Showing Recipe GUI with " + recipes.size() + " recipes");
    
    SwingUtilities.invokeLater(() -> {
      recipeGUI = new RecipeGUI(recipes, userData);
      recipeGUI.setVisible(true);
      if (userInfoGUI != null) {
        userInfoGUI.dispose();
      }
    });
  }

  public static NutritionCalculator getNutritionCalculator() {
    return nutritionCalculator;
  }

  public static Map<String, Object> getUserData() {
    return userData;
  }

  public static List<Recipe> getRecipes() {
    return new ArrayList<>(recipes); // Return a defensive copy
  }

  public static void exitApplication() {
    System.exit(0);
  }
} 