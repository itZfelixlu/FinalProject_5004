package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import model.Recipe;
import java.io.IOException;

public class UserInfoGUI extends JFrame {
  private JTextField ageField;
  private JTextField heightField;
  private JTextField weightField;
  private JComboBox<String> genderCombo;
  private JComboBox<String> activityLevelCombo;
  private JLabel bmrLabel;
  private JLabel tdeeLabel;
  private JButton continueButton;

  public UserInfoGUI() {
    setTitle("User Information");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 400);
    setLayout(new BorderLayout(10, 10));

    // Create main panel
    JPanel mainPanel = new JPanel(new GridBagLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Title label
    JLabel titleLabel = new JLabel("Enter Your Information", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    mainPanel.add(titleLabel, gbc);
    gbc.gridwidth = 1;

    // Age field
    gbc.gridx = 0;
    gbc.gridy = 1;
    mainPanel.add(new JLabel("Age (years):"), gbc);
    gbc.gridx = 1;
    ageField = new JTextField(10);
    mainPanel.add(ageField, gbc);

    // Height field
    gbc.gridx = 0;
    gbc.gridy = 2;
    mainPanel.add(new JLabel("Height (cm):"), gbc);
    gbc.gridx = 1;
    heightField = new JTextField(10);
    mainPanel.add(heightField, gbc);

    // Weight field
    gbc.gridx = 0;
    gbc.gridy = 3;
    mainPanel.add(new JLabel("Weight (kg):"), gbc);
    gbc.gridx = 1;
    weightField = new JTextField(10);
    mainPanel.add(weightField, gbc);

    // Gender selection
    gbc.gridx = 0;
    gbc.gridy = 4;
    mainPanel.add(new JLabel("Gender:"), gbc);
    gbc.gridx = 1;
    String[] genders = {"Male", "Female"};
    genderCombo = new JComboBox<>(genders);
    mainPanel.add(genderCombo, gbc);

    // Activity level selection
    gbc.gridx = 0;
    gbc.gridy = 5;
    mainPanel.add(new JLabel("Activity Level:"), gbc);
    gbc.gridx = 1;
    String[] activityLevels = {
        "Sedentary (little or no exercise)",
        "Lightly active (1-3 days/week)",
        "Moderately active (3-5 days/week)",
        "Very active (6-7 days/week)",
        "Extra active (very active + physical job)"
    };
    activityLevelCombo = new JComboBox<>(activityLevels);
    mainPanel.add(activityLevelCombo, gbc);

    // Results panel
    JPanel resultsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
    resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    bmrLabel = new JLabel("BMR: --", SwingConstants.CENTER);
    tdeeLabel = new JLabel("TDEE: --", SwingConstants.CENTER);
    bmrLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    tdeeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    resultsPanel.add(bmrLabel);
    resultsPanel.add(tdeeLabel);

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    JButton calculateButton = new JButton("Calculate");
    calculateButton.addActionListener(e -> calculateAndShowResults());
    continueButton = new JButton("Continue to Recipe Selection");
    continueButton.setEnabled(false); // Disabled until calculation is done
    continueButton.addActionListener(e -> {
        if (bmrLabel.getText().contains("--")) {
            JOptionPane.showMessageDialog(this, 
                "Please calculate your BMR and TDEE first.", 
                "Calculation Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        Map<String, Object> userData = collectUserData();
        showRecipeGUI(userData);
    });

    buttonPanel.add(calculateButton);
    buttonPanel.add(continueButton);

    // Add components to frame
    add(mainPanel, BorderLayout.CENTER);
    add(resultsPanel, BorderLayout.NORTH);
    add(buttonPanel, BorderLayout.SOUTH);

    // Center the window on screen
    setLocationRelativeTo(null);
  }

  private Map<String, Object> collectUserData() {
    Map<String, Object> userData = new HashMap<>();
    try {
        int age = Integer.parseInt(ageField.getText());
        double height = Double.parseDouble(heightField.getText());
        double weight = Double.parseDouble(weightField.getText());
        boolean isMale = genderCombo.getSelectedItem().equals("Male");
        double bmr = calculateBMR(age, height, weight, isMale);
        double tdee = calculateTDEE(bmr, (String) activityLevelCombo.getSelectedItem());

        userData.put("age", age);
        userData.put("height", height);
        userData.put("weight", weight);
        userData.put("isMale", isMale);
        userData.put("bmr", bmr);
        userData.put("tdee", tdee);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, 
            "Invalid input values. Please check your entries.", 
            "Input Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    return userData;
  }

  private void calculateAndShowResults() {
    try {
      int age = Integer.parseInt(ageField.getText());
      double height = Double.parseDouble(heightField.getText());
      double weight = Double.parseDouble(weightField.getText());
      boolean isMale = genderCombo.getSelectedItem().equals("Male");
      String activityLevel = (String) activityLevelCombo.getSelectedItem();

      // Calculate BMR using Mifflin-St Jeor Equation
      double bmr = calculateBMR(age, height, weight, isMale);

      // Calculate TDEE based on activity level
      double tdee = calculateTDEE(bmr, activityLevel);

      // Update labels
      bmrLabel.setText(String.format("BMR: %.2f calories/day", bmr));
      tdeeLabel.setText(String.format("TDEE: %.2f calories/day", tdee));
      
      // Enable continue button
      continueButton.setEnabled(true);

    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, 
          "Please enter valid numbers for age, height, and weight.",
          "Input Error", 
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private double calculateBMR(int age, double height, double weight, boolean isMale) {
    // Mifflin-St Jeor Equation
    if (isMale) {
      return 10 * weight + 6.25 * height - 5 * age + 5;
    } else {
      return 10 * weight + 6.25 * height - 5 * age - 161;
    }
  }

  private double calculateTDEE(double bmr, String activityLevel) {
    // Activity multipliers
    double multiplier = 1.2; // Default sedentary

    if (activityLevel.contains("Lightly active")) {
      multiplier = 1.375;
    } else if (activityLevel.contains("Moderately active")) {
      multiplier = 1.55;
    } else if (activityLevel.contains("Very active")) {
      multiplier = 1.725;
    } else if (activityLevel.contains("Extra active")) {
      multiplier = 1.9;
    }

    return bmr * multiplier;
  }

  private void showRecipeGUI(Map<String, Object> userData) {
    try {
      // Get recipes directly
      List<Recipe> recipes = getRecipesFromMain();
      
      // Create and show the RecipeGUI
      SwingUtilities.invokeLater(() -> {
        RecipeGUI recipeGUI = new RecipeGUI(recipes, userData);
        recipeGUI.setVisible(true);
        this.dispose(); // Close the UserInfoGUI
      });
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
        "Error starting recipe GUI: " + e.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
  }
  
  // Helper method to get recipes from Main using reflection
  private List<Recipe> getRecipesFromMain() {
    try {
      // Use reflection to access Main's getRecipes method
      Class<?> mainClass = Class.forName("Main");
      java.lang.reflect.Method getRecipesMethod = mainClass.getMethod("getRecipes");
      return (List<Recipe>) getRecipesMethod.invoke(null);
    } catch (Exception e) {
      System.err.println("Error accessing recipes: " + e.getMessage());
      e.printStackTrace();
      return new ArrayList<>(); // Return empty list if there's an error
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        // First, make sure recipes are loaded by calling Main's initialization
        Class<?> mainClass = Class.forName("Main");
        java.lang.reflect.Method initComponentsMethod = mainClass.getDeclaredMethod("initializeComponents");
        initComponentsMethod.setAccessible(true); // Make private method accessible
        initComponentsMethod.invoke(null);
        
        // Then show the UserInfoGUI
        UserInfoGUI gui = new UserInfoGUI();
        gui.setVisible(true);
      } catch (Exception e) {
        System.err.println("Error initializing application: " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
          "Error initializing application: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
      }
    });
  }
} 