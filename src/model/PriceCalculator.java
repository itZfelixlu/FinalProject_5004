package model;

/**
 * Handles price calculations for recipes and ingredients.
 * Provides methods to calculate total prices, apply unit conversions,
 * and consolidate prices across multiple ingredients.
 */
public class PriceCalculator {
    
    /**
     * Calculates the total price for a recipe by summing all ingredient prices.
     *
     * @param recipe Recipe to calculate price for
     * @return Total price of the recipe
     */
    public double calculateRecipePrice(Recipe recipe) {
        if (recipe == null) {
            System.out.println("Warning: Null recipe provided to calculateRecipePrice");
            return 0.0;
        }

        return recipe.getIngredients().stream()
            .mapToDouble(this::calculateIngredientPrice)
            .sum();
    }

    /**
     * Calculates the total price for an ingredient based on quantity and unit conversion.
     *
     * @param ingredient Ingredient to calculate price for
     * @return Total price of the ingredient
     */
    public double calculateIngredientPrice(Ingredient ingredient) {
        if (ingredient == null) {
            System.out.println("Warning: Null ingredient provided to calculateIngredientPrice");
            return 0.0;
        }

        String unit = ingredient.getUnit().toLowerCase();
        double quantity = ingredient.getQuantity();
        double pricePerUnit = ingredient.getPricePerUnit();

        // For items priced per piece, use direct calculation
        if (unit.contains("piece") || unit.contains("egg")) {
            return pricePerUnit * quantity;
        }

        // For weight-based items, convert price from per 100g to per gram
        double basePrice = pricePerUnit / 100.0;
        return basePrice * quantity * getUnitMultiplier(unit);
    }

    /**
     * Gets the conversion multiplier for different units.
     *
     * @param unit Unit of measurement
     * @return Conversion multiplier
     */
    private double getUnitMultiplier(String unit) {
        String baseUnit = extractBaseUnit(unit);
        switch (baseUnit) {
            case "tablespoon":
                return 15.0; // 1 tablespoon = 15g
            case "teaspoon":
                return 5.0;  // 1 teaspoon = 5g
            case "cup":
                return 240.0; // 1 cup = 240g
            case "ml":
                return 1.0;  // Assume 1ml = 1g for liquids
            default:
                return 1.0;  // For units already in standard form (g, piece, etc.)
        }
    }

    /**
     * Extracts the base unit from a compound unit string.
     * For example: "tablespoon (15g)" -> "tablespoon"
     *
     * @param unit Unit string to parse
     * @return Base unit
     */
    private String extractBaseUnit(String unit) {
        int parenIndex = unit.indexOf('(');
        return parenIndex > 0 ? unit.substring(0, parenIndex).trim() : unit;
    }
} 