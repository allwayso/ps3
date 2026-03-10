package expressivo;

import java.util.Map;

/**
 * An immutable concrete variant of Expression representing a nonnegative constant.
 */
class Number implements Expression {
    private final double n;

    // Abstraction function:
    //   AF(n) = the constant value n
    // Representation invariant:
    //   n >= 0
    // Safety from rep exposure:
    //   n is a primitive double and final, so it's immutable and cannot be modified.
    
    /**
     * Create a number expression.
     * @param n the value of the number, must be non-negative.
     */
    public Number(double n) {
        this.n = n;
        checkRep();
    }

    private void checkRep() {
        assert n >= 0;
    }

    @Override
    public Expression differentiate(String variable) {
        return new Number(0);
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(n);
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Number)) return false;
        Number that = (Number) thatObject;
        return Double.compare(this.n, that.n) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(n);
    }
    
    public double value() { return n; }
}