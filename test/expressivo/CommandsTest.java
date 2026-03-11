/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for the static methods of Commands.
 */
public class CommandsTest {
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    /**
     * Helper method to verify that the string result from Commands matches the expected structure.
     * It parses both strings into Expression objects and compares them using Expression.equals().
     * 
     * @param expectedString The string representation of the expected expression structure
     * @param actualString The string returned by Commands methods
     */
    private void assertStructuralEquals(String expectedString, String actualString) {
        Expression expected = Expression.parse(expectedString);
        Expression actual = Expression.parse(actualString);
        assertEquals("Expression structures should match", expected, actual);
    }
    
    // Testing Strategy for differentiate:
    // Partition on expression:
    //   - Constant: number
    //   - Variable: same as differentiate variable, different from differentiate variable
    //   - Plus: x + y
    //   - Multiple: x * y
    //   - Nested: (x + y) * z
    
    @Test
    public void testDifferentiateConstant() {
        // d/dx(5) = 0
        assertStructuralEquals("0", Commands.differentiate("5", "x"));
        // d/dy(1.23) = 0
        assertStructuralEquals("0", Commands.differentiate("1.23", "y"));
    }

    @Test
    public void testDifferentiateVariableSame() {
        // d/dx(x) = 1
        assertStructuralEquals("1", Commands.differentiate("x", "x"));
    }

    @Test
    public void testDifferentiateVariableDifferent() {
        // d/dx(y) = 0
        assertStructuralEquals("0", Commands.differentiate("y", "x"));
    }

    @Test
    public void testDifferentiateSum() {
        // d/dx(x + y) = d/dx(x) + d/dx(y) = 1 + 0
        // We expect the exact structure "1 + 0" because differentiate doesn't simplify automatically
        assertStructuralEquals("1 + 0", Commands.differentiate("x + y", "x"));
    }

    @Test
    public void testDifferentiateProduct() {
        // d/dx(x * y) = x * d/dx(y) + y * d/dx(x) = x * 0 + y * 1
        // Note: The order of terms depends on your implementation of the product rule in Expression.differentiate.
        // Assuming standard left * d(right) + right * d(left) rule:
        assertStructuralEquals("x * 0 + y * 1", Commands.differentiate("x * y", "x"));
        
        // d/dx(x * x) = x * 1 + x * 1
        assertStructuralEquals("x * 1 + x * 1", Commands.differentiate("x * x", "x"));
    }
    
    // Testing Strategy for simplify:
    // Partition on environment:
    //   - Empty map
    //   - Partial map
    //   - Full map (result is a number)
    
    @Test
    public void testSimplifyConstantFolding() {
        // 1 + 2 -> 3
        assertStructuralEquals("3", Commands.simplify("1 + 2", new HashMap<>()));
        // 2 * 3 -> 6
        assertStructuralEquals("6", Commands.simplify("2 * 3", new HashMap<>()));
    }

    @Test
    public void testSimplifyPartialSubstitution() {
        Map<String, Double> env = new HashMap<>();
        env.put("x", 2.0);
        
        // x + y -> 2 + y (assuming x is substituted)
        // Note: The formatting of "2" vs "2.0" is handled by Expression.parse logic, making this robust.
        assertStructuralEquals("2 + y", Commands.simplify("x + y", env));
    }

    @Test
    public void testSimplifyFullSubstitution() {
        Map<String, Double> env = new HashMap<>();
        env.put("x", 2.0);
        env.put("y", 3.0);
        
        // (x + y) * x -> (2 + 3) * 2 -> 5 * 2 -> 10
        assertStructuralEquals("10", Commands.simplify("(x + y) * x", env));
    }

    @Test
    public void testSimplifyNoVariablesInMap() {
        Map<String, Double> env = new HashMap<>();
        env.put("z", 10.0);
        
        // x + y (no change structurally)
        assertStructuralEquals("x + y", Commands.simplify("x + y", env));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentiateInvalidExpression() {
        Commands.differentiate("x ++ 1", "x");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSimplifyInvalidExpression() {
        Commands.simplify("1 *", new HashMap<>());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDifferentiateInvalidVariable() {
        // Variable cannot be a number or empty
        Commands.differentiate("x", "1");
    }
}