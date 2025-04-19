
public class NutritionInfo {
  private double protein;
  private double fat;
  private double carbohydrates;
  private double fiber;
  private double sugar;
  private double sodium;
  private double calories;

  public NutritionInfo(double protein, double fat, double carbohydrates,
      double fiber, double sugar, double sodium, double calories) {
    this.protein = protein;
    this.fat = fat;
    this.carbohydrates = carbohydrates;
    this.fiber = fiber;
    this.sugar = sugar;
    this.sodium = sodium;
    this.calories = calories;
  }

  // Add two NutritionInfo objects together
  public NutritionInfo add(NutritionInfo other) {
    return new NutritionInfo(
        this.protein + other.protein,
        this.fat + other.fat,
        this.carbohydrates + other.carbohydrates,
        this.fiber + other.fiber,
        this.sugar + other.sugar,
        this.sodium + other.sodium,
        this.calories + other.calories
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
        this.sodium * factor,
        this.calories * factor
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
}