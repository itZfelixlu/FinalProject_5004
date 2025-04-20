import javax.swing.*;
import java.awt.*;
import java.util.Map;

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

  private Recipe currentRecipe;  // Add this field to track current recipe
  private Map<String, Object> userData;
  private NutritionCalculator nutritionCalculator;

  public RecipeNutritionGUI(Map<String, Object> userData) {
    this.userData = userData;
    this.nutritionCalculator = new NutritionCalculator();
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

    // Create progress bars
    calorieProgress = createProgressBar("Calories");
    proteinProgress = createProgressBar("Protein");
    fatProgress = createProgressBar("Fat");
    carbProgress = createProgressBar("Carbs");

    // Add progress bars to panel
    summaryPanel.add(createLabeledProgress("Calories", calorieProgress));
    summaryPanel.add(createLabeledProgress("Protein", proteinProgress));
    summaryPanel.add(createLabeledProgress("Fat", fatProgress));
    summaryPanel.add(createLabeledProgress("Carbs", carbProgress));

    // Add click listener to open detail window
    summaryPanel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        detailWindow.setVisible(true);
      }
    });

    summaryPanel.setPreferredSize(new Dimension(200, 150));
  }

  private JProgressBar createProgressBar(String nutrient) {
    JProgressBar bar = new JProgressBar(0, 100);
    bar.setStringPainted(true);
    return bar;
  }

  private JPanel createLabeledProgress(String label, JProgressBar bar) {
    JPanel panel = new JPanel(new BorderLayout(5, 0));
    panel.add(new JLabel(label + ":"), BorderLayout.WEST);
    panel.add(bar, BorderLayout.CENTER);
    return panel;
  }

  private void createDetailWindow() {
    detailWindow = new JFrame("Detailed Nutrition Information");
    detailWindow.setLayout(new BorderLayout(10, 10));
    detailWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    // Progress bars panel
    progressBarsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
    progressBarsPanel.setBorder(BorderFactory.createTitledBorder("Nutrition Progress"));

    // Detailed text area
    nutritionDetails = new JTextArea(15, 30);
    nutritionDetails.setEditable(false);

    // Target settings panel
    JPanel targetPanel = new JPanel(new GridLayout(0, 2, 5, 5));
    targetPanel.setBorder(BorderFactory.createTitledBorder("Nutrition Targets"));
    addTargetSetting(targetPanel, "Calories Target:", targetCalories);
    addTargetSetting(targetPanel, "Protein Target (g):", targetProtein);
    addTargetSetting(targetPanel, "Fat Target (g):", targetFat);
    addTargetSetting(targetPanel, "Carbs Target (g):", targetCarbs);

    // Add components to window
    detailWindow.add(progressBarsPanel, BorderLayout.NORTH);
    detailWindow.add(new JScrollPane(nutritionDetails), BorderLayout.CENTER);
    detailWindow.add(targetPanel, BorderLayout.EAST);

    detailWindow.pack();
  }

  private void addTargetSetting(JPanel panel, String label, double defaultValue) {
    panel.add(new JLabel(label));
    JTextField field = new JTextField(String.valueOf(defaultValue));
    field.addActionListener(e -> updateTarget(label, field.getText()));
    panel.add(field);
  }

  private void updateTarget(String label, String value) {
    try {
      double newValue = Double.parseDouble(value);
      if (label.contains("Calories")) targetCalories = newValue;
      else if (label.contains("Protein")) targetProtein = newValue;
      else if (label.contains("Fat")) targetFat = newValue;
      else if (label.contains("Carbs")) targetCarbs = newValue;

      // Update display with current recipe if available
      if (currentRecipe != null) {
        updateNutritionDisplay(currentRecipe);
      }
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(detailWindow, "Please enter a valid number");
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

      // Update the display if a recipe is currently shown
      if (currentRecipe != null) {
        updateNutritionDisplay(currentRecipe);
      }
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

    NutritionInfo nutrition = recipe.getNutritionInfo();
    
    // Update progress bars with proper integer values
    updateProgressBar(calorieProgress, (int)nutrition.getCalories(), 2000);
    updateProgressBar(proteinProgress, (int)nutrition.getProtein(), 100);
    updateProgressBar(fatProgress, (int)nutrition.getFat(), 100);
    updateProgressBar(carbProgress, (int)nutrition.getCarbohydrates(), 300);

    // Update labels with proper formatting
    StringBuilder details = new StringBuilder();
    details.append(String.format("Nutrition Information for %s:\n\n", recipe.getName()));
    details.append(String.format("Calories: %d / %.1f kcal\n", (int)nutrition.getCalories(), targetCalories));
    details.append(String.format("Protein: %d / %.1f g\n", (int)nutrition.getProtein(), targetProtein));
    details.append(String.format("Fat: %d / %.1f g\n", (int)nutrition.getFat(), targetFat));
    details.append(String.format("Carbohydrates: %d / %.1f g\n", (int)nutrition.getCarbohydrates(), targetCarbs));
    details.append(String.format("Fiber: %d g\n", (int)nutrition.getFiber()));
    details.append(String.format("Sugar: %.1f g\n", nutrition.getSugar()));
    details.append(String.format("Sodium: %.1f mg\n", nutrition.getSodium()));

    nutritionDetails.setText(details.toString());
  }

  private void updateProgressBar(JProgressBar bar, int value, int max) {
    if (bar == null) return;
    
    bar.setMaximum(max);
    bar.setValue(value);
    bar.setString(String.format("%d%%", (int)((value * 100.0) / max)));
  }

  public void positionDetailWindow(JFrame mainWindow) {
    // Position the detail window to the right of the main window
    detailWindow.setLocation(
        mainWindow.getX() + mainWindow.getWidth(),
        mainWindow.getY()
    );
  }

  public void showDetailWindow() {
    detailWindow.setVisible(true);
  }
}