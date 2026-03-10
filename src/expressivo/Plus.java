package expressivo;

import java.util.Map;

/**
 * An immutable concrete variant of Expression representing an addition.
 */
public class Plus implements Expression {
    private final Expression left;
    private final Expression right;

    // Abstraction function:
    //   AF(left, right) = the mathematical expression left + right
    // Representation invariant:
    //   left and right are non-null Expressions
    // Safety from rep exposure:
    //   left and right are final and are Expressions (which are immutable).

    /**
     * Create an addition expression.
     * @param left the left operand
     * @param right the right operand
     */
    public Plus(Expression left, Expression right) {
        this.left = left;
        this.right = right;
        checkRep();
    }

    private void checkRep() {
        assert left != null;
        assert right != null;
    }

    @Override
    public Expression differentiate(String variable) {
        // Sum Rule: d(u + v)/dx = du/dx + dv/dx
        return new Plus(left.differentiate(variable), right.differentiate(variable));
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
        // Recursively simplify left and right children
        Expression leftSimp = left.simplify(environment);
        Expression rightSimp = right.simplify(environment);

        // Constant folding: if both children are Numbers, sum them into a single Number
        if (leftSimp instanceof Number && rightSimp instanceof Number) {
            return new Number(((Number)leftSimp).value() + ((Number)rightSimp).value());
        }
        
        return new Plus(leftSimp, rightSimp);
    }

    @Override
    public String toString() {
        // Using parentheses to ensure order of operations is preserved in the string
        return "(" + left.toString() + " + " + right.toString() + ")";
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Plus)) return false;
        Plus that = (Plus) thatObject;
        return this.left.equals(that.left) && this.right.equals(that.right);
    }

    @Override
    public int hashCode() {
        // Standard recipe for combining hash codes of recursive fields
        return left.hashCode() + 31 * right.hashCode();
    }
}