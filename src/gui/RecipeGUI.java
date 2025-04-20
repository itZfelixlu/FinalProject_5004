package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.Recipe;
import model.NutritionInfo;
import filters.IRecipeFilter;
import filters.TextSearchFilter;
import filters.CuisineFilter;
import filters.CalorieRangeFilter;
import filters.PrepTimeFilter;
import model.Ingredient;

public class RecipeGUI extends JFrame {
    private List<Recipe> recipes;
    private List<Recipe> allRecipes;
    private List<Recipe> addedRecipes;
    private Map<String, Object> userData;
    private JPanel recipeCardsPanel;
    private JPanel recipeDetailsPanel;
    private Recipe selectedRecipe;
    private JTextField searchField;
    private JComboBox<String> cuisineFilter;
    private JComboBox<String> calorieFilter;
    private JComboBox<String> prepTimeFilter;
    private RecipeNutritionGUI nutritionGUI;
    private List<IRecipeFilter> filters;

    public RecipeGUI(List<Recipe> recipes, Map<String, Object> userData) {
        this.userData = userData;
        this.allRecipes = new ArrayList<>(recipes); // Create defensive copy
        this.recipes = new ArrayList<>(recipes);
        this.addedRecipes = new ArrayList<>();

        // Initialize filters
        this.filters = new ArrayList<>();
        filters.add(new TextSearchFilter());
        filters.add(new CuisineFilter());
        filters.add(new CalorieRangeFilter());
        filters.add(new PrepTimeFilter());

        setTitle("Shopping APP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1300, 1000)); // Set minimum window size
        setLayout(new BorderLayout(10, 10));

        // Initialize nutrition GUI
        nutritionGUI = new RecipeNutritionGUI(userData);
        nutritionGUI.positionDetailWindow(this);

        // Create main panels
        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        // Add panels to frame with resizable split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(0.5); // Start with equal sizes
        splitPane.setResizeWeight(0.5); // Both sides resize equally
        add(splitPane, BorderLayout.CENTER);

        // Initial population of recipes
        filterAndDisplayRecipes();
        
        // Center the window
        setLocationRelativeTo(null);
    }

    private void filterAndDisplayRecipes() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCuisine = (String) cuisineFilter.getSelectedItem();
        String selectedCalorieRange = (String) calorieFilter.getSelectedItem();
        String selectedPrepTimeRange = (String) prepTimeFilter.getSelectedItem();

        List<Recipe> filteredRecipes = new ArrayList<>(allRecipes);

        // Apply text search filter
        filteredRecipes = filteredRecipes.stream()
            .filter(recipe -> filters.get(0).matches(recipe, searchText))
            .collect(Collectors.toList());

        // Apply cuisine filter
        filteredRecipes = filteredRecipes.stream()
            .filter(recipe -> filters.get(1).matches(recipe, selectedCuisine))
            .collect(Collectors.toList());

        // Apply calorie range filter
        filteredRecipes = filteredRecipes.stream()
            .filter(recipe -> filters.get(2).matches(recipe, selectedCalorieRange))
            .collect(Collectors.toList());

        // Apply prep time filter
        filteredRecipes = filteredRecipes.stream()
            .filter(recipe -> filters.get(3).matches(recipe, selectedPrepTimeRange))
            .collect(Collectors.toList());

        displayRecipeCards(filteredRecipes);
    }

    private boolean matchesFilters(Recipe recipe, String searchText, 
                                 String cuisine, String calorieRange, String prepTimeRange) {
        if (recipe == null) {
            return false;
        }

        // Search text matching
        boolean matchesSearch = searchText.isEmpty();
        if (!matchesSearch) {
            String searchLower = searchText.toLowerCase();
            matchesSearch = recipe.getName().toLowerCase().contains(searchLower) ||
                          recipe.getFlavor().toLowerCase().contains(searchLower) ||
                          recipe.getCuisine().toLowerCase().contains(searchLower) ||
                          recipe.getFlavorTags().stream()
                              .anyMatch(tag -> tag.toLowerCase().contains(searchLower));
        }

        // Cuisine matching - more lenient comparison
        boolean matchesCuisine = true;
        if (!"All".equalsIgnoreCase(cuisine)) {
            String recipeCuisine = recipe.getCuisine().toLowerCase().trim();
            String selectedCuisine = cuisine.toLowerCase().trim();
            matchesCuisine = recipeCuisine.contains(selectedCuisine) || selectedCuisine.contains(recipeCuisine);
        }

        // Calorie range matching
        boolean matchesCalories = matchesCalorieRange(recipe.getCalories(), calorieRange);

        // Prep time matching
        boolean matchesPrepTime = matchesPrepTimeRange(recipe.getPrepTime(), prepTimeRange);

        return matchesSearch && matchesCuisine && matchesCalories && matchesPrepTime;
    }

    private boolean matchesCalorieRange(int calories, String range) {
        if ("All".equals(range)) return true;
        
        try {
            switch (range) {
                case "0-300":
                    return calories >= 0 && calories <= 300;
                case "301-600":
                    return calories >= 301 && calories <= 600;
                case "601-900":
                    return calories >= 601 && calories <= 900;
                case "901+":
                    return calories >= 901;
                default:
                    return true;
            }
        } catch (Exception e) {
            return true;
        }
    }

    private boolean matchesPrepTimeRange(int prepTime, String range) {
        if ("All".equals(range)) return true;
        
        try {
            switch (range) {
                case "0-15 min":
                    return prepTime >= 0 && prepTime <= 15;
                case "16-30 min":
                    return prepTime >= 16 && prepTime <= 30;
                case "31-45 min":
                    return prepTime >= 31 && prepTime <= 45;
                case "46+ min":
                    return prepTime >= 46;
                default:
                    return true;
            }
        } catch (Exception e) {
            return true;
        }
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create top panel for search and filters
        JPanel topPanel = new JPanel(new BorderLayout(5, 10));
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        topPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Filters panel
        JPanel filtersPanel = createFiltersPanel();
        topPanel.add(filtersPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create a container panel for the recipe cards with a custom layout
        JPanel cardsContainer = new JPanel(new BorderLayout());
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Recipe cards panel with WrapLayout
        recipeCardsPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                // Calculate the width based on the viewport width
                int containerWidth = getParent() != null ? getParent().getWidth() : 800;
                int cardWidth = 250; // Width of each card
                int hgap = 15; // Horizontal gap between cards
                
                // Calculate number of cards that can fit in a row
                int cardsPerRow = Math.max(1, (containerWidth - 40) / (cardWidth + hgap));
                
                // Calculate total rows needed
                int componentCount = getComponentCount();
                int rows = (int) Math.ceil((double) componentCount / cardsPerRow);
                
                // Calculate total height
                int cardHeight = 150; // Height of each card
                int vgap = 15; // Vertical gap between cards
                int totalHeight = rows * (cardHeight + vgap) + vgap;
                
                return new Dimension(containerWidth - 40, totalHeight);
            }

            @Override
            public void doLayout() {
                synchronized (getTreeLock()) {
                    int containerWidth = getWidth();
                    int cardWidth = 250; // Width of each card
                    int cardHeight = 150; // Height of each card
                    int hgap = 15; // Horizontal gap
                    int vgap = 15; // Vertical gap
                    
                    // Calculate number of cards that can fit in a row
                    int cardsPerRow = Math.max(1, (containerWidth - hgap) / (cardWidth + hgap));
                    
                    // Position each component
                    int x = hgap;
                    int y = vgap;
                    int componentCount = getComponentCount();
                    
                    for (int i = 0; i < componentCount; i++) {
                        Component c = getComponent(i);
                        if (c.isVisible()) {
                            c.setBounds(x, y, cardWidth, cardHeight);
                            
                            x += cardWidth + hgap;
                            if ((i + 1) % cardsPerRow == 0) {
                                x = hgap;
                                y += cardHeight + vgap;
                            }
                        }
                    }
                }
            }
        };
        recipeCardsPanel.setBackground(Color.WHITE);
        recipeCardsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Wrap the cards panel in a scroll pane
        JScrollPane scrollPane = new JScrollPane(recipeCardsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add a component listener to handle resize events
        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                recipeCardsPanel.revalidate();
                recipeCardsPanel.repaint();
            }
        });

        cardsContainer.add(scrollPane, BorderLayout.CENTER);
        panel.add(cardsContainer, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> performSearch());
        panel.add(searchField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        
        // Cuisine filter
        JPanel cuisinePanel = new JPanel(new BorderLayout(5, 5));
        cuisinePanel.add(new JLabel("Cuisines"), BorderLayout.WEST);
        String[] cuisines = {
            "All", 
            "American",
            "Chinese", 
            "French", 
            "Greek",
            "Indian",
            "Italian",
            "Japanese",
            "Korean",
            "Mexican",
            "Middle Eastern",
            "Thai",
            "Vietnamese"
        };
        cuisineFilter = new JComboBox<>(cuisines);
        cuisinePanel.add(cuisineFilter, BorderLayout.CENTER);
        panel.add(cuisinePanel);

        // Calories filter
        JPanel caloriesPanel = new JPanel(new BorderLayout(5, 5));
        caloriesPanel.add(new JLabel("Calories"), BorderLayout.WEST);
        String[] calories = {"All", "0-300", "301-600", "601-900", "901+"};
        calorieFilter = new JComboBox<>(calories);
        caloriesPanel.add(calorieFilter, BorderLayout.CENTER);
        panel.add(caloriesPanel);

        // Prep time filter
        JPanel prepTimePanel = new JPanel(new BorderLayout(5, 5));
        prepTimePanel.add(new JLabel("Prep time"), BorderLayout.WEST);
        String[] prepTimes = {"All", "0-15 min", "16-30 min", "31-45 min", "46+ min"};
        prepTimeFilter = new JComboBox<>(prepTimes);
        prepTimePanel.add(prepTimeFilter, BorderLayout.CENTER);
        panel.add(prepTimePanel);

        // Go button
        JButton goButton = new JButton("Go");
        goButton.addActionListener(e -> performSearch());
        panel.add(goButton);

        return panel;
    }

    private void performSearch() {
        filterAndDisplayRecipes();
    }

    private void displayRecipeCards(List<Recipe> recipes) {
        recipeCardsPanel.removeAll();
        
        if (recipes.isEmpty()) {
            // Create a panel to center the "no results" message
            JPanel centerPanel = new JPanel(new GridBagLayout());
            JLabel noResultsLabel = new JLabel("No matching recipes found", SwingConstants.CENTER);
            noResultsLabel.setFont(new Font("Arial", Font.BOLD, 14));
            centerPanel.add(noResultsLabel);
            recipeCardsPanel.add(centerPanel);
        } else {
            for (Recipe recipe : recipes) {
                JPanel card = createRecipeCard(recipe);
                recipeCardsPanel.add(card);
            }
        }

        recipeCardsPanel.revalidate();
        recipeCardsPanel.repaint();
    }

    private JPanel createRecipeCard(Recipe recipe) {
        // Create a fixed-size card
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setPreferredSize(new Dimension(250, 150));
        card.setMinimumSize(new Dimension(250, 150));
        card.setMaximumSize(new Dimension(250, 150));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Recipe name
        JLabel nameLabel = new JLabel(recipe.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(nameLabel, BorderLayout.NORTH);

        // Basic info panel
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        infoPanel.add(new JLabel("Cuisine: " + recipe.getCuisine()));
        infoPanel.add(new JLabel("Calories: " + recipe.getCalories()));
        infoPanel.add(new JLabel("Prep Time: " + recipe.getPrepTime() + " min"));
        card.add(infoPanel, BorderLayout.CENTER);

        // Make the entire card clickable
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedRecipe = recipe;
                displayRecipeDetails(recipe);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLUE, 2),
                    BorderFactory.createEmptyBorder(9, 9, 9, 9)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        });

        return card;
    }

    private void displayRecipeDetails(Recipe recipe) {
        recipeDetailsPanel.removeAll();
        recipeDetailsPanel.setLayout(new BorderLayout(10, 10));

        // Recipe title
        JLabel titleLabel = new JLabel(recipe.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        recipeDetailsPanel.add(titleLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Ingredients section
        JPanel ingredientsPanel = new JPanel(new BorderLayout());
        JLabel ingredientsLabel = new JLabel("Ingredients:");
        ingredientsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        ingredientsPanel.add(ingredientsLabel, BorderLayout.NORTH);
        
        JTextArea ingredientsArea = new JTextArea(recipe.getIngredientsText());
        ingredientsArea.setEditable(false);
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        ingredientsPanel.add(new JScrollPane(ingredientsArea), BorderLayout.CENTER);
        contentPanel.add(ingredientsPanel);

        // Nutrition section
        JPanel nutritionPanel = new JPanel(new BorderLayout());
        JLabel nutritionLabel = new JLabel("Nutrition Information:");
        nutritionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nutritionPanel.add(nutritionLabel, BorderLayout.NORTH);
        
        JTextArea nutritionArea = new JTextArea(recipe.getNutritionText());
        nutritionArea.setEditable(false);
        nutritionArea.setLineWrap(true);
        nutritionArea.setWrapStyleWord(true);
        nutritionPanel.add(new JScrollPane(nutritionArea), BorderLayout.CENTER);
        contentPanel.add(nutritionPanel);

        // Preparation section
        JPanel prepPanel = new JPanel(new BorderLayout());
        JLabel prepLabel = new JLabel("Preparation Steps:");
        prepLabel.setFont(new Font("Arial", Font.BOLD, 14));
        prepPanel.add(prepLabel, BorderLayout.NORTH);
        
        JTextArea prepArea = new JTextArea(recipe.getPrepText());
        prepArea.setEditable(false);
        prepArea.setLineWrap(true);
        prepArea.setWrapStyleWord(true);
        prepPanel.add(new JScrollPane(prepArea), BorderLayout.CENTER);
        contentPanel.add(prepPanel);

        recipeDetailsPanel.add(contentPanel, BorderLayout.CENTER);

        // Add to shopping list button
        JButton addButton = new JButton("Add to Shopping List");
        addButton.addActionListener(e -> addRecipe(recipe));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        recipeDetailsPanel.add(buttonPanel, BorderLayout.SOUTH);

        recipeDetailsPanel.revalidate();
        recipeDetailsPanel.repaint();
    }

    private void addRecipe(Recipe recipe) {
        if (recipe != null) {
            addedRecipes.add(recipe);
            nutritionGUI.addRecipe(recipe);
            JOptionPane.showMessageDialog(this,
                "Recipe added to shopping list: " + recipe.getName(),
                "Added to Shopping List",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create top panel with Nutrition Summary button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton nutritionButton = new JButton("Nutrition Summary");
        nutritionButton.addActionListener(e -> {
            if (!addedRecipes.isEmpty()) {
                nutritionGUI.showDetailWindow();
            } else {
                JOptionPane.showMessageDialog(this, "Please add some recipes first", "No Recipes Added", JOptionPane.WARNING_MESSAGE);
            }
        });
        topPanel.add(nutritionButton);
        panel.add(topPanel, BorderLayout.NORTH);

        recipeDetailsPanel = new JPanel(new BorderLayout(10, 10));
        recipeDetailsPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(recipeDetailsPanel, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Recipe> recipes = new ArrayList<>();
            // Add sample recipes here
            RecipeGUI gui = new RecipeGUI(recipes, null);
            gui.setVisible(true);
        });
    }
} 