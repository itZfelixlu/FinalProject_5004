package model;


/**
 * Abstract base class implementing common functionality for all ingredients.
 * Provides basic validation and standard methods for ingredient properties.
 */
public abstract class AbstractIngredient implements IIngredient {
  protected String name;
  protected double quantity;
  protected String unit;
  protected int caloriesPerUnit;
  protected double pricePerUnit;
  protected String category;
  protected String cookingMethod;

  /**
   * Creates a new ingredient with default "raw" cooking method.
   *
   * @param name Name of the ingredient
   * @param quantity Quantity amount
   * @param unit Unit of measurement
   * @param caloriesPerUnit Calories per unit
   * @param pricePerUnit Price per unit
   * @param category Food category
   */
  public AbstractIngredient(String name, double quantity, String unit,
      int caloriesPerUnit, double pricePerUnit, String category) {
    this(name, quantity, unit, caloriesPerUnit, pricePerUnit, category, "raw");
  }

  /**
   * Creates a new ingredient with specified cooking method.
   *
   * @param name Name of the ingredient
   * @param quantity Quantity amount
   * @param unit Unit of measurement
   * @param caloriesPerUnit Calories per unit
   * @param pricePerUnit Price per unit
   * @param category Food category
   * @param cookingMethod Method of preparation
   */
  public AbstractIngredient(String name, double quantity, String unit,
      int caloriesPerUnit, double pricePerUnit,
      String category, String cookingMethod) {
    validateInputs(name, quantity, unit, caloriesPerUnit, pricePerUnit, category);

    this.name = name.trim();
    this.quantity = quantity;
    this.unit = unit.trim();
    this.caloriesPerUnit = caloriesPerUnit;
    this.pricePerUnit = pricePerUnit;
    this.category = category.trim().toLowerCase();
    this.cookingMethod = cookingMethod.trim().toLowerCase();
  }

  /**
   * Validates input parameters for ingredient creation.
   *
   * @param name Name to validate
   * @param quantity Quantity to validate
   * @param unit Unit to validate
   * @param caloriesPerUnit Calories to validate
   * @param pricePerUnit Price to validate
   * @param category Category to validate
   * @throws IllegalArgumentException If any inputs are invalid
   */
  private void validateInputs(String name, double quantity, String unit,
      int caloriesPerUnit, double pricePerUnit, String category) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Ingredient name cannot be empty");
    }
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    if (unit == null || unit.trim().isEmpty()) {
      throw new IllegalArgumentException("Unit cannot be empty");
    }
    if (caloriesPerUnit < 0) {
      throw new IllegalArgumentException("Calories cannot be negative");
    }
    if (pricePerUnit < 0) {
      throw new IllegalArgumentException("Price cannot be negative");
    }
    if (category == null || category.trim().isEmpty()) {
      throw new IllegalArgumentException("Category cannot be empty");
    }
  }

  /**
   * Gets the ingredient name.
   *
   * @return Ingredient name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Gets the ingredient quantity.
   *
   * @return Quantity amount
   */
  @Override
  public double getQuantity() {
    return quantity;
  }

  /**
   * Gets the unit of measurement.
   *
   * @return Unit string
   */
  @Override
  public String getUnit() {
    return unit;
  }

  /**
   * Gets the base calories per unit.
   *
   * @return Calories per unit
   */
  @Override
  public int getCaloriesPerUnit() {
    return caloriesPerUnit;
  }

  /**
   * Gets the price per unit.
   *
   * @return Price per unit
   */
  @Override
  public double getPricePerUnit() {
    return pricePerUnit;
  }

  /**
   * Extracts the base unit from a compound unit string.
   * For example: "tablespoon (15g)" -> "tablespoon"
   */
  protected String getBaseUnit() {
    String unitLower = unit.toLowerCase();
    int parenIndex = unitLower.indexOf('(');
    return parenIndex > 0 ? unitLower.substring(0, parenIndex).trim() : unitLower;
  }

  /**
   * Gets the standard quantity multiplier for the current unit.
   * This converts various units to their standard form for calculations.
   */
  protected double getUnitMultiplier() {
    String baseUnit = getBaseUnit();
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
   * Calculates the total price based on quantity and unit conversion.
   * Prices in JSON are per 100g, so we need to divide by 100 to get price per gram.
   *
   * @return Total price
   */
  @Override
  public double getTotalPrice() {
    if (unit.toLowerCase().contains("piece") || unit.toLowerCase().contains("egg")) {
      // For items priced per piece, don't apply the 100g conversion
      return pricePerUnit * quantity;
    }
    // For weight-based items, convert price from per 100g to per gram
    return (pricePerUnit / 100.0) * quantity * getUnitMultiplier();
  }

  /**
   * Gets the food category.
   *
   * @return Category string
   */
  @Override
  public String getCategory() {
    return category;
  }

  /**
   * Gets the current cooking method.
   *
   * @return Cooking method string
   */
  @Override
  public String getCookingMethod() {
    return cookingMethod;
  }

  /**
   * Sets a new cooking method.
   *
   * @param cookingMethod New cooking method
   * @throws IllegalArgumentException If cooking method is empty
   */
  @Override
  public void setCookingMethod(String cookingMethod) {
    if (cookingMethod == null || cookingMethod.trim().isEmpty()) {
      throw new IllegalArgumentException("Cooking method cannot be empty");
    }
    this.cookingMethod = cookingMethod.trim().toLowerCase();
  }

  /**
   * Gets calories per unit modified by cooking method.
   * Default implementation returns base calories.
   * Override in subclasses to apply cooking modifiers.
   *
   * @return Modified calories per unit
   */
  @Override
  public double getModifiedCaloriesPerUnit(){
    return caloriesPerUnit;
  }

  /**
   * Calculates total calories based on quantity and cooking method.
   *
   * @return Total calories
   */
  @Override
  public int getTotalCalories() {
    return (int)(getModifiedCaloriesPerUnit() * quantity);
  }

  /**
   * Returns a string representation of this ingredient.
   *
   * @return String with quantity, unit, name and cooking method
   */
  @Override
  public String toString() {
    return String.format("%.2f %s %s (%s)", quantity, unit, name, cookingMethod);
  }
}
