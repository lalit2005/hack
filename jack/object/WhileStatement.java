package object;

import object.interfaces.Expression;
import object.interfaces.Statement;

public class WhileStatement implements Statement {
    public Expression condition;

    public BlockStatement body;

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public void setBody(BlockStatement body) {
        this.body = body;
    }

    public WhileStatement() {
    }

    public WhileStatement(Expression condition, BlockStatement body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return this.condition;
    }

    public BlockStatement getBody() {
        return this.body;
    }

    @Override
    public String toString() {
        return "while" + "(" + this.condition.toString() + ")" + "{\n" + this.body.toString() + "\n}";
    }
}
