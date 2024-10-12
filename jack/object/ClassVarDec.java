package object;

import java.util.List;

import lexer.Token;
import object.interfaces.Statement;

public class ClassVarDec implements Statement {
    public Token type; // type is int | char | boolean | className
    public Token staticOrField;
    public List<Identifier> varNames;

    public ClassVarDec(Token type, Token staticOrField, List<Identifier> varNames) {
        this.type = type;
        this.staticOrField = staticOrField;
        this.varNames = varNames;
    }

    public Token getType() {
        return this.type;
    }

    public Token getStaticOrField() {
        return this.staticOrField;
    }

    public List<Identifier> getVarNames() {
        return this.varNames;
    }
}