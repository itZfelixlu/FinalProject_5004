package model;

/**
 * A class representing the nutritional information for a food item, recipe, or meal.
 * Stores and calculates various nutritional values including macronutrients and micronutrients.
 * Provides methods for combining nutritional information and calculating total calories.
 */
public class NutritionInfo {
  private final double protein;
  private final double fat;
  private final double carbohydrates;
  private final double fiber;
  private final double sugar;
  private final double sodium;
  private final double calories;

  /**
   * Constructs a new NutritionInfo object with specified nutritional values.
   *
   * @param protein Amount of protein in grams
   * @param fat Amount of fat in grams
   * @param carbohydrates Amount of carbohydrates in grams
   * @param fiber Amount of fiber in grams
   * @param sugar Amount of sugar in grams
   * @param sodium Amount of sodium in milligrams
   */
  public NutritionInfo(double protein, double fat, double carbohydrates,
      double fiber, double sugar, double sodium) {
    this.protein = protein;
    this.fat = fat;
    this.carbohydrates = carbohydrates;
    this.fiber = fiber;
    this.sugar = sugar;
    this.sodium = sodium;
    // Calculate calories from macronutrients
    this.calories = (protein * 4) + (carbohydrates * 4) + (fat * 9);
  }

  /**
   * Adds the nutritional values of another NutritionInfo object to this one.
   * Creates a new NutritionInfo object with the combined values.
   *
   * @param other The NutritionInfo object to add
   * @return A new NutritionInfo object containing the sum of both objects' values
   */
  public NutritionInfo add(NutritionInfo other) {
    return new NutritionInfo(
        this.protein + other.protein,
        this.fat + other.fat,
        this.carbohydrates + other.carbohydrates,
        this.fiber + other.fiber,
        this.sugar + other.sugar,
        this.sodium + other.sodium
    );
  }

  /**
   * Multiplies all nutritional values by a given factor.
   * Useful for scaling recipes or adjusting serving sizes.
   *
   * @param factor The multiplication factor
   * @return A new NutritionInfo object with all values multiplied by the factor
   */
  public NutritionInfo multiply(double factor) {
    return new NutritionInfo(
        this.protein * factor,
        this.fat * factor,
        this.carbohydrates * factor,
        this.fiber * factor,
        this.sugar * factor,
        this.sodium * factor
    );
  }

  /**
   * Gets the amount of protein in grams.
   *
   * @return Protein content in grams
   */
  public double getProtein() { return protein; }

  /**
   * Gets the amount of fat in grams.
   *
   * @return Fat content in grams
   */
  public double getFat() { return fat; }

  /**
   * Gets the amount of carbohydrates in grams.
   *
   * @return Carbohydrate content in grams
   */
  public double getCarbohydrates() { return carbohydrates; }

  /**
   * Gets the amount of fiber in grams.
   *
   * @return Fiber content in grams
   */
  public double getFiber() { return fiber; }

  /**
   * Gets the amount of sugar in grams.
   *
   * @return Sugar content in grams
   */
  public double getSugar() { return sugar; }

  /**
   * Gets the amount of sodium in milligrams.
   *
   * @return Sodium content in milligrams
   */
  public double getSodium() { return sodium; }

  /**
   * Calculates and returns the total calories based on macronutrient content.
   * Uses standard caloric values: 4 calories per gram of protein/carbs, 9 calories per gram of fat.
   *
   * @return Total calories
   */
  public double getCalories() { return calories; }

  /**
   * Returns a string representation of the nutritional information.
   * Includes all macronutrients and micronutrients with their respective units.
   *
   * @return Formatted string containing all nutritional values
   */
  @Override
  public String toString() {
    return String.format("NutritionInfo[calories=%.1f, protein=%.1f, carbs=%.1f, fat=%.1f, fiber=%.1f, sugar=%.1f, sodium=%.1f]",
        calories, protein, carbohydrates, fat, fiber, sugar, sodium);
  }
}