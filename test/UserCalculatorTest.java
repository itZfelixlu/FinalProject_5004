import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.UserCalculator;
import java.util.Map;

/**
 * Test class for UserCalculator.
 * Tests the calculation of BMR and TDEE values with various input scenarios.
 */
public class UserCalculatorTest {
    private final UserCalculator calculator = new UserCalculator();

    /**
     * Tests BMR calculation for a male user with standard inputs.
     */
    @Test
    public void testCalculateBMR_Male() {
        double bmr = calculator.calculateBMR(30, 180, 80, true);
        assertEquals(1780.0, bmr, 0.01, "BMR calculation for male is incorrect");
    }

    /**
     * Tests BMR calculation for a female user with standard inputs.
     */
    @Test
    public void testCalculateBMR_Female() {
        double bmr = calculator.calculateBMR(30, 180, 80, false);
        assertEquals(1614.0, bmr, 0.01, "BMR calculation for female is incorrect");
    }

    /**
     * Tests BMR calculation with edge case values (minimum possible values).
     */
    @Test
    public void testCalculateBMR_MinimumValues() {
        double bmr = calculator.calculateBMR(1, 1, 1, true);
        assertEquals(16.25, bmr, 0.01, "BMR calculation with minimum values is incorrect");
    }

    /**
     * Tests TDEE calculation for sedentary activity level.
     */
    @Test
    public void testCalculateTDEE_Sedentary() {
        double tdee = calculator.calculateTDEE(2000, "Sedentary");
        assertEquals(2400, tdee, 0.01, "TDEE calculation for sedentary is incorrect");
    }

    /**
     * Tests TDEE calculation for lightly active activity level.
     */
    @Test
    public void testCalculateTDEE_LightlyActive() {
        double tdee = calculator.calculateTDEE(2000, "Lightly active");
        assertEquals(2750, tdee, 0.01, "TDEE calculation for lightly active is incorrect");
    }

    /**
     * Tests TDEE calculation for moderately active activity level.
     */
    @Test
    public void testCalculateTDEE_ModeratelyActive() {
        double tdee = calculator.calculateTDEE(2000, "Moderately active");
        assertEquals(3100, tdee, 0.01, "TDEE calculation for moderately active is incorrect");
    }

    /**
     * Tests TDEE calculation for very active activity level.
     */
    @Test
    public void testCalculateTDEE_VeryActive() {
        double tdee = calculator.calculateTDEE(2000, "Very active");
        assertEquals(3450, tdee, 0.01, "TDEE calculation for very active is incorrect");
    }

    /**
     * Tests TDEE calculation for extra active activity level.
     */
    @Test
    public void testCalculateTDEE_ExtraActive() {
        double tdee = calculator.calculateTDEE(2000, "Extra active");
        assertEquals(3800, tdee, 0.01, "TDEE calculation for extra active is incorrect");
    }

    /**
     * Tests TDEE calculation with unknown activity level (should default to sedentary).
     */
    @Test
    public void testCalculateTDEE_UnknownActivity() {
        double tdee = calculator.calculateTDEE(2000, "Unknown activity");
        assertEquals(2400, tdee, 0.01, "TDEE calculation for unknown activity is incorrect");
    }

    /**
     * Tests the complete user data calculation with all fields.
     */
    @Test
    public void testCalculateAllUserData() {
        Map<String, Object> data = calculator.calculateAllUserData(30, 180, 80, true, "Moderately active");
        
        assertEquals(30, data.get("age"));
        assertEquals(180.0, data.get("height"));
        assertEquals(80.0, data.get("weight"));
        assertEquals(true, data.get("isMale"));
        
        double bmr = (double) data.get("bmr");
        double tdee = (double) data.get("tdee");
        
        assertEquals(1780.0, bmr, 0.01, "BMR in all user data is incorrect");
        assertEquals(2759.0, tdee, 0.01, "TDEE in all user data is incorrect");
    }
} 