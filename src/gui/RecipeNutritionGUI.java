package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import model.Recipe;
import model.NutritionInfo;

public class RecipeNutritionGUI {
  // Main summary panel (shown in RecipeGUI)
  private JPanel summaryPanel;
  private JProgressBar calorieProgress;
  private JProgressBar proteinProgress;
  private JProgressBar fatProgress;
  private JProgressBar carbProgress;

  // Detailed window
  private JFrame detailWindow;
  private JTextArea nutritionDetails;
  private JPanel progressBarsPanel;

  // Target values
  private double targetCalories = 2000.0;  // Default values, can be set by user
  private double targetProtein = 50.0;
  private double targetFat = 65.0;
  private double targetCarbs = 300.0;

  private Map<String, Object> userData;

  private JPanel recipeList;
  private List<Recipe> addedRecipes = new ArrayList<>();

  public RecipeNutritionGUI(Map<String, Object> userData) {
    this.userData = userData;
    createSummaryPanel();
    createDetailWindow();

    // Set target calories based on user's TDEE if available
    if (userData != null && userData.containsKey("tdee")) {
      double tdee = (double) userData.get("tdee");
      targetCalories = tdee;
      updateTargetLabels();
    }
  }

  private void createSummaryPanel() {
    summaryPanel = new JPanel(new GridLayout(4, 1, 5, 5));
    summaryPanel.setBorder(BorderFactory.createTitledBorder("Nutrition Summary"));

    // Create progress bars with proper formatting
    calorieProgress = createProgressBar("Calories");
    proteinProgress = createProgressBar("Protein");
    fatProgress = createProgressBar("Fat");
    carbProgress = createProgressBar("Carbs");

    // Add progress bars to panel with labels showing current/target values
    summaryPanel.add(createLabeledProgress("Calories", calorieProgress, targetCalories));
    summaryPanel.add(createLabeledProgress("Protein", proteinProgress, targetProtein));
    summaryPanel.add(createLabeledProgress("Fat", fatProgress, targetFat));
    summaryPanel.add(createLabeledProgress("Carbs", carbProgress, targetCarbs));

    // Add click listener to open detail window
    summaryPanel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        detailWindow.setVisible(true);
      }
    });

    summaryPanel.setPreferredSize(new Dimension(250, 200));
  }

  private JProgressBar createProgressBar(String nutrient) {
    JProgressBar bar = new JProgressBar(0, 100);
    bar.setStringPainted(true);
    bar.setForeground(getProgressBarColor(nutrient));
    return bar;
  }

  private Color getProgressBarColor(String nutrient) {
    switch (nutrient.toLowerCase()) {
      case "calories":
        return new Color(255, 100, 100); // Red
      case "protein":
        return new Color(100, 200, 100); // Green
      case "fat":
        return new Color(255, 200, 100); // Orange
      case "carbs":
        return new Color(100, 150, 255); // Blue
      default:
        return Color.BLUE;
    }
  }

  private JPanel createLabeledProgress(String label, JProgressBar bar, double target) {
    JPanel panel = new JPanel(new BorderLayout(5, 0));
    
    // Create label with current/target values and units
    String unit = "";
    switch (label.toLowerCase()) {
      case "calories":
        unit = " kcal";
        break;
      case "protein":
      case "fat":
      case "carbs":
        unit = " g";
        break;
    }
    
    JLabel valueLabel = new JLabel(String.format("%s: 0%s / %.1f%s", label, unit, target, unit));
    valueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    panel.add(valueLabel, BorderLayout.WEST);
    
    // Add progress bar
    panel.add(bar, BorderLayout.CENTER);
    
    return panel;
  }

  private void createDetailWindow() {
    detailWindow = new JFrame("Detailed Nutrition Information");
    detailWindow.setLayout(new BorderLayout(10, 10));
    detailWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    // Create a main panel to hold all components
    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Create a panel for the top section (progress bars and recipe list)
    JPanel topPanel = new JPanel(new BorderLayout(10, 10));

    // Add the summary panel with progress bars at the top
    JPanel progressPanel = new JPanel(new GridLayout(4, 1, 5, 5));
    progressPanel.setBorder(BorderFactory.createTitledBorder("Nutrition Progress"));
    
    // Create progress bars with fixed width
    int progressBarWidth = 300;
    calorieProgress.setPreferredSize(new Dimension(progressBarWidth, 20));
    proteinProgress.setPreferredSize(new Dimension(progressBarWidth, 20));
    fatProgress.setPreferredSize(new Dimension(progressBarWidth, 20));
    carbProgress.setPreferredSize(new Dimension(progressBarWidth, 20));
    
    progressPanel.add(createLabeledProgress("Calories", calorieProgress, targetCalories));
    progressPanel.add(createLabeledProgress("Protein", proteinProgress, targetProtein));
    progressPanel.add(createLabeledProgress("Fat", fatProgress, targetFat));
    progressPanel.add(createLabeledProgress("Carbs", carbProgress, targetCarbs));
    
    topPanel.add(progressPanel, BorderLayout.NORTH);

    // Add recipe list panel
    JPanel recipeListPanel = new JPanel(new BorderLayout());
    recipeListPanel.setBorder(BorderFactory.createTitledBorder("Added Recipes"));
    recipeList = new JPanel();
    recipeList.setLayout(new BoxLayout(recipeList, BoxLayout.Y_AXIS));
    JScrollPane recipeScrollPane = new JScrollPane(recipeList);
    recipeScrollPane.setPreferredSize(new Dimension(progressBarWidth, 150));
    recipeListPanel.add(recipeScrollPane, BorderLayout.CENTER);
    
    // Add checkout button below recipe list
    JButton checkoutButton = new JButton("Proceed to Checkout");
    checkoutButton.setFont(new Font("Arial", Font.BOLD, 14));
    checkoutButton.addActionListener(e -> {
        if (addedRecipes.isEmpty()) {
            JOptionPane.showMessageDialog(detailWindow,
                "Please add some recipes before proceeding to checkout.",
                "No Recipes Added",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        CheckoutGUI checkoutGUI = new CheckoutGUI(new ArrayList<>(addedRecipes));
        checkoutGUI.setVisible(true);
    });
    JPanel checkoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    checkoutPanel.add(checkoutButton);
    recipeListPanel.add(checkoutPanel, BorderLayout.SOUTH);
    
    topPanel.add(recipeListPanel, BorderLayout.CENTER);
    mainPanel.add(topPanel, BorderLayout.NORTH);

    // Detailed text area
    nutritionDetails = new JTextArea(15, 30);
    nutritionDetails.setEditable(false);
    nutritionDetails.setFont(new Font("Arial", Font.PLAIN, 12));
    mainPanel.add(new JScrollPane(nutritionDetails), BorderLayout.CENTER);

    // Target settings panel
    JPanel targetPanel = new JPanel(new GridLayout(4, 3, 5, 5));
    targetPanel.setBorder(BorderFactory.createTitledBorder("Nutrition Targets"));
    addTargetSetting(targetPanel, "Calories Target:", targetCalories);
    addTargetSetting(targetPanel, "Protein Target (g):", targetProtein);
    addTargetSetting(targetPanel, "Fat Target (g):", targetFat);
    addTargetSetting(targetPanel, "Carbs Target (g):", targetCarbs);
    mainPanel.add(targetPanel, BorderLayout.SOUTH);

    detailWindow.add(mainPanel);
    detailWindow.pack();
  }

  private void addTargetSetting(JPanel panel, String label, double defaultValue) {
    // Add label
    panel.add(new JLabel(label));
    
    // Add text field
    JTextField field = new JTextField(String.valueOf(defaultValue));
    panel.add(field);
    
    // Add update button
    JButton updateButton = new JButton("Update");
    updateButton.addActionListener(e -> {
        updateTarget(label, field.getText());
        // Update the display
        if (!addedRecipes.isEmpty()) {
            updateNutritionDisplay();
        }
    });
    panel.add(updateButton);
  }

  private void updateTarget(String label, String value) {
    try {
      double newValue = Double.parseDouble(value);
      
      // Update the target value
      if (label.contains("Calories")) {
          targetCalories = newValue;
      }
      else if (label.contains("Protein")) {
          targetProtein = newValue;
      }
      else if (label.contains("Fat")) {
          targetFat = newValue;
      }
      else if (label.contains("Carbs")) {
          targetCarbs = newValue;
      }

      // Update the display if there are recipes
      if (!addedRecipes.isEmpty()) {
          updateNutritionDisplay();
      } else {
          // If no recipes, just update the progress bars with 0 values
          updateProgressBar(calorieProgress, 0, (int)targetCalories, "Calories");
          updateProgressBar(proteinProgress, 0, (int)targetProtein, "Protein");
          updateProgressBar(fatProgress, 0, (int)targetFat, "Fat");
          updateProgressBar(carbProgress, 0, (int)targetCarbs, "Carbs");
      }

      // Show confirmation message
      JOptionPane.showMessageDialog(detailWindow, 
          "Target updated successfully!", 
          "Update Successful", 
          JOptionPane.INFORMATION_MESSAGE);

    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(detailWindow, 
          "Please enter a valid number", 
          "Invalid Input", 
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void updateTargetLabels() {
    if (userData != null) {
      // Set protein target (1.6-2.2g per kg of body weight)
      double weight = (double) userData.get("weight");
      targetProtein = weight * 1.8; // Using middle of the range

      // Set fat target (20-35% of total calories)
      targetFat = (targetCalories * 0.3) / 9; // 30% of calories, divided by 9 calories per gram

      // Set carbs target (remaining calories)
      double proteinCalories = targetProtein * 4;
      double fatCalories = targetFat * 9;
      double carbCalories = targetCalories - proteinCalories - fatCalories;
      targetCarbs = carbCalories / 4; // 4 calories per gram

      // Update the display with combined nutrition of all recipes
      updateNutritionDisplay();
    }
  }

  public JPanel getSummaryPanel() {
    return summaryPanel;
  }

  public void updateNutritionDisplay(Recipe recipe) {
    if (recipe == null || recipe.getNutritionInfo() == null) {
      System.out.println("Warning: Null recipe or nutrition info in updateNutritionDisplay");
      return;
    }

    updateNutritionDisplay(recipe.getName(), recipe.getNutritionInfo());
  }

  private void updateNutritionDisplay(String name, NutritionInfo nutrition) {
    // Update progress bars with proper integer values
    updateProgressBar(calorieProgress, (int)nutrition.getCalories(), (int)targetCalories, "Calories");
    updateProgressBar(proteinProgress, (int)nutrition.getProtein(), (int)targetProtein, "Protein");
    updateProgressBar(fatProgress, (int)nutrition.getFat(), (int)targetFat, "Fat");
    updateProgressBar(carbProgress, (int)nutrition.getCarbohydrates(), (int)targetCarbs, "Carbs");

    // Update labels with proper formatting
    StringBuilder details = new StringBuilder();
    details.append(String.format("Nutrition Information for %s:\n\n", name));
    details.append(String.format("Calories: %d / %.1f kcal\n", (int)nutrition.getCalories(), targetCalories));
    details.append(String.format("Protein: %d / %.1f g\n", (int)nutrition.getProtein(), targetProtein));
    details.append(String.format("Fat: %d / %.1f g\n", (int)nutrition.getFat(), targetFat));
    details.append(String.format("Carbohydrates: %d / %.1f g\n", (int)nutrition.getCarbohydrates(), targetCarbs));
    details.append(String.format("Fiber: %d g\n", (int)nutrition.getFiber()));
    details.append(String.format("Sugar: %.1f g\n", nutrition.getSugar()));
    details.append(String.format("Sodium: %.1f mg\n", nutrition.getSodium()));

    nutritionDetails.setText(details.toString());
  }

  private void updateProgressBar(JProgressBar bar, int value, int max, String nutrient) {
    if (bar == null) return;
    
    // Calculate percentage
    int percentage = (int)((value * 100.0) / max);
    bar.setMaximum(100);
    bar.setValue(Math.min(percentage, 100));
    
    // Update label with current/target values and units
    JPanel parent = (JPanel)bar.getParent();
    JLabel label = (JLabel)parent.getComponent(0);
    String unit = "";
    switch (nutrient.toLowerCase()) {
      case "calories":
        unit = " kcal";
        break;
      case "protein":
      case "fat":
      case "carbs":
        unit = " g";
        break;
    }
    label.setText(String.format("%s: %d%s / %d%s", nutrient, value, unit, max, unit));
    
    // Update progress bar color based on percentage
    if (percentage > 100) {
      bar.setForeground(Color.RED);
    } else if (percentage > 80) {
      bar.setForeground(Color.ORANGE);
    } else {
      bar.setForeground(getProgressBarColor(nutrient));
    }
    
    bar.setString(String.format("%d%%", Math.min(percentage, 100)));
  }

  public void positionDetailWindow(JFrame mainWindow) {
    // Position the detail window to the right of the main window
    detailWindow.setLocation(
        mainWindow.getX() + mainWindow.getWidth(),
        mainWindow.getY()
    );
  }

  public void showDetailWindow() {
    if (!detailWindow.isVisible()) {
      detailWindow.setVisible(true);
    }
    detailWindow.toFront();
  }

  public void addRecipe(Recipe recipe) {
    if (!addedRecipes.contains(recipe)) {
      addedRecipes.add(recipe);
      updateRecipeList();
      updateNutritionDisplay();
    }
  }

  public void removeRecipe(Recipe recipe) {
    addedRecipes.remove(recipe);
    updateRecipeList();
    updateNutritionDisplay();
  }

  private void updateRecipeList() {
    recipeList.removeAll();
    
    for (Recipe recipe : addedRecipes) {
      JPanel recipePanel = new JPanel(new BorderLayout());
      recipePanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      
      JLabel nameLabel = new JLabel(recipe.getName());
      recipePanel.add(nameLabel, BorderLayout.CENTER);
      
      JButton removeButton = new JButton("Remove");
      removeButton.addActionListener(e -> removeRecipe(recipe));
      recipePanel.add(removeButton, BorderLayout.EAST);
      
      recipeList.add(recipePanel);
    }
    
    recipeList.revalidate();
    recipeList.repaint();
  }

  private void updateNutritionDisplay() {
    if (addedRecipes.isEmpty()) {
      updateNutritionDisplay("No Recipes", new NutritionInfo(0, 0, 0, 0, 0, 0));
      return;
    }

    // Calculate combined nutrition
    NutritionInfo combinedNutrition = new NutritionInfo(0, 0, 0, 0, 0, 0);
    for (Recipe recipe : addedRecipes) {
      NutritionInfo nutrition = recipe.getNutritionInfo();
      if (nutrition != null) {
        combinedNutrition = combinedNutrition.add(nutrition);
      }
    }

    updateNutritionDisplay("Combined Recipes", combinedNutrition);
  }
}