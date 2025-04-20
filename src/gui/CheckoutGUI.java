package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import model.Recipe;
import model.Ingredient;
import model.PriceCalculator;

public class CheckoutGUI extends JFrame {
    private List<Recipe> selectedRecipes;
    private JPanel mainPanel;
    private JLabel totalLabel;
    private static final Color ACCENT_COLOR = new Color(70, 130, 180); // Steel blue
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color HEADER_COLOR = new Color(51, 51, 51);
    private static final Color REMOVE_BTN_COLOR = new Color(220, 53, 69); // Bootstrap danger red
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font PRICE_FONT = new Font("Arial", Font.BOLD, 15);
    private final PriceCalculator priceCalculator;

    public CheckoutGUI(List<Recipe> selectedRecipes) {
        this.selectedRecipes = new ArrayList<>(selectedRecipes); // Create a mutable copy
        this.priceCalculator = new PriceCalculator();
        
        setTitle("Shopping Cart");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Create main panel with some padding
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        refreshDisplay();

        // Add scrolling
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        // Set size and position
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void refreshDisplay() {
        mainPanel.removeAll();
        createHeader();
        createRecipeList();
        createTotalPanel();
        createCheckoutButton();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel headerLabel = new JLabel("Your Shopping Cart");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(HEADER_COLOR);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        mainPanel.add(headerPanel);
    }

    private void createRecipeList() {
        // Create a map to consolidate ingredients
        Map<String, Double> consolidatedIngredients = new HashMap<>();
        Map<String, Double> consolidatedPrices = new HashMap<>();
        
        JPanel recipesContainer = new JPanel();
        recipesContainer.setLayout(new BoxLayout(recipesContainer, BoxLayout.Y_AXIS));
        recipesContainer.setBackground(BACKGROUND_COLOR);
        
        for (Recipe recipe : selectedRecipes) {
            // Add recipe panel
            JPanel recipePanel = new JPanel(new BorderLayout(15, 10));
            recipePanel.setBackground(Color.WHITE);
            recipePanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));

            // Recipe name and price in a header panel
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.WHITE);
            
            // Left side: Name
            JPanel namePanel = new JPanel(new GridLayout(2, 1));
            namePanel.setBackground(Color.WHITE);
            
            JLabel nameLabel = new JLabel(recipe.getName());
            nameLabel.setFont(HEADER_FONT);
            nameLabel.setForeground(HEADER_COLOR);
            
            namePanel.add(nameLabel);
            headerPanel.add(namePanel, BorderLayout.WEST);

            // Right side: Price and remove button
            JPanel priceRemovePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            priceRemovePanel.setBackground(Color.WHITE);

            // Calculate recipe ingredient cost
            double ingredientCost = calculateIngredientCost(recipe);
            
            JPanel pricePanel = new JPanel(new GridLayout(2, 1));
            pricePanel.setBackground(Color.WHITE);
            
            JLabel recipePriceLabel = new JLabel(String.format("Total: $%.2f", ingredientCost));
            recipePriceLabel.setFont(PRICE_FONT);
            recipePriceLabel.setForeground(ACCENT_COLOR);
            
            pricePanel.add(recipePriceLabel);
            priceRemovePanel.add(pricePanel);

            // Remove button
            JButton removeButton = new JButton("Remove");
            removeButton.setBackground(REMOVE_BTN_COLOR);
            removeButton.setForeground(Color.WHITE);
            removeButton.setFocusPainted(false);
            removeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            removeButton.addActionListener(e -> {
                selectedRecipes.remove(recipe);
                refreshDisplay();
            });
            priceRemovePanel.add(removeButton);

            headerPanel.add(priceRemovePanel, BorderLayout.EAST);
            recipePanel.add(headerPanel, BorderLayout.NORTH);

            // Ingredients list with prices
            JPanel ingredientsPanel = new JPanel();
            ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.Y_AXIS));
            ingredientsPanel.setBackground(Color.WHITE);
            ingredientsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));

            for (Ingredient ingredient : recipe.getIngredients()) {
                // Add to consolidated ingredients
                String key = ingredient.getName() + " (" + ingredient.getUnit() + ")";
                consolidatedIngredients.merge(key, ingredient.getQuantity(), Double::sum);
                consolidatedPrices.merge(key, ingredient.getTotalPrice(), Double::sum);
                
                // Add ingredient to panel with unit price and total
                JPanel ingredientRow = new JPanel(new BorderLayout(5, 0));
                ingredientRow.setBackground(Color.WHITE);
                
                JLabel ingredientLabel = new JLabel(String.format("• %s %s %s", 
                    ingredient.getQuantity(), ingredient.getUnit(), ingredient.getName()));
                ingredientLabel.setFont(REGULAR_FONT);
                ingredientRow.add(ingredientLabel, BorderLayout.WEST);
                
                JPanel priceDetails = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                priceDetails.setBackground(Color.WHITE);
                
                JLabel ingredientPriceLabel = new JLabel(String.format("$%.2f", ingredient.getTotalPrice()));
                ingredientPriceLabel.setFont(REGULAR_FONT);
                priceDetails.add(ingredientPriceLabel);
                
                ingredientRow.add(priceDetails, BorderLayout.EAST);
                
                ingredientsPanel.add(ingredientRow);
                ingredientsPanel.add(Box.createVerticalStrut(5));
            }

            recipePanel.add(ingredientsPanel, BorderLayout.CENTER);
            recipesContainer.add(recipePanel);
            recipesContainer.add(Box.createVerticalStrut(15));
        }

        mainPanel.add(recipesContainer);

        if (!selectedRecipes.isEmpty()) {
            // Add consolidated ingredients section
            createConsolidatedPanel(consolidatedIngredients, consolidatedPrices);
        } else {
            // Show empty cart message
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel emptyLabel = new JLabel("Your shopping cart is empty");
            emptyLabel.setFont(HEADER_FONT);
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            mainPanel.add(emptyPanel);
        }
    }

    private void createConsolidatedPanel(Map<String, Double> ingredients, Map<String, Double> prices) {
        JPanel consolidatedPanel = new JPanel();
        consolidatedPanel.setLayout(new BoxLayout(consolidatedPanel, BoxLayout.Y_AXIS));
        consolidatedPanel.setBackground(Color.WHITE);
        consolidatedPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Header
        JLabel consolidatedHeader = new JLabel("Shopping List Summary");
        consolidatedHeader.setFont(HEADER_FONT);
        consolidatedHeader.setForeground(HEADER_COLOR);
        consolidatedHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        consolidatedPanel.add(consolidatedHeader);
        consolidatedPanel.add(Box.createVerticalStrut(10));

        // Ingredients list
        JPanel ingredientsList = new JPanel();
        ingredientsList.setLayout(new BoxLayout(ingredientsList, BoxLayout.Y_AXIS));
        ingredientsList.setBackground(Color.WHITE);
        ingredientsList.setAlignmentX(Component.LEFT_ALIGNMENT);

        ingredients.forEach((name, quantity) -> {
            JPanel row = new JPanel(new BorderLayout(5, 0));
            row.setBackground(Color.WHITE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

            JLabel quantityLabel = new JLabel(String.format("• %.2f %s", quantity, name));
            quantityLabel.setFont(REGULAR_FONT);
            row.add(quantityLabel, BorderLayout.WEST);

            double price = prices.get(name);
            JLabel priceLabel = new JLabel(String.format("$%.2f", price));
            priceLabel.setFont(REGULAR_FONT);
            row.add(priceLabel, BorderLayout.EAST);

            ingredientsList.add(row);
            ingredientsList.add(Box.createVerticalStrut(5));
        });

        consolidatedPanel.add(ingredientsList);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(consolidatedPanel);
    }

    private void createTotalPanel() {
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        totalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Calculate totals
        double subtotal = selectedRecipes.stream()
            .mapToDouble(priceCalculator::calculateRecipePrice)
            .sum();
        
        double tax = subtotal * 0.0825; // Assuming 8.25% sales tax
        double total = subtotal + tax;

        // Price breakdown
        JPanel breakdownPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        breakdownPanel.setBackground(Color.WHITE);
        breakdownPanel.setMaximumSize(new Dimension(400, 100));
        breakdownPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtotal
        JLabel subtotalLabel = new JLabel("Subtotal:");
        subtotalLabel.setFont(REGULAR_FONT);
        breakdownPanel.add(subtotalLabel);

        JLabel subtotalAmount = new JLabel(String.format("$%.2f", subtotal));
        subtotalAmount.setFont(REGULAR_FONT);
        subtotalAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        breakdownPanel.add(subtotalAmount);

        // Tax
        JLabel taxLabel = new JLabel("Sales Tax (8.25%):");
        taxLabel.setFont(REGULAR_FONT);
        breakdownPanel.add(taxLabel);

        JLabel taxAmount = new JLabel(String.format("$%.2f", tax));
        taxAmount.setFont(REGULAR_FONT);
        taxAmount.setHorizontalAlignment(SwingConstants.RIGHT);
        breakdownPanel.add(taxAmount);

        // Total
        JLabel totalTextLabel = new JLabel("Total:");
        totalTextLabel.setFont(PRICE_FONT);
        totalTextLabel.setForeground(ACCENT_COLOR);
        breakdownPanel.add(totalTextLabel);

        totalLabel = new JLabel(String.format("$%.2f", total));
        totalLabel.setFont(PRICE_FONT);
        totalLabel.setForeground(ACCENT_COLOR);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        breakdownPanel.add(totalLabel);

        totalPanel.add(breakdownPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(totalPanel);
    }

    private void createCheckoutButton() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton checkoutButton = new JButton("Proceed to Checkout");
        checkoutButton.setBackground(ACCENT_COLOR);
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFont(HEADER_FONT);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        checkoutButton.addActionListener(e -> {
            // TODO: Implement checkout process
            JOptionPane.showMessageDialog(this,
                "Thank you for your order!\nYour ingredients will be delivered soon.",
                "Order Confirmed",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });

        buttonPanel.add(checkoutButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);
    }

    private double calculateIngredientCost(Recipe recipe) {
        return priceCalculator.calculateRecipePrice(recipe);
    }
} 