package object;

import object.interfaces.Expression;
import object.interfaces.Statement;

public class IfStatement implements Statement {
    public Expression condition;
    public BlockStatement ifConditionBody;
    public BlockStatement elseConditionBody;

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public void setIfConditionBody(BlockStatement ifCondition) {
        this.ifConditionBody = ifCondition;
    }

    public void setElseConditionBody(BlockStatement elseCondition) {
        this.elseConditionBody = elseCondition;
    }

    public IfStatement() {
    };

    public IfStatement(Expression condition, BlockStatement ifCondition) {
        this.condition = condition;
        this.ifConditionBody = ifCondition;
    }

    public IfStatement(Expression condition, BlockStatement ifCondition, BlockStatement elseCondition) {
        this.condition = condition;
        this.ifConditionBody = ifCondition;
        this.elseConditionBody = elseCondition;
    }

    public Expression getCondition() {
        return condition;
    }

    public BlockStatement getIfConditionBody() {
        return ifConditionBody;
    }

    public BlockStatement getElseConditionBody() {
        return elseConditionBody;
    }

}