import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                return new Ingredient(name, quantity, unit, calories, price, category, cookingMethod, description);
            } else {
                return new Ingredient(name, quantity, unit, calories, price, category, description);
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
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
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

        if (json.trim().startsWith("{") && json.trim().endsWith("}")) {
            json = json.trim().substring(1, json.trim().length() - 1);

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
                        if (json.charAt(i) == '"') {
                            // String value
                            i++; // Skip opening quote
                            int valueStart = i;
                            while (i < json.length() && (json.charAt(i) != '"' || json.charAt(i - 1) == '\\')) i++;
                            String value = json.substring(valueStart, i);
                            i++; // Skip closing quote
                            properties.put(key, value);
                        } else {
                            // Number, boolean, or null value
                            int valueStart = i;
                            while (i < json.length() && json.charAt(i) != ',' && json.charAt(i) != '}') i++;
                            String value = json.substring(valueStart, i).trim();
                            properties.put(key, value);
                        }
                    }
                } else {
                    i++; // Skip non-key character
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

        // Parse JSON object
        if (json.trim().startsWith("{") && json.trim().endsWith("}")) {
            json = json.trim().substring(1, json.trim().length() - 1);

            // Split into categories
            Map<String, String> categories = splitJSONSections(json);

            for (Map.Entry<String, String> categoryEntry : categories.entrySet()) {
                String category = categoryEntry.getKey();
                String categoryJson = categoryEntry.getValue();

                Map<String, String> ingredients = splitJSONSections(categoryJson);
                Map<String, NutritionInfo> categoryMap = new HashMap<>();

                for (Map.Entry<String, String> ingredientEntry : ingredients.entrySet()) {
                    String ingredient = ingredientEntry.getKey();
                    String ingredientJson = ingredientEntry.getValue();

                    Map<String, String> nutritionValues = parseJSONObject(ingredientJson);
                    try {
                        NutritionInfo nutritionInfo = new NutritionInfo(
                            Double.parseDouble(nutritionValues.get("protein")),
                            Double.parseDouble(nutritionValues.get("fat")),
                            Double.parseDouble(nutritionValues.get("carbohydrates")),
                            Double.parseDouble(nutritionValues.get("fiber")),
                            Double.parseDouble(nutritionValues.get("sugar")),
                            Double.parseDouble(nutritionValues.get("sodium")),
                            Double.parseDouble(nutritionValues.get("calories"))
                        );
                        categoryMap.put(ingredient, nutritionInfo);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in nutrition data for " + ingredient);
                    }
                }

                result.put(category, categoryMap);
            }
        }

        return result;
    }
}