package object;

import java.util.List;

import lexer.Token;
import object.interfaces.Statement;

public class SubroutineDec implements Statement {
    public Token constrOrFuncOrMethod; // constructor or function or a method
    public Token returnType; // void or a className
    public Identifier subroutineName;
    public List<Identifier> parametersList;

    public SubroutineDec(Token constrOrFuncOrMethod, Identifier subroutineName,
            List<Identifier> parametersList) {
        this.constrOrFuncOrMethod = constrOrFuncOrMethod;
        this.subroutineName = subroutineName;
        this.parametersList = parametersList;
    }

    public Token getConstrOrFuncOrMethod() {
        return this.constrOrFuncOrMethod;
    }

    public Token getReturnType() {
        return this.returnType;
    }

    public Identifier getSubroutineName() {
        return this.subroutineName;
    }

    public List<Identifier> getParametersList() {
        return this.parametersList;
    }
}