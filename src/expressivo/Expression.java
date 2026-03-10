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
     * @param environment a map from variable names to their numerical values.
     * The environment must not be modified by this method.
     * @return an Expression where variables present in the environment are 
     * replaced by their values, and constant subexpressions are 
     * evaluated (folded) into a single Number where possible.
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
        if (input == null) {
            throw new IllegalArgumentException("Input must not be null");
        }
        
        // Step 1: Tokenize - Add spaces around operators/parens to ensure proper splitting
        String sanitized = input.replace("(", " ( ").replace(")", " ) ")
                                .replace("+", " + ").replace("*", " * ");
        // Split by any amount of whitespace and remove empty tokens from the array
        String[] tokens = sanitized.trim().split("\\s+");
        
        // Handle purely whitespace or empty input
        if (tokens.length == 0 || (tokens.length == 1 && tokens[0].isEmpty())) {
            throw new IllegalArgumentException("Input string is empty or blank");
        }

        try {
            int[] pos = {0};
            Expression result = parseExpression(tokens, pos);
            
            // Check for trailing garbage (e.g., "x + y 123")
            if (pos[0] < tokens.length) {
                throw new IllegalArgumentException("Unexpected tokens at end of expression");
            }
            return result;
        } catch (IllegalArgumentException e) {
            // Re-throw specific grammar/syntax errors
            throw e;
        } catch (Exception e) {
            // Wrap other unexpected structural errors
            throw new IllegalArgumentException("Malformed expression: " + e.getMessage());
        }
    }

    private static Expression parseExpression(String[] tokens, int[] pos) {
        Expression left = parseTerm(tokens, pos);
        while (pos[0] < tokens.length && tokens[pos[0]].equals("+")) {
            pos[0]++; // Consume '+'
            if (pos[0] >= tokens.length) throw new IllegalArgumentException("Dangling '+' operator");
            Expression right = parseTerm(tokens, pos);
            left = new Plus(left, right);
        }
        return left;
    }

    private static Expression parseTerm(String[] tokens, int[] pos) {
        Expression left = parsePrimary(tokens, pos);
        while (pos[0] < tokens.length && tokens[pos[0]].equals("*")) {
            pos[0]++; // Consume '*'
            if (pos[0] >= tokens.length) throw new IllegalArgumentException("Dangling '*' operator");
            Expression right = parsePrimary(tokens, pos);
            left = new Multiple(left, right);
        }
        return left;
    }

    private static Expression parsePrimary(String[] tokens, int[] pos) {
        if (pos[0] >= tokens.length) {
            throw new IllegalArgumentException("Missing operand at end of expression");
        }
        
        String token = tokens[pos[0]++];
        
        if (token.equals("(")) {
            Expression sub = parseExpression(tokens, pos);
            if (pos[0] >= tokens.length || !tokens[pos[0]].equals(")")) {
                throw new IllegalArgumentException("Mismatched or missing closing parenthesis");
            }
            pos[0]++; // Consume ')'
            return sub;
        } else if (token.matches("[a-zA-Z]+")) {
            return new Variable(token);
        } else if (token.matches("[0-9]*\\.?[0-9]+")) {
            return new Number(Double.parseDouble(token));
        } else {
            throw new IllegalArgumentException("Unsupported operator or illegal character: " + token);
        }
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
