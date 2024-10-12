package object;

import object.interfaces.Expression;
import object.interfaces.Statement;

public class ReturnStatement implements Statement {
    public Expression returnValue;

    public ReturnStatement(Expression returnValue) {
        this.returnValue = returnValue;
    }

    public Expression getReturnValue() {
        return returnValue;
    }

    public String toString() {
        StringBuilder out = new StringBuilder();

        out.append("return" + " ");
        if (this.returnValue != null) {
            out.append(this.returnValue.toString());
        }
        out.append(";");

        return out.toString();
    }
}