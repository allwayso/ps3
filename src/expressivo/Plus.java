package expressivo;

/**
 * An immutable concrete variant of Expression representing addition.
 */
public class Plus implements Expression {
    private final Expression left;
    private final Expression right;

    // Abstraction function:
    //   AF(left, right) = left + right
    // Representation invariant:
    //   left != null, right != null
    // Safety from rep exposure:
    //   All fields are private and final. Since Expression is 
    //   defined as immutable, the internal sub-expressions cannot be modified.

    /**
     * Create an addition expression.
     * @param left left operand
     * @param right right operand
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
}