/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import expressivo.parser.ExpressionLexer;
import expressivo.parser.ExpressionParser;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RecognitionException;
import java.util.*;


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
    public static Expression parse(String string) {
        // 1. 创建输入流
        CharStream stream = new ANTLRInputStream(string);

        // 2. 配置 Lexer 并添加错误监听器
        ExpressionLexer lexer = new ExpressionLexer(stream);
        lexer.removeErrorListeners(); // 移除默认的控制台打印
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
                                    int line, int charPositionInLine, String msg, RecognitionException e) {
                // 捕获非法字符（如 ^）
                throw new IllegalArgumentException("Invalid syntax at line " + line + ":" + charPositionInLine + " - " + msg);
            }
        });
        
        TokenStream tokens = new CommonTokenStream(lexer);
        
        // 3. 配置 Parser 并添加错误监听器
        ExpressionParser parser = new ExpressionParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
                                    int line, int charPositionInLine, String msg, RecognitionException e) {
                // 捕获语法错误（如括号不匹配、操作符悬挂）
                throw new IllegalArgumentException("Parser error at line " + line + ":" + charPositionInLine + " - " + msg);
            }
        });

        // 4. 解析起始规则
        ParseTree tree = parser.root();

        // 5. 遍历并构建 AST
        ParseTreeWalker walker = new ParseTreeWalker();
        MakeExpression exprMaker = new MakeExpression();
        walker.walk(exprMaker, tree);
        
        return exprMaker.getExpression();
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
