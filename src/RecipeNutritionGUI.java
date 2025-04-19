import javax.swing.*;
import java.awt.*;

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

  public RecipeNutritionGUI() {
    createSummaryPanel();
    createDetailWindow();
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

  public JPanel getSummaryPanel() {
    return summaryPanel;
  }

  public void updateNutritionDisplay(Recipe recipe) {
    this.currentRecipe = recipe;  // Store the current recipe
    if (recipe != null) {
      NutritionInfo nutrition = recipe.getNutritionInfo();

      // Update progress bars in summary panel
      updateProgressBar(calorieProgress, nutrition.getCalories(), targetCalories);
      updateProgressBar(proteinProgress, nutrition.getProtein(), targetProtein);
      updateProgressBar(fatProgress, nutrition.getFat(), targetFat);
      updateProgressBar(carbProgress, nutrition.getCarbohydrates(), targetCarbs);

      // Update detailed window
      StringBuilder details = new StringBuilder();
      details.append(String.format("Nutrition Information for %s:\n\n", recipe.getName()));
      details.append(String.format("Calories: %.1f / %.1f kcal\n", nutrition.getCalories(), targetCalories));
      details.append(String.format("Protein: %.1f / %.1f g\n", nutrition.getProtein(), targetProtein));
      details.append(String.format("Fat: %.1f / %.1f g\n", nutrition.getFat(), targetFat));
      details.append(String.format("Carbohydrates: %.1f / %.1f g\n", nutrition.getCarbohydrates(), targetCarbs));
      details.append(String.format("Fiber: %.1f g\n", nutrition.getFiber()));
      details.append(String.format("Sugar: %.1f g\n", nutrition.getSugar()));
      details.append(String.format("Sodium: %.1f mg\n", nutrition.getSodium()));

      nutritionDetails.setText(details.toString());
    }
  }

  private void updateProgressBar(JProgressBar bar, double current, double target) {
    int percentage = (int) ((current / target) * 100);
    bar.setValue(Math.min(percentage, 100));
    bar.setString(String.format("%.1f / %.1f (%.0f%%)", current, target, Math.min(percentage, 100)));

    // Color coding
    if (percentage > 100) {
      bar.setForeground(Color.RED);
    } else if (percentage > 80) {
      bar.setForeground(Color.ORANGE);
    } else {
      bar.setForeground(Color.GREEN);
    }
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