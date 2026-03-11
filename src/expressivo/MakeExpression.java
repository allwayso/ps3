package expressivo;
import java.util.Stack;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import expressivo.parser.*;

/**
 * Constructs an Expression AST from an ANTLR parse tree.
 */
class MakeExpression implements ExpressionListener {
    private Stack<Expression> stack = new Stack<>();

    public Expression getExpression() {
        return stack.get(0);
    }

    @Override 
    public void exitSum(ExpressionParser.SumContext ctx) {
        // sum : product ('+' product)*
        List<ExpressionParser.ProductContext> terms = ctx.product();
        Expression sum = stack.pop();
        
        // Build Plus objects by popping from stack
        for (int i = 1; i < terms.size(); i++) {
            sum = new Plus(stack.pop(), sum);
        }
        stack.push(sum);
    }

    @Override 
    public void exitProduct(ExpressionParser.ProductContext ctx) {
        // product : primary ('*' primary)*
        List<ExpressionParser.PrimaryContext> factors = ctx.primary();
        Expression product = stack.pop();
        
        // Build Multiple objects by popping from stack
        for (int i = 1; i < factors.size(); i++) {
            product = new Multiple(stack.pop(), product);
        }
        stack.push(product);
    }

    @Override 
    public void exitPrimary(ExpressionParser.PrimaryContext ctx) {
        // primary : NUMBER | VARIABLE | '(' sum ')'
        if (ctx.NUMBER() != null) {
            stack.push(new Number(Double.parseDouble(ctx.NUMBER().getText())));
        } else if (ctx.VARIABLE() != null) {
            stack.push(new Variable(ctx.VARIABLE().getText()));
        }
        // In the '(' sum ')' case, the sum is already pushed by exitSum
    }

    @Override public void exitRoot(ExpressionParser.RootContext ctx) { }

    // Required empty implementations
    @Override public void enterRoot(ExpressionParser.RootContext ctx) { }
    @Override public void enterSum(ExpressionParser.SumContext ctx) { }
    @Override public void enterProduct(ExpressionParser.ProductContext ctx) { }
    @Override public void enterPrimary(ExpressionParser.PrimaryContext ctx) { }
    @Override public void visitTerminal(TerminalNode node) { }
    @Override public void enterEveryRule(ParserRuleContext ctx) { }
    @Override public void exitEveryRule(ParserRuleContext ctx) { }
    @Override public void visitErrorNode(ErrorNode node) { }
}