package lexer;

public class Token {
    private String value;
    private TokenType type;

    public Token(String value, TokenType tokenType) {
        this.value = value;
        this.type = tokenType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

}