import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.NutritionInfo;

/**
 * Test class for NutritionInfo.
 * Tests the creation, calculation, and combination of nutritional information.
 */
public class NutritionInfoTest {
    
    /**
     * Tests the creation of a NutritionInfo object with standard values.
     */
    @Test
    public void testCreateNutritionInfo() {
        NutritionInfo info = new NutritionInfo(20, 10, 30, 5, 15, 100);
        
        assertEquals(20, info.getProtein(), 0.01, "Protein value is incorrect");
        assertEquals(10, info.getFat(), 0.01, "Fat value is incorrect");
        assertEquals(30, info.getCarbohydrates(), 0.01, "Carbohydrates value is incorrect");
        assertEquals(5, info.getFiber(), 0.01, "Fiber value is incorrect");
        assertEquals(15, info.getSugar(), 0.01, "Sugar value is incorrect");
        assertEquals(100, info.getSodium(), 0.01, "Sodium value is incorrect");
        assertEquals(290, info.getCalories(), 0.01, "Calories calculation is incorrect");
    }

    /**
     * Tests the creation of a NutritionInfo object with zero values.
     */
    @Test
    public void testCreateNutritionInfo_ZeroValues() {
        NutritionInfo info = new NutritionInfo(0, 0, 0, 0, 0, 0);
        
        assertEquals(0, info.getProtein(), 0.01, "Protein value is incorrect");
        assertEquals(0, info.getFat(), 0.01, "Fat value is incorrect");
        assertEquals(0, info.getCarbohydrates(), 0.01, "Carbohydrates value is incorrect");
        assertEquals(0, info.getFiber(), 0.01, "Fiber value is incorrect");
        assertEquals(0, info.getSugar(), 0.01, "Sugar value is incorrect");
        assertEquals(0, info.getSodium(), 0.01, "Sodium value is incorrect");
        assertEquals(0, info.getCalories(), 0.01, "Calories calculation is incorrect");
    }

    /**
     * Tests the creation of a NutritionInfo object with negative values.
     * Note: While negative values are technically allowed, they should be handled appropriately.
     */
    @Test
    public void testCreateNutritionInfo_NegativeValues() {
        NutritionInfo info = new NutritionInfo(-20, -10, -30, -5, -15, -100);
        
        assertEquals(-20, info.getProtein(), 0.01, "Protein value is incorrect");
        assertEquals(-10, info.getFat(), 0.01, "Fat value is incorrect");
        assertEquals(-30, info.getCarbohydrates(), 0.01, "Carbohydrates value is incorrect");
        assertEquals(-5, info.getFiber(), 0.01, "Fiber value is incorrect");
        assertEquals(-15, info.getSugar(), 0.01, "Sugar value is incorrect");
        assertEquals(-100, info.getSodium(), 0.01, "Sodium value is incorrect");
        assertEquals(-290, info.getCalories(), 0.01, "Calories calculation is incorrect");
    }

    /**
     * Tests the addition of two NutritionInfo objects.
     */
    @Test
    public void testAddNutritionInfo() {
        NutritionInfo info1 = new NutritionInfo(20, 10, 30, 5, 15, 100);
        NutritionInfo info2 = new NutritionInfo(10, 5, 15, 2, 7, 50);
        
        NutritionInfo result = info1.add(info2);
        
        assertEquals(30, result.getProtein(), 0.01, "Protein addition is incorrect");
        assertEquals(15, result.getFat(), 0.01, "Fat addition is incorrect");
        assertEquals(45, result.getCarbohydrates(), 0.01, "Carbohydrates addition is incorrect");
        assertEquals(7, result.getFiber(), 0.01, "Fiber addition is incorrect");
        assertEquals(22, result.getSugar(), 0.01, "Sugar addition is incorrect");
        assertEquals(150, result.getSodium(), 0.01, "Sodium addition is incorrect");
        assertEquals(435, result.getCalories(), 0.01, "Calories addition is incorrect");
    }

    /**
     * Tests the addition of NutritionInfo objects with zero values.
     */
    @Test
    public void testAddNutritionInfo_WithZero() {
        NutritionInfo info1 = new NutritionInfo(20, 10, 30, 5, 15, 100);
        NutritionInfo info2 = new NutritionInfo(0, 0, 0, 0, 0, 0);
        
        NutritionInfo result = info1.add(info2);
        
        assertEquals(20, result.getProtein(), 0.01, "Protein addition with zero is incorrect");
        assertEquals(10, result.getFat(), 0.01, "Fat addition with zero is incorrect");
        assertEquals(30, result.getCarbohydrates(), 0.01, "Carbohydrates addition with zero is incorrect");
        assertEquals(5, result.getFiber(), 0.01, "Fiber addition with zero is incorrect");
        assertEquals(15, result.getSugar(), 0.01, "Sugar addition with zero is incorrect");
        assertEquals(100, result.getSodium(), 0.01, "Sodium addition with zero is incorrect");
        assertEquals(290, result.getCalories(), 0.01, "Calories addition with zero is incorrect");
    }

    /**
     * Tests the multiplication of a NutritionInfo object by a factor.
     */
    @Test
    public void testMultiplyNutritionInfo() {
        NutritionInfo info = new NutritionInfo(20, 10, 30, 5, 15, 100);
        double factor = 2.5;
        
        NutritionInfo result = info.multiply(factor);
        
        assertEquals(50, result.getProtein(), 0.01, "Protein multiplication is incorrect");
        assertEquals(25, result.getFat(), 0.01, "Fat multiplication is incorrect");
        assertEquals(75, result.getCarbohydrates(), 0.01, "Carbohydrates multiplication is incorrect");
        assertEquals(12.5, result.getFiber(), 0.01, "Fiber multiplication is incorrect");
        assertEquals(37.5, result.getSugar(), 0.01, "Sugar multiplication is incorrect");
        assertEquals(250, result.getSodium(), 0.01, "Sodium multiplication is incorrect");
        assertEquals(725, result.getCalories(), 0.01, "Calories multiplication is incorrect");
    }

    /**
     * Tests the multiplication of a NutritionInfo object by zero.
     */
    @Test
    public void testMultiplyNutritionInfo_ByZero() {
        NutritionInfo info = new NutritionInfo(20, 10, 30, 5, 15, 100);
        
        NutritionInfo result = info.multiply(0);
        
        assertEquals(0, result.getProtein(), 0.01, "Protein multiplication by zero is incorrect");
        assertEquals(0, result.getFat(), 0.01, "Fat multiplication by zero is incorrect");
        assertEquals(0, result.getCarbohydrates(), 0.01, "Carbohydrates multiplication by zero is incorrect");
        assertEquals(0, result.getFiber(), 0.01, "Fiber multiplication by zero is incorrect");
        assertEquals(0, result.getSugar(), 0.01, "Sugar multiplication by zero is incorrect");
        assertEquals(0, result.getSodium(), 0.01, "Sodium multiplication by zero is incorrect");
        assertEquals(0, result.getCalories(), 0.01, "Calories multiplication by zero is incorrect");
    }

    /**
     * Tests the multiplication of a NutritionInfo object by a negative factor.
     */
    @Test
    public void testMultiplyNutritionInfo_ByNegative() {
        NutritionInfo info = new NutritionInfo(20, 10, 30, 5, 15, 100);
        double factor = -1.5;
        
        NutritionInfo result = info.multiply(factor);
        
        assertEquals(-30, result.getProtein(), 0.01, "Protein multiplication by negative is incorrect");
        assertEquals(-15, result.getFat(), 0.01, "Fat multiplication by negative is incorrect");
        assertEquals(-45, result.getCarbohydrates(), 0.01, "Carbohydrates multiplication by negative is incorrect");
        assertEquals(-7.5, result.getFiber(), 0.01, "Fiber multiplication by negative is incorrect");
        assertEquals(-22.5, result.getSugar(), 0.01, "Sugar multiplication by negative is incorrect");
        assertEquals(-150, result.getSodium(), 0.01, "Sodium multiplication by negative is incorrect");
        assertEquals(-435, result.getCalories(), 0.01, "Calories multiplication by negative is incorrect");
    }

    /**
     * Tests the toString method of NutritionInfo.
     */
    @Test
    public void testToString() {
        NutritionInfo info = new NutritionInfo(20, 10, 30, 5, 15, 100);
        String expected = "NutritionInfo[calories=290.0, protein=20.0, carbs=30.0, fat=10.0, fiber=5.0, sugar=15.0, sodium=100.0]";
        
        assertEquals(expected, info.toString(), "toString output is incorrect");
    }

    /**
     * Tests the toString method with zero values.
     */
    @Test
    public void testToString_ZeroValues() {
        NutritionInfo info = new NutritionInfo(0, 0, 0, 0, 0, 0);
        String expected = "NutritionInfo[calories=0.0, protein=0.0, carbs=0.0, fat=0.0, fiber=0.0, sugar=0.0, sodium=0.0]";
        
        assertEquals(expected, info.toString(), "toString output with zero values is incorrect");
    }
} 