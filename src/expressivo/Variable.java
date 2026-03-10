package expressivo;

import java.util.Map;

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

    @Override
    public Expression differentiate(String variable) {
        // d(x)/dx = 1, d(y)/dx = 0
        if (this.name.equals(variable)) {
            return new Number(1);
        } else {
            return new Number(0);
        }
    }

    @Override
    public Expression simplify(Map<String, Double> environment) {
        // If the variable is in the environment, replace it with its value
        if (environment.containsKey(name)) {
            return new Number(environment.get(name));
        }
        // Otherwise, return this variable as is
        return this;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Variable)) return false;
        Variable that = (Variable) thatObject;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}