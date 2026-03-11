/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import java.util.Map;

/**
 * String-based commands provided by the expression system.
 * 
 * <p>PS3 instructions: this is a required class.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You MUST NOT add fields, constructors, or instance methods.
 * You may, however, add additional static methods, or strengthen the specs of existing methods.
 */
public class Commands {
    
    /**
     * Differentiate an expression with respect to a variable.
     * @param expression the expression to differentiate
     * @param variable the variable to differentiate by, a case-sensitive nonempty string of letters.
     * @return expression's derivative with respect to variable.  Must be a valid expression equal
     *         to the derivative, but doesn't need to be in simplest or canonical form.
     * @throws IllegalArgumentException if the expression or variable is invalid
     */
    public static String differentiate(String expression, String variable) {
        // Validate that the variable consists only of letters and is not empty
        if (!variable.matches("[a-zA-Z]+")) {
            throw new IllegalArgumentException("Variable must be a nonempty string of letters");
        }
        
        // Parse the string expression into an Expression object
        Expression expr = Expression.parse(expression);
        
        // Delegate the differentiation logic to the Expression object
        Expression derivative = expr.differentiate(variable);
        
        // Convert the resulting Expression AST back to a string
        return derivative.toString();
    }
    
    /**
     * Simplify an expression.
     * @param expression the expression to simplify
     * @param environment maps variables to values.  Variables are required to be case-sensitive nonempty 
     *         strings of letters.  The set of variables in environment is allowed to be different than the 
     *         set of variables actually found in expression.  Values must be nonnegative numbers.
     * @return an expression equal to the input, but after substituting every variable v that appears in both
     *         the expression and the environment with its value, environment.get(v).  If there are no
     *         variables left in this expression after substitution, it must be evaluated to a single number.
     *         Additional simplifications to the expression may be done at the implementor's discretion.
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static String simplify(String expression, Map<String,Double> environment) {
        // Parse the string expression into an Expression object
        Expression expr = Expression.parse(expression);
        
        // Delegate the simplification logic to the Expression object
        // The implementation in the variants (Plus, Multiply, etc.) handles the recursion
        Expression simplified = expr.simplify(environment);
        
        // Convert the simplified AST back to a string.
        // If the result is a single number, Number.toString() will handle the formatting.
        return simplified.toString();
    }
    
}
