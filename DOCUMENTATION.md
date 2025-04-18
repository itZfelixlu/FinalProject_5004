# Food Online Shopping Tool - Documentation

## Project Overview

This project implements a food online shopping tool with calorie tracking capabilities. It allows users to:
- Browse ingredients by category
- Create recipes with various ingredients
- Track calories based on ingredients and cooking methods
- Customize meals based on flavor profiles and cuisines

## System Architecture

### Core Components

#### 1. Ingredient System
- **IIngredient Interface**: Defines the contract for all ingredient objects
- **AbstractIngredient**: Base implementation with common functionality
- **Ingredient**: Concrete implementation with additional features
  - JSON data loading
  - Cooking method calorie modifications
  - Category management

#### 2. Recipe System
- Recipe class for combining ingredients
- Calorie calculation based on included ingredients
- Filtering by cuisine, flavor, and preparation time

#### 3. Data Management
- JSON-based data storage
- Structured by food categories
- Includes nutritional information and pricing

## Class Structure

### IIngredient Interface
Defines methods for:
- Basic properties (name, quantity, unit)
- Nutritional information
- Pricing information
- Cooking method management
- Category identification
- Detailed information retrieval

### AbstractIngredient
Provides:
- Common properties (name, quantity, unit, calories, etc.)
- Input validation
- Default implementations for interface methods
- Base calorie calculations

### Ingredient
Extends functionality with:
- JSON data loading
- Category-based organization
- Cooking method calorie modifications
- Search and retrieval methods
- Detailed information formatting

### Recipe
Combines ingredients with:
- Cuisine and flavor profiles
- Preparation time tracking
- Total calorie calculation
- Ingredient list management

## How to Use

### Creating Ingredients

```java
// Get an ingredient by name
Ingredient chicken = Ingredient.getIngredientByName("Chicken Breast", 2);

// Set a cooking method
chicken.setCookingMethod("grilled");

// Get calorie information
int calories = chicken.getTotalCalories();
```

### Creating Recipes

```java
// Create a new recipe
Recipe grilledChickenSalad = new Recipe(
    "Grilled Chicken Salad", 
    "Fresh", 
    "American", 
    20, 
    new ArrayList<>()
);

// Add ingredients
Ingredient grilledChicken = Ingredient.getIngredientByName("Chicken Breast", 1);
grilledChicken.setCookingMethod("grilled");
grilledChickenSalad.addIngredient(grilledChicken);

// Add more ingredients
grilledChickenSalad.addIngredient(Ingredient.getIngredientByName("Spinach", 2));

// Get recipe information
System.out.println(grilledChickenSalad.getDetailedInfo());
```

### Checking Calorie Information

```java
// Check if a recipe is within a calorie range
boolean isLowCalorie = grilledChickenSalad.isWithinCalorieRange(300, 500);

// Compare different cooking methods
chicken.setCookingMethod("raw");
int rawCalories = chicken.getTotalCalories();

chicken.setCookingMethod("fried");
int friedCalories = chicken.getTotalCalories();
```

## Data Files

All ingredient data is stored in JSON files in the `src/ingredientsSource/` directory:
- `meat.json`: Meat ingredients
- `vegetable.json`: Vegetable ingredients
- `fruit.json`: Fruit ingredients 
- `dairy.json`: Dairy ingredients
- `seasoning.json`: Seasonings and spices
- `CalorieModifier.json`: Cooking method calorie modifiers

## System Extensions

The system can be extended by:
1. Adding new ingredient categories
2. Implementing new cooking methods
3. Creating recipe recommendation algorithms
4. Adding meal planning functionality
5. Implementing shopping list generation

## Running the Demo

To see the system in action, run the `IngredientTest` class, which demonstrates:
- Loading ingredients from JSON
- Displaying ingredient information
- Showing calorie changes based on cooking methods
- Creating a sample recipe 