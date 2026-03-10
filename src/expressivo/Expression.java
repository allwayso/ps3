/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import java.util.Map;

/**
 * An immutable data type representing a polynomial expression of:
 *   + and *
 *   nonnegative integers and floating-point numbers
 *   variables (case-sensitive nonempty strings of letters)
 * 
 * <p>PS3 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 * Declare concrete variants of Expression in their own Java source files.
 */
public interface Expression {
    
    // Datatype definition:
    // Expression=Number(n:double)+Variable(s:String)+Plus(left:expression,right:expression)+Multiple(leftLexpr,right:expression)
    
    /**
     * Differentiates this expression with respect to a variable.
     * 
     * @param variable non-empty case-sensitive letter sequence.
     * @return a new Expression representing the derivative.
     * @throws IllegalArgumentException if variable format is invalid.
     */
    public Expression differentiate(String variable);

    /**
     * Simplifies this expression by substituting variables and folding constants.
     * 
     * @param environment map from variable names to values; may be empty.
     * @return a simplified Expression equivalent to the original under the given environment.
     */
    public Expression simplify(Map<String, Double> environment);
    
    
    /**
     * Parse an expression from a string.
     * * <p>The input must follow these grammar rules:
     * <ul>
     * <li>Supported operators: {@code +} (addition) and {@code *} (multiplication).</li>
     * <li>Operator precedence: {@code *} has higher precedence than {@code +}.</li>
     * <li>Grouping: Parentheses {@code ()} can be used to override default precedence.</li>
     * <li>Numbers: Non-negative integers or floating-point numbers (e.g., "7", "4.2").</li>
     * <li>Variables: Case-sensitive non-empty sequences of letters (e.g., "y", "Foo").</li>
     * <li>Whitespace: Leading/trailing whitespace and spaces around operators or 
     * parentheses are ignored (e.g., "x + y" is equivalent to "x+y").</li>
     * </ul>
     *
     * @param input expression string to parse. Must not be null.
     * @return an Expression AST representing the input string.
     * @throws IllegalArgumentException if the input string does not conform to the 
     * grammar rules, contains unsupported operators, or is mathematically 
     * malformed (e.g., "x +", "(x + y").
     */
    public static Expression parse(String input) {
        throw new RuntimeException("unimplemented");
    }
    
    /**
     * @return a string representation of this expression that can be 
     * parsed back into an Expression structurally equal to this one.
     * The output should only contain numbers, variables, '+', '*', 
     * and parentheses where necessary.
     */
    @Override 
    public String toString();

    /**
     * Compares the specified object with this expression for structural equality.
     * Two expressions are structurally equal if they have the same tree structure,
     * including the same operations and identical constants/variables 
     * in the same positions. Note that (x+y) is NOT structurally equal to (y+x).
     * * @param thatObject any object
     * @return true if this and thatObject represent the same expression tree
     */
    @Override
    public boolean equals(Object thatObject);
    
    /**
     * @return a hash code value consistent with the definition of structural 
     * equality. If e1.equals(e2), then e1.hashCode() == e2.hashCode().
     */
    @Override
    public int hashCode();
    
    // TODO more instance methods
    
}
