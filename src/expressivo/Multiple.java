package expressivo;

/**
 * An immutable concrete variant of Expression representing multiplication.
 */
public class Multiple implements Expression {
    private final Expression left;
    private final Expression right;

    // Abstraction function:
    //   AF(left, right) = left * right
    // Representation invariant:
    //   left != null, right != null
    // Safety from rep exposure:
    //   All fields are private and final. Internal Expression objects
    //   are immutable, ensuring the tree structure cannot be altered.

    /**
     * Create a multiplication expression.
     * @param left left operand
     * @param right right operand
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
}