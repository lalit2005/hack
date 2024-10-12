package object;

import object.interfaces.Expression;
import object.interfaces.Statement;

public class LetStatement implements Statement {
    public Identifier name;

    public Expression value;

    public void setName(Identifier name) {
        this.name = name;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    public LetStatement() {
    }

    public LetStatement(Identifier ident, Expression value) {
        this.name = ident;
        this.value = value;
    }

    public Identifier getName() {
        return name;
    }

    public Expression getValue() {
        return value;
    }

    public String toString() {
        return "let" + name.toString() + " = " + value.toString();
    }
}