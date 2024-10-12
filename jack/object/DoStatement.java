package object;

import object.interfaces.Statement;

public class DoStatement implements Statement {
    public SubroutineCall subroutineCall;

    public DoStatement() {
    }

    public DoStatement(SubroutineCall subroutineCall) {
        this.subroutineCall = subroutineCall;
    }

    public SubroutineCall getSubroutineCall() {
        return subroutineCall;
    }

    public void setSubroutineCall(SubroutineCall subroutineCall) {
        this.subroutineCall = subroutineCall;
    }

    public String toString() {
        return "do " + this.subroutineCall.toString();
    }
}
