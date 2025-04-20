package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;



/**
 * A simple JSON parser for reading ingredient and calorie modifier data.
 * Custom implementation that avoids external library dependencies.
 */
public class JSONParser {

    /**
     * Reads ingredients from a JSON file.
     *
     * @param filePath Path to the JSON file
     * @return List of Ingredient objects
     * @throws IOException If file read fails
     */
    public static List<Ingredient> readIngredientsFromFile(String filePath) throws IOException {
        List<Ingredient> ingredients = new ArrayList<>();
        String json = readFile(filePath);

        // Parse JSON array
        if (json.trim().startsWith("[") && json.trim().endsWith("]")) {
            json = json.trim().substring(1, json.trim().length() - 1);

            // Split array into individual objects
            List<String> objects = splitJSONObjects(json);

            for (String obj : objects) {
                Map<String, String> properties = parseJSONObject(obj);
                try {
                    Ingredient ingredient = createIngredientFromProperties(properties);
                    if (ingredient != null) {
                        ingredients.add(ingredient);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing ingredient: " + e.getMessage());
                }
            }
        }

        return ingredients;
    }

    /**
     * Reads calorie modifiers from a JSON file.
     *
     * @param filePath Path to the JSON file
     * @return Nested map of calorie modifiers
     * @throws IOException If file read fails
     */
    public static Map<String, Map<String, Map<String, Double>>> readCalorieModifiers(String filePath) throws IOException {
        Map<String, Map<String, Map<String, Double>>> result = new HashMap<>();
        String json = readFile(filePath);

        // Parse JSON object
        if (json.trim().startsWith("{") && json.trim().endsWith("}")) {
            json = json.trim().substring(1, json.trim().length() - 1);

            // Split into top-level sections (default, categories, subcategories, ingredients)
            Map<String, String> sections = splitJSONSections(json);

            for (Map.Entry<String, String> entry : sections.entrySet()) {
                String section = entry.getKey();
                String sectionJson = entry.getValue();

                if (section.equals("default")) {
                    // Parse default section
                    Map<String, Map<String, Double>> defaultMap = new HashMap<>();
                    Map<String, String> defaultValues = parseJSONObject(sectionJson);
                    Map<String, Double> methodMap = new HashMap<>();

                    for (Map.Entry<String, String> defaultEntry : defaultValues.entrySet()) {
                        try {
                            methodMap.put(defaultEntry.getKey(), Double.parseDouble(defaultEntry.getValue()));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid number format: " + defaultEntry.getValue());
                        }
                    }

                    defaultMap.put("default", methodMap);
                    result.put("default", defaultMap);
                } else {
                    // Parse other sections (categories, subcategories, ingredients)
                    Map<String, Map<String, Double>> sectionMap = parseNestedSection(sectionJson);
                    result.put(section, sectionMap);
                }
            }
        }

        return result;
    }

    /**
     * Parses a nested section of the JSON file.
     *
     * @param json JSON string representing a nested object
     * @return Map of parsed values
     */
    private static Map<String, Map<String, Double>> parseNestedSection(String json) {
        Map<String, Map<String, Double>> result = new HashMap<>();

        if (json.trim().startsWith("{") && json.trim().endsWith("}")) {
            json = json.trim().substring(1, json.trim().length() - 1);

            // Split into subsections
            Map<String, String> subsections = splitJSONSections(json);

            for (Map.Entry<String, String> entry : subsections.entrySet()) {
                String subsection = entry.getKey();
                String subsectionJson = entry.getValue();

                Map<String, String> values = parseJSONObject(subsectionJson);
                Map<String, Double> methodMap = new HashMap<>();

                for (Map.Entry<String, String> valueEntry : values.entrySet()) {
                    try {
                        methodMap.put(valueEntry.getKey(), Double.parseDouble(valueEntry.getValue()));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format: " + valueEntry.getValue());
                    }
                }

                result.put(subsection, methodMap);
            }
        }

        return result;
    }

    /**
     * Creates an Ingredient object from property map.
     *
     * @param properties Map of ingredient properties
     * @return Ingredient object or null if invalid
     */
    private static Ingredient createIngredientFromProperties(Map<String, String> properties) {
        String name = properties.get("name");
        String quantityStr = properties.get("quantity");
        String unit = properties.get("unit");
        String caloriesStr = properties.get("caloriesPerUnit");
        String priceStr = properties.get("pricePerUnit");
        String category = properties.get("category");
        String cookingMethod = properties.get("cookingMethod");
        String description = properties.get("description");

        if (name == null || quantityStr == null || unit == null ||
            caloriesStr == null || priceStr == null || category == null) {
            return null;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            int calories = Integer.parseInt(caloriesStr);
            double price = Double.parseDouble(priceStr);

            if (cookingMethod != null) {
                return new Ingredient(name, quantity, unit, calories, price, category, cookingMethod, description, price);
            } else {
                return new Ingredient(name, quantity, unit, calories, price, category, description, price);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in ingredient properties");
        }
    }

    /**
     * Reads the contents of a file.
     *
     * @param filePath Path to the file
     * @return File contents as string
     * @throws IOException If file read fails
     */
    private static String readFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        String result = content.toString();
        if (result.isEmpty()) {
            throw new IOException("File is empty: " + filePath);
        }

        return result;
    }

    /**
     * Splits a JSON string into individual objects.
     *
     * @param json JSON array content
     * @return List of JSON object strings
     */
    private static List<String> splitJSONObjects(String json) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        StringBuilder currentObject = new StringBuilder();

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '{') {
                depth++;
                currentObject.append(c);
            } else if (c == '}') {
                depth--;
                currentObject.append(c);

                if (depth == 0) {
                    objects.add(currentObject.toString().trim());
                    currentObject = new StringBuilder();

                    // Skip comma and whitespace
                    while (i + 1 < json.length() && (json.charAt(i + 1) == ',' || Character.isWhitespace(json.charAt(i + 1)))) {
                        i++;
                    }
                }
            } else if (depth > 0) {
                currentObject.append(c);
            }
        }

        return objects;
    }

    /**
     * Splits a JSON object into sections.
     *
     * @param json JSON object content
     * @return Map of section name to section JSON content
     */
    private static Map<String, String> splitJSONSections(String json) {
        Map<String, String> sections = new HashMap<>();
        int i = 0;

        while (i < json.length()) {
            // Find start of key
            while (i < json.length() && (Character.isWhitespace(json.charAt(i)) || json.charAt(i) == ',')) {
                i++;
            }

            if (i >= json.length()) break;

            // Parse key
            if (json.charAt(i) == '"') {
                i++; // Skip opening quote
                int keyStart = i;
                while (i < json.length() && json.charAt(i) != '"') i++;
                String key = json.substring(keyStart, i);
                i++; // Skip closing quote

                // Find colon
                while (i < json.length() && json.charAt(i) != ':') i++;
                i++; // Skip colon

                // Skip whitespace
                while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;

                // Parse value
                if (i < json.length()) {
                    if (json.charAt(i) == '{') {
                        // Object value
                        int depth = 1;
                        int valueStart = i;
                        i++; // Skip opening brace

                        while (i < json.length() && depth > 0) {
                            if (json.charAt(i) == '{') depth++;
                            else if (json.charAt(i) == '}') depth--;
                            i++;
                        }

                        String value = json.substring(valueStart, i);
                        sections.put(key, value);
                    } else {
                        // Other value (string, number, boolean, null)
                        int valueStart = i;
                        boolean inString = false;

                        while (i < json.length()) {
                            if (json.charAt(i) == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                                inString = !inString;
                            } else if (!inString && (json.charAt(i) == ',' || json.charAt(i) == '}')) {
                                break;
                            }
                            i++;
                        }

                        String value = json.substring(valueStart, i).trim();
                        sections.put(key, value);
                    }
                }
            } else {
                i++; // Skip non-key character
            }
        }

        return sections;
    }

    /**
     * Parses a JSON object into a map of properties.
     *
     * @param json JSON object string
     * @return Map of property name to value
     */
    private static Map<String, String> parseJSONObject(String json) {
        Map<String, String> properties = new HashMap<>();
        if (!json.trim().startsWith("{") || !json.trim().endsWith("}")) {
            return properties;
        }

        json = json.trim().substring(1, json.trim().length() - 1);
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean inKey = true;
        boolean inQuotes = false;
        int nestedDepth = 0;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
                if (!inKey) {
                    value.append(c);
                }
            } else if (c == ':' && !inQuotes && nestedDepth == 0) {
                inKey = false;
            } else if ((c == ',' && !inQuotes && nestedDepth == 0) || i == json.length() - 1) {
                if (i == json.length() - 1 && c != ',') {
                    if (!inKey) {
                        value.append(c);
                    } else {
                        key.append(c);
                    }
                }
                
                String keyStr = key.toString().trim();
                String valueStr = value.toString().trim();
                
                if (keyStr.startsWith("\"") && keyStr.endsWith("\"")) {
                    keyStr = keyStr.substring(1, keyStr.length() - 1);
                }
                
                properties.put(keyStr, valueStr);
                
                key = new StringBuilder();
                value = new StringBuilder();
                inKey = true;
            } else if (c == '{' || c == '[') {
                nestedDepth++;
                if (!inKey) {
                    value.append(c);
                }
            } else if (c == '}' || c == ']') {
                nestedDepth--;
                if (!inKey) {
                    value.append(c);
                }
            } else {
                if (inKey) {
                    key.append(c);
                } else {
                    value.append(c);
                }
            }
        }

        return properties;
    }

    /**
     * Reads micro-nutrition data from a JSON file.
     *
     * @param filePath Path to the JSON file
     * @return Map of category to ingredient nutrition info
     * @throws IOException If file read fails
     */
    public static Map<String, Map<String, NutritionInfo>> readMicroNutritionData(String filePath) throws IOException {
        Map<String, Map<String, NutritionInfo>> result = new HashMap<>();
        String json = readFile(filePath);
        
        if (json == null || json.trim().isEmpty()) {
            throw new IOException("Empty or null JSON content from file: " + filePath);
        }

        // Parse JSON object
        if (json.trim().startsWith("{") && json.trim().endsWith("}")) {
            json = json.trim().substring(1, json.trim().length() - 1);

            // Split into categories
            Map<String, String> categories = splitJSONSections(json);

            for (Map.Entry<String, String> entry : categories.entrySet()) {
                String category = entry.getKey();
                String categoryJson = entry.getValue();

                if (category == null || categoryJson == null) {
                    System.err.println("Skipping null category or data in nutrition file");
                    continue;
                }

                try {
                    Map<String, String> ingredients = parseJSONObject(categoryJson);
                    Map<String, NutritionInfo> categoryMap = new HashMap<>();

                    for (Map.Entry<String, String> ingredientEntry : ingredients.entrySet()) {
                        String ingredient = ingredientEntry.getKey();
                        String nutritionJson = ingredientEntry.getValue();

                        if (ingredient == null || nutritionJson == null) {
                            System.err.println("Skipping null ingredient or nutrition data");
                            continue;
                        }

                        try {
                            Map<String, String> nutritionValues = parseJSONObject(nutritionJson);
                            NutritionInfo info = createNutritionInfoFromValues(nutritionValues);
                            if (info != null) {
                                categoryMap.put(ingredient, info);
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing nutrition data for ingredient " + ingredient + ": " + e.getMessage());
                        }
                    }

                    result.put(category, categoryMap);
                } catch (Exception e) {
                    System.err.println("Error parsing category " + category + ": " + e.getMessage());
                }
            }
        }

        return result;
    }

    private static NutritionInfo createNutritionInfoFromValues(Map<String, String> values) {
        try {
            double protein = parseDoubleValue(values.get("protein"));
            double fat = parseDoubleValue(values.get("fat"));
            double carbohydrates = parseDoubleValue(values.get("carbohydrates"));
            double fiber = parseDoubleValue(values.get("fiber"));
            double sugar = parseDoubleValue(values.get("sugar"));
            double sodium = parseDoubleValue(values.get("sodium"));

            return new NutritionInfo(protein, fat, carbohydrates, fiber, sugar, sodium);
        } catch (Exception e) {
            System.err.println("Error creating NutritionInfo: " + e.getMessage());
            return new NutritionInfo(0, 0, 0, 0, 0, 0);
        }
    }

    private static double parseDoubleValue(Object value) {
        if (value == null) return 0.0;
        try {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            System.err.println("Error parsing double value: " + value);
            return 0.0;
        }
    }

    /**
     * Reads recipes from a JSON file.
     *
     * @param filePath Path to the JSON file
     * @return List of Recipe objects
     * @throws IOException If file read fails
     */
    public static List<Recipe> readRecipesFromFile(String filePath) throws IOException {
        List<Recipe> recipes = new ArrayList<>();
        String json = readFile(filePath);

        // Parse JSON array
        if (json.trim().startsWith("[") && json.trim().endsWith("]")) {
            json = json.trim().substring(1, json.trim().length() - 1);

            // Split array into individual objects
            List<String> objects = splitJSONObjects(json);

            for (String obj : objects) {
                Map<String, String> properties = parseJSONObject(obj);
                try {
                    Recipe recipe = createRecipeFromProperties(properties);
                    if (recipe != null) {
                        recipes.add(recipe);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing recipe: " + e.getMessage());
                }
            }
        }

        return recipes;
    }

    /**
     * Creates a Recipe object from property map.
     *
     * @param properties Map of recipe properties
     * @return Recipe object or null if invalid
     */
    private static Recipe createRecipeFromProperties(Map<String, String> properties) {
        String name = properties.get("name");
        String flavor = properties.get("flavor");
        String flavorTagsStr = properties.get("flavorTags");
        String cuisine = properties.get("cuisine");
        String prepTimeStr = properties.get("prepTime");
        String ingredientsStr = properties.get("ingredients");

        if (name == null || flavor == null || cuisine == null ||
            prepTimeStr == null || ingredientsStr == null || flavorTagsStr == null) {
            return null;
        }

        try {
            int prepTime = Integer.parseInt(prepTimeStr);
            List<Ingredient> ingredients = parseIngredients(ingredientsStr);
            List<String> flavorTags = parseFlavorTags(flavorTagsStr);

            return new Recipe(
                name,
                flavor,
                flavorTags,
                cuisine,
                prepTime,
                ingredients
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in recipe properties");
        }
    }

    /**
     * Parses ingredients from a JSON string.
     *
     * @param ingredientsStr JSON string of ingredients
     * @return List of Ingredient objects
     */
    private static List<Ingredient> parseIngredients(String ingredientsStr) {
        List<Ingredient> ingredients = new ArrayList<>();
        if (ingredientsStr.trim().startsWith("[") && ingredientsStr.trim().endsWith("]")) {
            ingredientsStr = ingredientsStr.trim().substring(1, ingredientsStr.trim().length() - 1);
            List<String> ingredientObjects = splitJSONObjects(ingredientsStr);

            for (String ingredientObj : ingredientObjects) {
                Map<String, String> properties = parseJSONObject(ingredientObj);
                Ingredient ingredient = createIngredientFromProperties(properties);
                if (ingredient != null) {
                    ingredients.add(ingredient);
                }
            }
        }
        return ingredients;
    }

    /**
     * Parses flavor tags from a JSON string.
     *
     * @param flavorTagsStr JSON string of flavor tags
     * @return List of flavor tags
     */
    private static List<String> parseFlavorTags(String flavorTagsStr) {
        List<String> flavorTags = new ArrayList<>();
        if (flavorTagsStr.trim().startsWith("[") && flavorTagsStr.trim().endsWith("]")) {
            flavorTagsStr = flavorTagsStr.trim().substring(1, flavorTagsStr.trim().length() - 1);
            String[] tags = flavorTagsStr.split(",");
            for (String tag : tags) {
                tag = tag.trim();
                if (tag.startsWith("\"") && tag.endsWith("\"")) {
                    tag = tag.substring(1, tag.length() - 1);
                }
                flavorTags.add(tag);
            }
        }
        return flavorTags;
    }
}