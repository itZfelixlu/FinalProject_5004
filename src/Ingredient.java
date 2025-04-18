public class Ingredient {
  private String name;
  private double quantity;
  private String unit; // e.g., "g", "ml", "tbsp", "cup", etc.
  private int calories; // calories per unit

  public Ingredient(String name, double quantity, String unit, int calories) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Ingredient name cannot be empty");
    }
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive");
    }
    if (unit == null || unit.trim().isEmpty()) {
      throw new IllegalArgumentException("Unit cannot be empty");
    }
    if (calories < 0) {
      throw new IllegalArgumentException("Calories cannot be negative");
    }

    this.name = name.trim();
    this.quantity = quantity;
    this.unit = unit.trim();
    this.calories = calories;
  }

  // Getters
  public String getName() {
    return name;
  }

  public double getQuantity() {
    return quantity;
  }

  public String getUnit() {
    return unit;
  }

  public int getCalories() {
    return calories;
  }

  // Calculate total calories for this ingredient
  public int getTotalCalories() {
    return (int)(calories * quantity);
  }

  @Override
  public String toString() {
    return String.format("%.2f %s %s", quantity, unit, name);
  }

  // Get detailed information about the ingredient
  public String getDetailedInfo() {
    return String.format(
        "Ingredient: %s\n" +
            "Quantity: %.2f %s\n" +
            "Calories per unit: %d\n" +
            "Total calories: %d",
        name, quantity, unit, calories, getTotalCalories()
    );
  }
}