package parser;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import object.BlockStatement;
import object.DoStatement;
import object.Identifier;
import object.IfStatement;
import object.LetStatement;
import object.WhileStatement;
import object.interfaces.Expression;
import object.interfaces.Statement;

public class Parser {
    private Lexer l;

    public Parser(Lexer l) {
        l.tokenize();
        l.nextToken(); // initialize to set current token to the actual first token
        this.l = l;
    }

    public void parse() {
        while (l.peekToken().getType() != TokenType.EOF) {
            switch (l.getCurToken().getType()) {
                case LET:
                case IF:
                parseStatement()
                    break;
                default:
                    break;
            }

            // current token: semicolon
            // nextToken to start parsing the next statement from first token
            l.nextToken();
        }
    }

    private Statement parseStatement() {
        // current token is first token of the statement
        Statement s;
        switch (l.getCurToken().getType()) {
            case LET:
                s = parseLetStmt();
                break;
            case IF:
                s = parseIfStmt();
                break;
            case WHILE:
                s = parseWhileStmt();
                break;
            case DO:
                s = parseDoStmt();
            default:
                break;
        }

        return s;
    }

    private Statement parseDoStmt() {
        DoStatement s = new DoStatement();
        // current token: do
        compareCurToken(TokenType.DO);
        l.nextToken();
        return s;
    }

    // TODO: finish building the object
    private Statement parseWhileStmt() {
        WhileStatement s = new WhileStatement();
        compareCurToken(TokenType.WHILE);
        l.nextToken();
        compareCurToken(TokenType.L_PAREN);
        s.setCondition(parseExpression());
        l.nextToken();
        compareCurToken(TokenType.R_PAREN);
        l.nextToken();
        compareCurToken(TokenType.L_BRACE);
        s.setBody(parseBlockStatement());
        l.nextToken();
        compareCurToken(TokenType.R_BRACE);
        l.nextToken();
        if (l.getCurToken().getType() == TokenType.SEMICOLON) {
            l.nextToken();
        }
        compareCurToken(TokenType.SEMICOLON);
        return s;
    }

    private Statement parseIfStmt() {
        IfStatement s = new IfStatement();
        compareCurToken(TokenType.IF);
        l.nextToken();
        compareCurToken(TokenType.L_PAREN);
        l.nextToken();
        Expression condition = parseExpression();
        s.setCondition(condition);
        l.nextToken();
        compareCurToken(TokenType.R_PAREN);
        l.nextToken();
        compareCurToken(TokenType.L_BRACE);
        BlockStatement body = parseBlockStatement();
        s.setIfConditionBody(body);
        l.nextToken();
        compareCurToken(TokenType.R_BRACE);
        if (l.getCurToken().getType() == TokenType.SEMICOLON) {
            l.nextToken();
        }
        return s;
    }

    // current token when this method is called is LET
    // after parsing, the final current token should be the semicolon
    private LetStatement parseLetStmt() {
        LetStatement s = new LetStatement();
        compareCurToken(TokenType.LET);
        l.nextToken();
        // compareCurToken(new TokenType[] { TokenType.IDENTIFIER, TokenType.STATIC });
        compareCurToken(TokenType.IDENTIFIER);
        Identifier var = new Identifier(l.getCurToken(), l.getCurToken().getValue())
        s.setName(var);
        l.nextToken();
        compareCurToken(TokenType.EQUALS);
        l.nextToken();
        Expression exp = parseExpression();
        s.setValue(exp);
        l.nextToken();
        compareCurToken(TokenType.SEMICOLON);
        return s;
    }

    private BlockStatement parseBlockStatement() {
        // current token is the first token of the first statement
        BlockStatement b = new BlockStatement();
        while (l.peekToken().getType() != TokenType.R_BRACE) {
            // current token is the semicolon of previous statement
            // if it's the last statement, it would have exited the loop in previous
            // iteration
            l.nextToken();
            b.addStatement(parseStatement());
        }
        // current token is the semicolon of the last statement of the block
        return b;
    }

    // current token when this method is called is the first token of the expression
    // after parsing, the final current token should be the last token of the
    // expression
    private Expression parseExpression() {
        return new Identifier(null, null);
    }

    private void compareCurToken(TokenType t[]) {
        for (TokenType tokenType : t) {
            if (tokenType.equals(l.getCurToken().getType())) {
                return;
            }
        }
        throwError(t.toString(), l.getCurToken().getType().toString());
    }

    private void compareCurToken(TokenType t) {
        if (!t.equals(l.getCurToken().getType())) {
            throwError(l.getCurToken().getType().toString(), t.toString());
        }
    }

    private void throwError(String got, String wanted) {
        System.err.println("Error: wanted " + wanted + " but got " + got + " instead");
    }
}

// normal: 0.6708984375 ms
// new: 0.671875 ms