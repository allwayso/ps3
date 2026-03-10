package expressivo;

import java.util.Map;

/**
 * An immutable concrete variant of Expression representing a multiplication.
 */
public class Multiple implements Expression {
    private final Expression left;
    private final Expression right;

    // Abstraction function:
    //   AF(left, right) = the mathematical expression left * right
    // Representation invariant:
    //   left and right are non-null Expressions
    // Safety from rep exposure:
    //   left and right are final and are Expressions (which are immutable).

    /**
     * Create a multiplication expression.
     * @param left the left operand
     * @param right the right operand
     */
    public Multiple(Expression left, Expression right) {
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
        // Product Rule: d(u * v)/dx = (u * dv/dx) + (v * du/dx)
        Expression term1 = new Multiple(left, right.differentiate(variable));
        Expression term2 = new Multiple(right, left.differentiate(variable));
        return new Plus(term1, term2);
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
        Expression leftSimp = left.simplify(environment);
        Expression rightSimp = right.simplify(environment);

        // Constant folding using Number.value()
        if (leftSimp instanceof Number && rightSimp instanceof Number) {
            double result = ((Number) leftSimp).value() * ((Number) rightSimp).value();
            return new Number(result);
        }

        return new Multiple(leftSimp, rightSimp);
    }

    @Override
    public String toString() {
        // Use parentheses to strictly enforce structural meaning
        return "(" + left.toString() + " * " + right.toString() + ")";
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Multiple)) return false;
        Multiple that = (Multiple) thatObject;
        return this.left.equals(that.left) && this.right.equals(that.right);
    }

    @Override
    public int hashCode() {
        // Use a different prime or pattern than Plus to reduce collisions between 1+2 and 1*2
        return left.hashCode() * 17 + right.hashCode();
    }
}