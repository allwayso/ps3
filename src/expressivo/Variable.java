package expressivo;

/**
 * An immutable concrete variant of Expression representing a variable.
 */
public class Variable implements Expression {
    private final String name;

    // Abstraction function:
    //   AF(name) = a mathematical variable whose name is the string 'name'
    // Representation invariant:
    //   name is a non-empty string of letters ([a-zA-Z]+)
    // Safety from rep exposure:
    //   name is a final String, which is immutable in Java.

    /**
     * Create a variable expression.
     * @param name sequence of letters, case-sensitive, non-empty.
     */
    public Variable(String name) {
        this.name = name;
        checkRep();
    }

    private void checkRep() {
        assert name != null && name.matches("[a-zA-Z]+");
    }
}
