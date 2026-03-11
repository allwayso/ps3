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
    
 // Testing Strategy for differentiate:
    // Partition on expression:
    //   - Constant: number
    //   - Variable: same as differentiate variable, different from differentiate variable
    //   - Plus: x + y
    //   - Multiple: x * y
    //   - Nested: (x + y) * z
    // Partition on differentiation variable:
    //   - Appears in expression
    //   - Does not appear in expression
    
    @Test
    public void testDifferentiateConstant() {
        assertEquals("0", Commands.differentiate("5", "x"));
        assertEquals("0", Commands.differentiate("1.23", "y"));
    }

    @Test
    public void testDifferentiateVariableSame() {
        // Result could be 1 or 1.0 depending on implementation
        String result = Commands.differentiate("x", "x");
        assertTrue(result.equals("1") || result.equals("1.0"));
    }

    @Test
    public void testDifferentiateVariableDifferent() {
        assertEquals("0", Commands.differentiate("y", "x"));
    }

    @Test
    public void testDifferentiateSum() {
        // d/dx(x + y) = 1 + 0
        String result = Commands.differentiate("x + y", "x");
        // We look for structural equivalence or simplest form
        assertTrue(result.contains("1") && result.contains("0"));
    }

    @Test
    public void testDifferentiateProduct() {
        // d/dx(x * x) = x*1 + 1*x
        String result = Commands.differentiate("x * x", "x");
        assertTrue(result.contains("*"));
        assertTrue(result.contains("+"));
    }
    
 // Testing Strategy for simplify:
    // Partition on environment:
    //   - Empty map
    //   - Partial map (some variables substituted)
    //   - Full map (all variables substituted -> result is a number)
    // Partition on expression:
    //   - Constant folding: 1 + 2 -> 3
    //   - Variable replacement: x -> 5
    //   - No change: variables not in map
    
    @Test
    public void testSimplifyConstantFolding() {
        assertEquals("3.0", Commands.simplify("1 + 2", new HashMap<>()));
        assertEquals("6.0", Commands.simplify("2 * 3", new HashMap<>()));
    }

    @Test
    public void testSimplifyPartialSubstitution() {
        Map<String, Double> env = new HashMap<>();
        env.put("x", 2.0);
        String result = Commands.simplify("x + y", env);
        // Result should contain "2" and "y"
        assertTrue(result.contains("2"));
        assertTrue(result.contains("y"));
    }

    @Test
    public void testSimplifyFullSubstitution() {
        Map<String, Double> env = new HashMap<>();
        env.put("x", 2.0);
        env.put("y", 3.0);
        // (2 + 3) * 2 = 10
        assertEquals("10.0", Commands.simplify("(x + y) * x", env));
    }

    @Test
    public void testSimplifyNoVariablesInMap() {
        Map<String, Double> env = new HashMap<>();
        env.put("z", 10.0);
        String result = Commands.simplify("x + y", env);
        assertTrue(result.contains("x"));
        assertTrue(result.contains("y"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentiateInvalidExpression() {
        Commands.differentiate("x ++ 1", "x");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSimplifyInvalidExpression() {
        Commands.simplify("1 *", new HashMap<>());
    }
    
}
