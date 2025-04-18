import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of an ingredient with calorie modifications based on cooking methods.
 * Supports loading ingredient data from JSON files and provides category-based organization.
 */
public class Ingredient extends AbstractIngredient {
    private static Map<String, Map<String, Map<String, Double>>> calorieModifiers;
    private static List<Ingredient> meatIngredients;
    private static List<Ingredient> vegetableIngredients;
    private static List<Ingredient> fruitIngredients;
    private static List<Ingredient> dairyIngredients;
    private static List<Ingredient> seasoningIngredients;
    private String description;

    // Path to JSON files
    private static final String JSON_BASE_PATH = "src/ingredientsSource/";

    static {
        // Initialize calorie modifiers and ingredients
        try {
            // Load calorie modifiers from JSON
            calorieModifiers = JSONParser.readCalorieModifiers(JSON_BASE_PATH + "CalorieModifier.json");
            
            // Load ingredients from JSON files
            loadIngredientsFromJSON();
        } catch (IOException e) {
            System.err.println("Error loading data from JSON files: " + e.getMessage());
            
            // Initialize with defaults if JSON loading fails
            initializeCalorieModifiers();
            initializeDefaultIngredients();
        }
    }

    /**
     * Creates a new ingredient.
     *
     * @param name Name of the ingredient
     * @param quantity Quantity of the ingredient
     * @param unit Unit of measurement
     * @param caloriesPerUnit Calories per unit (raw)
     * @param pricePerUnit Price per unit
     * @param category Food category
     * @param description Brief description of the ingredient
     */
    public Ingredient(String name, double quantity, String unit,
                     int caloriesPerUnit, double pricePerUnit,
                     String category, String description) {
        super(name, quantity, unit, caloriesPerUnit, pricePerUnit, category);
        this.description = description;
    }

    /**
     * Creates a new ingredient with a specified cooking method.
     *
     * @param name Name of the ingredient
     * @param quantity Quantity of the ingredient
     * @param unit Unit of measurement
     * @param caloriesPerUnit Calories per unit (raw)
     * @param pricePerUnit Price per unit
     * @param category Food category
     * @param cookingMethod Method of preparation
     * @param description Brief description of the ingredient
     */
    public Ingredient(String name, double quantity, String unit,
                     int caloriesPerUnit, double pricePerUnit,
                     String category, String cookingMethod, String description) {
        super(name, quantity, unit, caloriesPerUnit, pricePerUnit, category, cookingMethod);
        this.description = description;
    }

    /**
     * Gets the description of this ingredient.
     *
     * @return Description text
     */
    public String getDescription() {
        return description;
    }

    /**
     * Calculates calories per unit modified by cooking method.
     * Applies modifiers based on ingredient name, category and cooking method.
     *
     * @return Modified calories per unit
     */
    @Override
    public double getModifiedCaloriesPerUnit() {
        double modifier = 1.0; // Default modifier
        String lowerCaseName = name.toLowerCase();

        // First check if there's a specific modifier for this ingredient
        if (calorieModifiers.containsKey("ingredients")) {
            Map<String, Map<String, Double>> ingredients = calorieModifiers.get("ingredients");
            if (ingredients.containsKey(lowerCaseName)) {
                Map<String, Double> methods = ingredients.get(lowerCaseName);
                if (methods.containsKey(cookingMethod)) {
                    modifier = methods.get(cookingMethod);
                }
            }
        }
        // Next check if there's a modifier for this subcategory
        else if (category.equals("meat") && 
                (lowerCaseName.contains("chicken") || lowerCaseName.contains("turkey")) && 
                calorieModifiers.containsKey("subcategories")) {
            Map<String, Map<String, Double>> subcategories = calorieModifiers.get("subcategories");
            if (subcategories.containsKey("poultry")) {
                Map<String, Double> methods = subcategories.get("poultry");
                if (methods.containsKey(cookingMethod)) {
                    modifier = methods.get(cookingMethod);
                }
            }
        }
        else if (category.equals("meat") && 
                (lowerCaseName.contains("salmon") || lowerCaseName.contains("tuna") || 
                lowerCaseName.contains("cod") || lowerCaseName.contains("fish")) && 
                calorieModifiers.containsKey("subcategories")) {
            Map<String, Map<String, Double>> subcategories = calorieModifiers.get("subcategories");
            if (subcategories.containsKey("fish")) {
                Map<String, Double> methods = subcategories.get("fish");
                if (methods.containsKey(cookingMethod)) {
                    modifier = methods.get(cookingMethod);
                }
            }
        }
        else if (category.equals("vegetable") && 
                calorieModifiers.containsKey("subcategories")) {
            Map<String, Map<String, Double>> subcategories = calorieModifiers.get("subcategories");
            if (subcategories.containsKey("vegetable")) {
                Map<String, Double> methods = subcategories.get("vegetable");
                if (methods.containsKey(cookingMethod)) {
                    modifier = methods.get(cookingMethod);
                }
            }
        }
        else if (category.equals("fruit") && 
                calorieModifiers.containsKey("subcategories")) {
            Map<String, Map<String, Double>> subcategories = calorieModifiers.get("subcategories");
            if (subcategories.containsKey("fruit")) {
                Map<String, Double> methods = subcategories.get("fruit");
                if (methods.containsKey(cookingMethod)) {
                    modifier = methods.get(cookingMethod);
                }
            }
        }
        // Then check if there's a modifier for this category
        else if (calorieModifiers.containsKey("categories")) {
            Map<String, Map<String, Double>> categories = calorieModifiers.get("categories");
            if (categories.containsKey(category)) {
                Map<String, Double> methods = categories.get(category);
                if (methods.containsKey(cookingMethod)) {
                    modifier = methods.get(cookingMethod);
                }
            }
        }
        // Finally, fall back to the default modifier
        else if (calorieModifiers.containsKey("default")) {
            Map<String, Double> defaultMethods = calorieModifiers.get("default").get("default");
            if (defaultMethods.containsKey(cookingMethod)) {
                modifier = defaultMethods.get(cookingMethod);
            }
        }

        return caloriesPerUnit * modifier;
    }

    /**
     * Returns formatted detailed information about this ingredient.
     *
     * @return Formatted string with ingredient details
     */
    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "%s (%.2f %s, %s)\n" +
                "Category: %s\n" +
                "Cooking Method: %s\n" +
                "Calories: %.2f per %s (%.2f modified)\n" +
                "Price: $%.2f per %s ($%.2f total)\n" +
                "Description: %s",
                name, quantity, unit, cookingMethod,
                category, cookingMethod,
                (double) caloriesPerUnit, unit, getModifiedCaloriesPerUnit(),
                pricePerUnit, unit, getTotalPrice(),
                description));
        return sb.toString();
    }

    /**
     * Loads ingredients from JSON files into category-based lists.
     * 
     * @throws IOException If errors occur reading JSON files
     */
    private static void loadIngredientsFromJSON() throws IOException {
        // Initialize lists
        meatIngredients = new ArrayList<>();
        vegetableIngredients = new ArrayList<>();
        fruitIngredients = new ArrayList<>();
        dairyIngredients = new ArrayList<>();
        seasoningIngredients = new ArrayList<>();
        
        // Load ingredients from JSON files
        try {
            meatIngredients = JSONParser.readIngredientsFromFile(JSON_BASE_PATH + "meat.json");
        } catch (IOException e) {
            System.err.println("Error loading meat ingredients: " + e.getMessage());
        }
        
        try {
            vegetableIngredients = JSONParser.readIngredientsFromFile(JSON_BASE_PATH + "vegetable.json");
        } catch (IOException e) {
            System.err.println("Error loading vegetable ingredients: " + e.getMessage());
        }
        
        try {
            fruitIngredients = JSONParser.readIngredientsFromFile(JSON_BASE_PATH + "fruit.json");
        } catch (IOException e) {
            System.err.println("Error loading fruit ingredients: " + e.getMessage());
        }
        
        try {
            dairyIngredients = JSONParser.readIngredientsFromFile(JSON_BASE_PATH + "dairy.json");
        } catch (IOException e) {
            System.err.println("Error loading dairy ingredients: " + e.getMessage());
        }
        
        try {
            seasoningIngredients = JSONParser.readIngredientsFromFile(JSON_BASE_PATH + "seasoning.json");
        } catch (IOException e) {
            System.err.println("Error loading seasoning ingredients: " + e.getMessage());
        }
    }

    /**
     * Initializes default calorie modifiers when JSON loading fails.
     */
    private static void initializeCalorieModifiers() {
        calorieModifiers = new HashMap<>();
        
        // Default modifiers
        Map<String, Map<String, Double>> defaultCategory = new HashMap<>();
        Map<String, Double> defaultMethods = new HashMap<>();
        defaultMethods.put("raw", 1.0);
        defaultMethods.put("steamed", 1.0);
        defaultMethods.put("boiled", 1.0);
        defaultMethods.put("grilled", 1.2);
        defaultMethods.put("fried", 1.5);
        defaultMethods.put("deep-fried", 2.0);
        defaultMethods.put("baked", 1.1);
        defaultMethods.put("roasted", 1.2);
        defaultCategory.put("default", defaultMethods);
        calorieModifiers.put("default", defaultCategory);
        
        // Categories
        Map<String, Map<String, Double>> categories = new HashMap<>();
        
        // Meat category
        Map<String, Double> meatMethods = new HashMap<>();
        meatMethods.put("grilled", 1.3);
        meatMethods.put("fried", 1.7);
        meatMethods.put("sauteed", 1.4);
        meatMethods.put("roasted", 1.3);
        meatMethods.put("braised", 1.15);
        categories.put("meat", meatMethods);
        
        calorieModifiers.put("categories", categories);
    }
    
    /**
     * Initializes basic default ingredients when JSON loading fails.
     */
    private static void initializeDefaultIngredients() {
        // Initialize lists
        meatIngredients = new ArrayList<>();
        vegetableIngredients = new ArrayList<>();
        fruitIngredients = new ArrayList<>();
        dairyIngredients = new ArrayList<>();
        seasoningIngredients = new ArrayList<>();
        
        // Add a few basic ingredients to each category
        meatIngredients.add(new Ingredient("Chicken Breast", 1, "100g", 165, 4.29, "meat", 
                "Boneless, skinless chicken breast - lean protein source"));
        
        vegetableIngredients.add(new Ingredient("Broccoli", 1, "100g", 34, 1.99, "vegetable", 
                "Fresh broccoli florets - high in fiber and vitamin C"));
        
        fruitIngredients.add(new Ingredient("Apple", 1, "100g", 52, 0.99, "fruit", 
                "Fresh apple - good source of fiber and vitamin C"));
        
        dairyIngredients.add(new Ingredient("Cheddar Cheese", 1, "100g", 402, 5.49, "dairy", 
                "Aged cheddar cheese - rich in calcium and protein"));
        
        seasoningIngredients.add(new Ingredient("Salt", 1, "tsp", 0, 0.05, "seasoning", 
                "Table salt - basic flavor enhancer"));
    }
    
    /**
     * Gets all ingredients of a specific category.
     * 
     * @param category Category name
     * @return List of ingredients in the category
     */
    public static List<Ingredient> getIngredientsByCategory(String category) {
        switch(category.toLowerCase()) {
            case "meat":
                return new ArrayList<>(meatIngredients);
            case "vegetable":
                return new ArrayList<>(vegetableIngredients);
            case "fruit":
                return new ArrayList<>(fruitIngredients);
            case "dairy":
                return new ArrayList<>(dairyIngredients);
            case "seasoning":
                return new ArrayList<>(seasoningIngredients);
            default:
                return new ArrayList<>();
        }
    }
    
    /**
     * Gets an ingredient by name.
     * 
     * @param name Ingredient name
     * @return Ingredient if found, null otherwise
     */
    public static Ingredient getIngredientByName(String name) {
        String lowerName = name.toLowerCase();
        
        // Check all categories
        for (Ingredient ingredient : meatIngredients) {
            if (ingredient.getName().toLowerCase().equals(lowerName)) {
                return createCopy(ingredient, 1);
            }
        }
        
        for (Ingredient ingredient : vegetableIngredients) {
            if (ingredient.getName().toLowerCase().equals(lowerName)) {
                return createCopy(ingredient, 1);
            }
        }
        
        for (Ingredient ingredient : fruitIngredients) {
            if (ingredient.getName().toLowerCase().equals(lowerName)) {
                return createCopy(ingredient, 1);
            }
        }
        
        for (Ingredient ingredient : dairyIngredients) {
            if (ingredient.getName().toLowerCase().equals(lowerName)) {
                return createCopy(ingredient, 1);
            }
        }
        
        for (Ingredient ingredient : seasoningIngredients) {
            if (ingredient.getName().toLowerCase().equals(lowerName)) {
                return createCopy(ingredient, 1);
            }
        }
        
        return null;
    }
    
    /**
     * Gets an ingredient by name with specified quantity.
     * 
     * @param name Ingredient name
     * @param quantity Desired quantity
     * @return Ingredient if found, null otherwise
     */
    public static Ingredient getIngredientByName(String name, double quantity) {
        Ingredient ingredient = getIngredientByName(name);
        if (ingredient != null) {
            return createCopy(ingredient, quantity);
        }
        return null;
    }
    
    /**
     * Creates a copy of an ingredient with a new quantity.
     * 
     * @param original Original ingredient
     * @param quantity New quantity
     * @return New Ingredient instance
     */
    private static Ingredient createCopy(Ingredient original, double quantity) {
        return new Ingredient(
            original.getName(),
            quantity,
            original.getUnit(),
            original.getCaloriesPerUnit(),
            original.getPricePerUnit(),
            original.getCategory(),
            original.getCookingMethod(),
            original.getDescription()
        );
    }
    
    /**
     * Returns names of all ingredients in a category.
     * 
     * @param category Category name
     * @return List of ingredient names
     */
    public static List<String> getIngredientNamesByCategory(String category) {
        List<Ingredient> ingredients = getIngredientsByCategory(category);
        List<String> names = new ArrayList<>();
        
        for (Ingredient ingredient : ingredients) {
            names.add(ingredient.getName());
        }
        
        return names;
    }
    
    /**
     * Returns a list of all available food categories.
     * 
     * @return List of category names
     */
    public static List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("meat");
        categories.add("vegetable");
        categories.add("fruit");
        categories.add("dairy");
        categories.add("seasoning");
        return categories;
    }
} 