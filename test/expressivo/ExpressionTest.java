/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Tests for the Expression abstract data type.
 */
public class ExpressionTest {

    // Testing strategy
    //   TODO
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /*
     * Testing Strategy for equals() and hashCode():
     *
     * Partition for equals(thatObject):
     * - thatObject: null, not an Expression, Expression (same variant), Expression (different variant)
     * - Structure: 
     * - Identity: same instance vs different instances
     * - Leaves: Number (same value, different value), Variable (same name, different name)
     * - Recursive nodes: 
     * - Same operator with identical children
     * - Same operator with children in different order (e.g., x+y vs y+x)
     * - Different operator with same children (e.g., 1+2 vs 1*2)
     * - Deeply nested trees (different shapes)
     * - Case sensitivity: variable names "x" vs "X"
     * * Partition for hashCode():
     * - Contract: if e1.equals(e2), then e1.hashCode() == e2.hashCode()
     * - Differentiation: different expressions should ideally have different hash codes
     */


    // ---- equals() and hashCode() tests ----

    // Covers: thatObject is null
    @Test
    public void testEqualsNull() {
        Expression n = new Number(5);
        assertFalse("expected not equal to null", n.equals(null));
    }

    // Covers: thatObject is not an Expression
    @Test
    public void testEqualsDifferentType() {
        Expression v = new Variable("x");
        assertFalse("expected not equal to a String object", v.equals("x"));
    }

    // Covers: same leaves (Number, Variable), hashCode consistency
    @Test
    public void testEqualsSameLeaves() {
        Expression n1 = new Number(3.14);
        Expression n2 = new Number(3.14);
        Expression v1 = new Variable("alpha");
        Expression v2 = new Variable("alpha");
        
        assertEquals("identical numbers should be equal", n1, n2);
        assertEquals("identical variables should be equal", v1, v2);
        assertEquals("equal expressions must have same hashCode", n1.hashCode(), n2.hashCode());
        assertEquals("equal expressions must have same hashCode", v1.hashCode(), v2.hashCode());
    }

    // Covers: different leaves (value/name differences, case-sensitivity)
    @Test
    public void testEqualsDifferentLeaves() {
        assertNotEquals("different numbers should be unequal", new Number(1), new Number(2));
        assertNotEquals("different variables should be unequal", new Variable("x"), new Variable("y"));
        assertNotEquals("variable names should be case-sensitive", new Variable("x"), new Variable("X"));
    }

    // Covers: recursive structure - identical trees
    @Test
    public void testEqualsIdenticalRecursive() {
        Expression e1 = new Plus(new Variable("x"), new Number(1));
        Expression e2 = new Plus(new Variable("x"), new Number(1));
        
        assertEquals("identical recursive trees should be equal", e1, e2);
        assertEquals("identical recursive trees should have same hashCode", e1.hashCode(), e2.hashCode());
    }

    // Covers: structural vs mathematical equality (x+y vs y+x)
    @Test
    public void testEqualsSwappedChildren() {
        Expression e1 = new Plus(new Variable("x"), new Variable("y"));
        Expression e2 = new Plus(new Variable("y"), new Variable("x"));
        
        assertNotEquals("x+y and y+x are structurally different", e1, e2);
    }

    // Covers: same children, different operator
    @Test
    public void testEqualsDifferentOperatorSameChildren() {
        Expression e1 = new Plus(new Number(2), new Number(3));
        Expression e2 = new Multiple(new Number(2), new Number(3));
        
        assertNotEquals("2+3 should not equal 2*3 structurally", e1, e2);
    }
    
    /*
     * Testing Strategy for toString():
     *
     * Partition for toString():
     * - Node types: Number, Variable, Plus, Multiple
     * - Numerical formatting: integers, floating-point numbers
     * - Nesting depth: 
     * - Single leaf node
     * - Single operation (one level)
     * - Deeply nested operations (multiple levels)
     * - Precedence and Parentheses:
     * - Operations where parentheses are required (e.g., (1+2)*3)
     * - Operations where parentheses might be optional but present
     * - Round-trip property (Conceptually): 
     * - Result must be a parsable string that would yield an equal Expression.
     * - Consistency: e1.equals(e2) implies e1.toString().equals(e2.toString())
     */

    // Covers: Number and Variable leaf nodes
    @Test
    public void testToStringLeaves() {
        Expression n = new Number(3.14);
        Expression v = new Variable("x");
        
        // We use contains or regex if the exact spacing is not specified in RI
        assertEquals("expected variable name", "x", v.toString().trim());
        assertTrue("expected number value", n.toString().contains("3.14"));
    }

    // Covers: Simple Plus and Multiple
    @Test
    public void testToStringSimpleOperations() {
        Expression e1 = new Plus(new Variable("a"), new Variable("b"));
        Expression e2 = new Multiple(new Number(2), new Variable("z"));
        
        String s1 = e1.toString().replaceAll("\\s+", "");
        String s2 = e2.toString().replaceAll("\\s+", "");
        
        assertEquals("expected a+b", "a+b", s1);
        assertEquals("expected 2*z", "2*z", s2);
    }

    // Covers: Nested structure requiring parentheses for precedence
    // Case: (1 + 2) * 3
    @Test
    public void testToStringPrecedenceParentheses() {
        Expression e = new Multiple(
                            new Plus(new Number(1), new Number(2)), 
                            new Number(3)
                        );
        String s = e.toString().replaceAll("\\s+", "");
        
        // Parentheses are strictly necessary to differentiate from 1 + 2 * 3
        assertEquals("expected (1+2)*3", "(1+2)*3", s);
    }

    // Covers: Deeply nested structure
    // Case: x + (y * (z + 1))
    @Test
    public void testToStringDeeplyNested() {
        Expression innerPlus = new Plus(new Variable("z"), new Number(1));
        Expression innerMult = new Multiple(new Variable("y"), innerPlus);
        Expression root = new Plus(new Variable("x"), innerMult);
        
        String s = root.toString().replaceAll("\\s+", "");
        
        // Structure: x+(y*(z+1))
        assertTrue("should contain nested grouping", s.contains("y*(z+1)") || s.contains("y*(z+1.0)"));
    }

    // Covers: Consistency with equals()
    @Test
    public void testToStringConsistency() {
        Expression e1 = new Plus(new Number(1), new Variable("x"));
        Expression e2 = new Plus(new Number(1), new Variable("x"));
        
        assertEquals("equal expressions must produce same toString", e1.toString(), e2.toString());
    }
    
    /*
     * Testing Strategy for parse():
     *
     * Partition for input string:
     * - Components: numbers (int/double), variables (short/long names, case-sensitive).
     * - Operators: only +, only *, mixed (+ and *).
     * - Order of Operations: PEMDAS (multiply before add), explicit parentheses.
     * - Whitespace: no spaces, spaces around operators, leading/trailing spaces.
     * * Partition for Exceptional cases (throws IllegalArgumentException):
     * - Invalid characters: symbols like ^, -, /, !.
     * - Malformed structure: dangling operators (1+), empty parens (), mismatched parens.
     * - Empty input: "", "   ".
     */

    // Covers: Basic number, variable, and whitespace robustness
    @Test
    public void testParseBasicUnits() {
        assertEquals("expected number", new Number(42), Expression.parse(" 42 "));
        assertEquals("expected decimal", new Number(0.5), Expression.parse("0.500"));
        assertEquals("expected variable", new Variable("varName"), Expression.parse("  varName  "));
    }

    // Covers: Operator precedence (* before +)
    @Test
    public void testParsePrecedence() {
        // 1 + 2 * 3 should be 1 + (2 * 3)
        Expression expected = new Plus(new Number(1), new Multiple(new Number(2), new Number(3)));
        assertEquals("expected * to bind tighter than +", expected, Expression.parse("1+2*3"));
        
        // 1 * 2 + 3 should be (1 * 2) + 3
        Expression expected2 = new Plus(new Multiple(new Number(1), new Number(2)), new Number(3));
        assertEquals("expected * to bind tighter than +", expected2, Expression.parse("1*2+3"));
    }

    // Covers: Parentheses overriding precedence
    @Test
    public void testParseParentheses() {
        // (1 + 2) * 3
        Expression expected = new Multiple(new Plus(new Number(1), new Number(2)), new Number(3));
        assertEquals("expected parentheses to override precedence", expected, Expression.parse("(1+2)*3"));
    }

    // Covers: Deeply nested and complex expressions
    @Test
    public void testParseComplex() {
        // x * (y + z * 3)
        Expression inner = new Plus(new Variable("y"), new Multiple(new Variable("z"), new Number(3)));
        Expression expected = new Multiple(new Variable("x"), inner);
        assertEquals("expected correct tree for x*(y+z*3)", expected, Expression.parse("x * (y + z * 3)"));
    }

    // ---- Negative Tests (Error Handling) ----

    // Covers: Invalid operators
    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidOperator() {
        Expression.parse("x ^ 2"); // ^ is not supported
    }

    // Covers: Malformed syntax (dangling operator)
    @Test(expected = IllegalArgumentException.class)
    public void testParseDanglingOperator() {
        Expression.parse("1 + 2 * ");
    }

    // Covers: Mismatched parentheses
    @Test(expected = IllegalArgumentException.class)
    public void testParseMismatchedParens() {
        Expression.parse("((1+2)");
    }

    // Covers: Empty/Blank input
    @Test(expected = IllegalArgumentException.class)
    public void testParseEmpty() {
        Expression.parse("  ");
    }
    
    /*
     * Testing Strategy for simplify(environment):
     *
     * Partition for environment:
     * - empty map
     * - map with some variables in expression
     * - map with all variables in expression
     * - map with variables NOT in expression
     * * Partition for expression structure:
     * - Single Number/Variable
     * - Plus/Multiple with only numbers (full folding)
     * - Plus/Multiple with partial numbers/variables (partial folding)
     * - Deeply nested trees (recursive folding)
     */

    // Covers: Empty environment, constant folding only
    @Test
    public void testSimplifyNoEnvironment() {
        Map<String, Double> emptyEnv = Collections.emptyMap();
        
        // 1 + 2 -> 3
        Expression e1 = Expression.parse("1 + 2");
        assertEquals("expected 1+2 to fold to 3", new Number(3), e1.simplify(emptyEnv));
        
        // x + (2 * 3) -> x + 6
        Expression e2 = Expression.parse("x + (2 * 3)");
        Expression expected2 = new Plus(new Variable("x"), new Number(6));
        assertEquals("expected partial folding", expected2, e2.simplify(emptyEnv));
    }

    // Covers: Full substitution and folding
    @Test
    public void testSimplifyFullSubstitution() {
        Map<String, Double> env = new HashMap<>();
        env.put("x", 2.0);
        env.put("y", 3.0);
        
        // x * y + 5 -> 2 * 3 + 5 -> 11
        Expression e = Expression.parse("x * y + 5");
        assertEquals("expected 11", new Number(11), e.simplify(env));
    }

    // Covers: Partial substitution (some variables remain)
    @Test
    public void testSimplifyPartialSubstitution() {
        Map<String, Double> env = new HashMap<>();
        env.put("x", 10.0);
        
        // x + y + 2 -> 10 + y + 2 -> 12 + y OR (10+y)+2 depending on structure
        // Note: simplify should fold what it can. 
        // If your parse builds Plus(Plus(x, y), 2), and x=10, result is Plus(Plus(10, y), 2).
        // A smarter simplify might fold it further, but at minimum it must replace x.
        Expression e = Expression.parse("x + y");
        Expression expected = new Plus(new Number(10), new Variable("y"));
        assertEquals("expected x replaced by 10", expected, e.simplify(env));
    }

    // Covers: Deep recursive folding
    @Test
    public void testSimplifyRecursive() {
        Map<String, Double> env = new HashMap<>();
        env.put("a", 1.0);
        
        // (a + 1) * (3 + 4) -> (1 + 1) * 7 -> 2 * 7 -> 14
        Expression e = Expression.parse("(a + 1) * (3 + 4)");
        assertEquals("expected recursive folding to 14", new Number(14), e.simplify(env));
    }

    // Covers: Irrelevant variables in map
    @Test
    public void testSimplifyIrrelevantEnv() {
        Map<String, Double> env = new HashMap<>();
        env.put("z", 99.0);
        
        // x + 1 stays x + 1
        Expression e = Expression.parse("x + 1");
        assertEquals("expected no change", e, e.simplify(env));
    }
    
    /*
     * Testing Strategy for differentiate(variable):
     *
     * Partition for variable:
     * - variable matches the target (d(x)/dx)
     * - variable does not match the target (d(y)/dx)
     * - target is a constant (d(5)/dx)
     * * Partition for expression structure:
     * - Basic: Number, Variable
     * - Sum Rule: Plus(u, v) -> du/dx + dv/dx
     * - Product Rule: Multiple(u, v) -> (u * dv/dx) + (v * du/dx)
     * - Combinations: Mixed Plus and Multiple, nested expressions
     * - Repeated variables: x * x, x + x
     */

    // Covers: Base cases - Numbers and Variables
    @Test
    public void testDifferentiateBaseCases() {
        Expression five = new Number(5);
        Expression x = new Variable("x");
        Expression y = new Variable("y");

        // d(5)/dx = 0
        assertEquals("derivative of constant should be 0", 
                     new Number(0), five.differentiate("x"));
        
        // d(x)/dx = 1
        assertEquals("derivative of x w.r.t x should be 1", 
                     new Number(1), x.differentiate("x"));
        
        // d(y)/dx = 0
        assertEquals("derivative of y w.r.t x should be 0", 
                     new Number(0), y.differentiate("x"));
    }

    // Covers: Sum Rule d(u + v)/dx = du/dx + dv/dx
    @Test
    public void testDifferentiateSumRule() {
        // x + 5
        Expression e = Expression.parse("x + 5");
        // Expected structural result: 1 + 0
        Expression expected = new Plus(new Number(1), new Number(0));
        
        assertEquals("d(x+5)/dx should be 1+0", expected, e.differentiate("x"));
    }

    // Covers: Product Rule d(u * v)/dx = u*dv/dx + v*du/dx
    @Test
    public void testDifferentiateProductRule() {
        // 3 * x
        Expression e = Expression.parse("3 * x");
        // Expected structural result: (3 * 1) + (x * 0) 
        // Note: The order depends on your implementation of product rule
        Expression u = new Number(3);
        Expression v = new Variable("x");
        Expression du = new Number(0);
        Expression dv = new Number(1);
        
        // Following (u * dv) + (v * du)
        Expression expected = new Plus(new Multiple(u, dv), new Multiple(v, du));
        
        assertEquals("d(3*x)/dx should follow product rule", expected, e.differentiate("x"));
    }

    // Covers: Nested expressions (Multiple levels)
    @Test
    public void testDifferentiateNested() {
        // x * x + x
        Expression e = Expression.parse("x * x + x");
        
        // d(x*x + x)/dx = d(x*x)/dx + d(x)/dx
        // = (x*1 + x*1) + 1
        Expression x = new Variable("x");
        Expression one = new Number(1);
        Expression dXX = new Plus(new Multiple(x, one), new Multiple(x, one));
        Expression expected = new Plus(dXX, one);
        
        assertEquals("d(x*x+x)/dx should be (x*1+x*1)+1", expected, e.differentiate("x"));
    }

    // Covers: Case sensitivity in differentiation
    @Test
    public void testDifferentiateCaseSensitivity() {
        Expression e = new Variable("X");
        // d(X)/dx = 0 (case-sensitive)
        assertEquals("X is not x", new Number(0), e.differentiate("x"));
    }
}
