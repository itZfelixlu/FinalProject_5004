import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeGUI extends JFrame {
  private List<Recipe> recipes;
  private DefaultListModel<String> recipeListModel;
  private JList<String> recipeList;
  private JTextArea recipeDetails;
  private JTextField searchField;
  private JComboBox<String> cuisineFilter;
  private JComboBox<String> calorieFilter;
  private JComboBox<String> prepTimeFilter;
  private JTextField searchBar;
  private JComboBox<String> cuisineCombo;
  private JComboBox<String> prepTimeCombo;
  private JTextField calorieGoalField;
  private JPanel recipePanel;
  private RecipeNutritionGUI nutritionGUI;
  private List<Recipe> allRecipes;

  public RecipeGUI(List<Recipe> recipes) {
    this.allRecipes = recipes;
    this.recipes = new ArrayList<>(recipes);  // Create a copy for filtering
    recipeListModel = new DefaultListModel<>();

    setTitle("Recipe Manager");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);
    setLayout(new BorderLayout());

    // Initialize nutrition GUI
    nutritionGUI = new RecipeNutritionGUI();
    nutritionGUI.positionDetailWindow(this);

    // Add nutrition summary panel to the top right
    JPanel topRightPanel = new JPanel(new BorderLayout());
    topRightPanel.add(nutritionGUI.getSummaryPanel(), BorderLayout.NORTH);
    add(topRightPanel, BorderLayout.EAST);

    // Create components
    JPanel leftPanel = new JPanel(new BorderLayout());
    JPanel rightPanel = new JPanel(new BorderLayout());

    // Search panel
    JPanel searchPanel = new JPanel(new BorderLayout());
    searchField = new JTextField();
    searchField.setToolTipText("Search by flavor or description (e.g., 'heavy', 'sour', 'spicy')");
    JButton searchButton = new JButton("Search");
    searchButton.addActionListener(e -> filterRecipes());
    searchPanel.add(searchField, BorderLayout.CENTER);
    searchPanel.add(searchButton, BorderLayout.EAST);

    // Filter panel
    JPanel filterPanel = new JPanel(new GridLayout(3, 2));

    // Cuisine filter
    filterPanel.add(new JLabel("Cuisine:"));
    String[] cuisines = {"All", "italian", "chinese", "greek", "mexican", "american", "indian", "japanese", "korean", "french", "vietnamese", "middle eastern"};
    cuisineFilter = new JComboBox<>(cuisines);
    cuisineFilter.addActionListener(e -> filterRecipes());
    filterPanel.add(cuisineFilter);

    // Calorie filter
    filterPanel.add(new JLabel("Calories:"));
    String[] calorieRanges = {"All", "0-300", "301-600", "601-900", "901+"};
    calorieFilter = new JComboBox<>(calorieRanges);
    calorieFilter.addActionListener(e -> filterRecipes());
    filterPanel.add(calorieFilter);

    // Prep time filter
    filterPanel.add(new JLabel("Prep Time:"));
    String[] prepTimeRanges = {"All", "0-15 min", "16-30 min", "31-45 min", "46+ min"};
    prepTimeFilter = new JComboBox<>(prepTimeRanges);
    prepTimeFilter.addActionListener(e -> filterRecipes());
    filterPanel.add(prepTimeFilter);

    // Recipe list
    recipeList = new JList<>(recipeListModel);
    recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    recipeList.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        displayRecipeDetails();
      }
    });
    JScrollPane listScrollPane = new JScrollPane(recipeList);

    // Recipe details
    recipeDetails = new JTextArea();
    recipeDetails.setEditable(false);
    JScrollPane detailsScrollPane = new JScrollPane(recipeDetails);

    // Add components to panels
    leftPanel.add(searchPanel, BorderLayout.NORTH);
    leftPanel.add(filterPanel, BorderLayout.CENTER);
    leftPanel.add(listScrollPane, BorderLayout.SOUTH);

    rightPanel.add(new JLabel("Recipe Details"), BorderLayout.NORTH);
    rightPanel.add(detailsScrollPane, BorderLayout.CENTER);

    // Add panels to frame
    add(leftPanel, BorderLayout.WEST);
    add(rightPanel, BorderLayout.CENTER);

    // Set sizes
    leftPanel.setPreferredSize(new Dimension(300, 600));

    // Initial population of recipe list
    populateRecipeList();
  }

  private void filterRecipes() {
    String searchText = searchField.getText().toLowerCase();
    String selectedCuisine = (String) cuisineFilter.getSelectedItem();
    String selectedCalorieRange = (String) calorieFilter.getSelectedItem();
    String selectedPrepTimeRange = (String) prepTimeFilter.getSelectedItem();

    recipeListModel.clear();

    for (Recipe recipe : recipes) {
      boolean matchesSearch = searchText.isEmpty() ||
          recipe.getName().toLowerCase().contains(searchText) ||
          recipe.getFlavor().toLowerCase().contains(searchText);

      boolean matchesCuisine = selectedCuisine.equals("All") ||
          recipe.getCuisine().equalsIgnoreCase(selectedCuisine);

      boolean matchesCalories = selectedCalorieRange.equals("All") ||
          matchesCalorieRange(recipe.getCalories(), selectedCalorieRange);

      boolean matchesPrepTime = selectedPrepTimeRange.equals("All") ||
          matchesPrepTimeRange(recipe.getPrepTime(), selectedPrepTimeRange);

      if (matchesSearch && matchesCuisine && matchesCalories && matchesPrepTime) {
        recipeListModel.addElement(recipe.getName());
      }
    }
  }

  private boolean matchesCalorieRange(int calories, String range) {
    switch (range) {
      case "0-300": return calories <= 300;
      case "301-600": return calories > 300 && calories <= 600;
      case "601-900": return calories > 600 && calories <= 900;
      case "901+": return calories > 900;
      default: return true;
    }
  }

  private boolean matchesPrepTimeRange(int prepTime, String range) {
    switch (range) {
      case "0-15 min": return prepTime <= 15;
      case "16-30 min": return prepTime > 15 && prepTime <= 30;
      case "31-45 min": return prepTime > 30 && prepTime <= 45;
      case "46+ min": return prepTime > 45;
      default: return true;
    }
  }

  private void displayRecipeDetails() {
    int selectedIndex = recipeList.getSelectedIndex();
    if (selectedIndex != -1) {
      Recipe recipe = recipes.get(selectedIndex);
      recipeDetails.setText(recipe.getDetailedInfo());
      nutritionGUI.updateNutritionDisplay(recipe);
    }
  }

  private void populateRecipeList() {
    for (Recipe recipe : recipes) {
      recipeListModel.addElement(recipe.getName());
    }
  }

  public static void main(String[] args) {
    // Example usage
    List<Recipe> recipes = new ArrayList<>();
    // Add sample recipes here

    SwingUtilities.invokeLater(() -> {
      RecipeGUI mainGUI = new RecipeGUI(recipes);
      mainGUI.setVisible(true);
    });
  }
} 