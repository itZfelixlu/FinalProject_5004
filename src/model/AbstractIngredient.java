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
   * Calculates the total price based on quantity.
   *
   * @return Total price
   */
  @Override
  public double getTotalPrice() {
    return pricePerUnit * quantity;
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
