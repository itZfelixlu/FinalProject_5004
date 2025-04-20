public class NutritionInfo {
  private final double protein;
  private final double fat;
  private final double carbohydrates;
  private final double fiber;
  private final double sugar;
  private final double sodium;
  private final double calories;

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

  // Add two NutritionInfo objects together
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

  // Multiply nutrition values by a factor (for ingredient quantities)
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

  // Getters
  public double getProtein() { return protein; }
  public double getFat() { return fat; }
  public double getCarbohydrates() { return carbohydrates; }
  public double getFiber() { return fiber; }
  public double getSugar() { return sugar; }
  public double getSodium() { return sodium; }
  public double getCalories() { return calories; }

  @Override
  public String toString() {
    return String.format("NutritionInfo[calories=%.1f, protein=%.1f, carbs=%.1f, fat=%.1f, fiber=%.1f, sugar=%.1f, sodium=%.1f]",
        calories, protein, carbohydrates, fat, fiber, sugar, sodium);
  }
}