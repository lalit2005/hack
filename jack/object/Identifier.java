package object;

import lexer.Token;
import object.interfaces.Expression;

public class Identifier implements Expression {
    public Token token; // the token.IDENT token
    public String value;

    public Identifier(Token token, String value) {
        this.token = token;
        this.value = value;
    }

    public Token getToken() {
        return token;
    }

    public String getValue() {
        return value;
    }

    public String tokenLiteral() {
        return this.token.getType().toString();
    }

    public String toString() {
        return this.value;
    }
}
