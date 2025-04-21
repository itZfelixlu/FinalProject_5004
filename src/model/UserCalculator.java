package model;

import java.util.Map;
import java.util.HashMap;

/**
 * A utility class for calculating user-specific metabolic values including
 * Basal Metabolic Rate (BMR) and Total Daily Energy Expenditure (TDEE).
 * Uses the Mifflin-St Jeor Equation for BMR calculation and standard activity
 * multipliers for TDEE calculation.
 */
public class UserCalculator {
    
    /**
     * Calculates the Basal Metabolic Rate (BMR) using the Mifflin-St Jeor Equation.
     * BMR represents the number of calories needed to maintain basic bodily functions
     * at rest.
     *
     * @param age The user's age in years
     * @param height The user's height in centimeters
     * @param weight The user's weight in kilograms
     * @param isMale Whether the user is male (true) or female (false)
     * @return The calculated BMR in calories per day
     */
    public double calculateBMR(int age, double height, double weight, boolean isMale) {
        // Mifflin-St Jeor Equation
        if (isMale) {
            return 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            return 10 * weight + 6.25 * height - 5 * age - 161;
        }
    }

    /**
     * Calculates the Total Daily Energy Expenditure (TDEE) based on BMR and activity level.
     * TDEE represents the total number of calories burned in a day, including both
     * BMR and activity-related energy expenditure.
     *
     * @param bmr The user's Basal Metabolic Rate
     * @param activityLevel A string describing the user's activity level
     * @return The calculated TDEE in calories per day
     */
    public double calculateTDEE(double bmr, String activityLevel) {
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

    /**
     * Collects and calculates all user-specific metabolic data.
     * This method serves as a convenience method to calculate both BMR and TDEE
     * in a single call.
     *
     * @param age The user's age in years
     * @param height The user's height in centimeters
     * @param weight The user's weight in kilograms
     * @param isMale Whether the user is male (true) or female (false)
     * @param activityLevel A string describing the user's activity level
     * @return A map containing all calculated metabolic values
     */
    public Map<String, Object> calculateAllUserData(int age, double height, double weight, 
                                                  boolean isMale, String activityLevel) {
        Map<String, Object> userData = new HashMap<>();
        
        double bmr = calculateBMR(age, height, weight, isMale);
        double tdee = calculateTDEE(bmr, activityLevel);

        userData.put("age", age);
        userData.put("height", height);
        userData.put("weight", weight);
        userData.put("isMale", isMale);
        userData.put("bmr", bmr);
        userData.put("tdee", tdee);

        return userData;
    }
} 