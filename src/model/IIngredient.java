package model;

/**
 * Interface for food ingredients in the meal planning system.
 * Defines methods for accessing and modifying ingredient properties including
 * nutritional information, pricing, and cooking methods.
 */
public interface IIngredient {
  /**
   * Gets the name of the ingredient.
   *
   * @return Ingredient name
   */
  String getName();

  /**
   * Gets the quantity of the ingredient.
   *
   * @return Quantity amount
   */
  double getQuantity();

  /**
   * Gets the unit of measurement.
   *
   * @return Unit of measurement (e.g., "g", "ml", "tbsp", "cup")
   */
  String getUnit();

  /**
   * Gets the base calories per unit (raw state).
   *
   * @return Calories per unit
   */
  int getCaloriesPerUnit();

  /**
   * Gets calories per unit modified by cooking method.
   *
   * @return Modified calories per unit
   */
  double getModifiedCaloriesPerUnit();

  /**
   * Calculates total calories based on quantity and cooking method.
   *
   * @return Total calories
   */
  int getTotalCalories();

  /**
   * Gets the price per unit.
   *
   * @return Price per unit
   */
  double getPricePerUnit();

  /**
   * Calculates total price based on quantity.
   *
   * @return Total price
   */
  double getTotalPrice();

  /**
   * Gets the current cooking method.
   *
   * @return Cooking method
   */
  String getCookingMethod();

  /**
   * Sets a new cooking method.
   *
   * @param cookingMethod New cooking method
   */
  void setCookingMethod(String cookingMethod);

  /**
   * Gets the food category.
   *
   * @return Category (e.g., "meat", "vegetable", "fruit")
   */
  String getCategory();

}
