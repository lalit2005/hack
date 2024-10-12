package object;

import java.beans.Expression;
import java.util.List;
import java.util.Optional;

// TODO: does this extends to expression or expression statemnt or is it standalone?
public class SubroutineCall {
    public String subroutineCallName;
    public List<Expression> expressionsList;
    public Identifier className;

    public String getSubRoutineCallName() {
        return subroutineCallName;
    }

    public List<Expression> getExpressionsList() {
        return expressionsList;
    }

    public Optional<Identifier> getClassName() {
        return Optional.ofNullable(className);
    }

    public SubroutineCall(String subroutineCall, List<Expression> expressionsList) {
        this.subroutineCallName = subroutineCall;
        this.expressionsList = expressionsList;
        this.className = null;
    }

    public SubroutineCall(String subroutineCall, List<Expression> expressionsList, Identifier className) {
        this.subroutineCallName = subroutineCall;
        this.expressionsList = expressionsList;
        this.className = className;
    }

    public boolean isMethodCall() {
        return this.className != null;
    }

    @Override
    public String toString() {
        if (this.isMethodCall()) {
            return className + "." + subroutineCallName + "(" + expressionsList + ")";
        } else {
            return subroutineCallName + "(" + expressionsList + ")";
        }
    }
}