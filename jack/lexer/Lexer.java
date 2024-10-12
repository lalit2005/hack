package lexer;

import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
  private PushbackReader reader;
  private ArrayList<Token> tokensList;
  private char currentChar;
  private HashMap<String, TokenType> keywordMap;
  private int currentIndex;
  private Token curToken;

  public Token getCurToken() {
    return curToken;
  }

  public Lexer(FileReader fileStream) {
    this.reader = new PushbackReader(fileStream);
    this.tokensList = new ArrayList<>();
    this.keywordMap = new HashMap<>();
    this.currentIndex = -1; // -1 so that from the first nextToken call, this can be incremented
    initializeKeywordMap();
  }

  private void initializeKeywordMap() {
    keywordMap.put("class", TokenType.CLASS);
    keywordMap.put("constructor", TokenType.CONSTRUCTOR);
    keywordMap.put("function", TokenType.FUNCTION);
    keywordMap.put("static", TokenType.STATIC);
    keywordMap.put("var", TokenType.VAR);
    keywordMap.put("int", TokenType.INT);
    keywordMap.put("char", TokenType.CHAR);
    keywordMap.put("boolean", TokenType.BOOLEAN);
    keywordMap.put("void", TokenType.VOID);
    keywordMap.put("true", TokenType.TRUE);
    keywordMap.put("false", TokenType.FALSE);
    keywordMap.put("null", TokenType.NULL);
    keywordMap.put("this", TokenType.THIS);
    keywordMap.put("let", TokenType.LET);
    keywordMap.put("do", TokenType.DO);
    keywordMap.put("if", TokenType.IF);
    keywordMap.put("else", TokenType.ELSE);
    keywordMap.put("while", TokenType.WHILE);
    keywordMap.put("return", TokenType.RETURN);
  }

  private char readChar() {
    try {
      int ch = reader.read();
      if (ch == -1) {
        currentChar = '\0';
      } else {
        currentChar = (char) ch;
      }
      return currentChar;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return 0;
  }

  private char peekChar() {
    char c = readChar();
    try {
      reader.unread(c);
    } catch (Exception e) {
      throw new java.lang.RuntimeException("Unable to unread char inside peekChar()");
    }
    return c;
  }

  private void addToken(char value, TokenType tokenType) {
    Token t = new Token(String.valueOf(value), tokenType);
    tokensList.add(t);
  }

  private void addToken(String value, TokenType tokenType) {
    Token t = new Token(value, tokenType);
    tokensList.add(t);
  }

  public void nextToken() {
    this.curToken = tokensList.get(++currentIndex);
  }

  public Token peekToken() {
    return this.tokensList.get(currentIndex + 1);
  }

  public void tokenize() {
    readChar(); // initialize cursor properly for readChar

    while (Character.isWhitespace((currentChar))) {
      while (currentChar == ' ') {
        readChar();
      }
    }

    while (currentChar != '\0') {
      switch (currentChar) {
        case '{':
          addToken(currentChar, TokenType.L_BRACE);
          break;
        case '}':
          addToken(currentChar, TokenType.R_PAREN);
          break;
        case '(':
          addToken(currentChar, TokenType.L_PAREN);
          break;
        case ')':
          addToken(currentChar, TokenType.R_PAREN);
          break;
        case '[':
          addToken(currentChar, TokenType.L_SQUARE);
          break;
        case ']':
          addToken(currentChar, TokenType.R_SQUARE);
          break;
        case '.':
          addToken(currentChar, TokenType.DOT);
          break;
        case ',':
          addToken(currentChar, TokenType.COMMA);
          break;
        case ';':
          addToken(currentChar, TokenType.SEMICOLON);
          break;
        case '+':
          addToken(currentChar, TokenType.PLUS);
          break;
        case '-':
          addToken(currentChar, TokenType.MINUS);
          break;
        case '*':
          addToken(currentChar, TokenType.ASTERISK);
          break;
        case '/':
          addToken(currentChar, TokenType.SLASH);
          break;
        case '&':
          addToken(currentChar, TokenType.AND);
          break;
        case '|':
          addToken(currentChar, TokenType.OR);
          break;
        case '<':
          addToken(currentChar, TokenType.LT);
          break;
        case '>':
          addToken(currentChar, TokenType.GT);
          break;
        case '=':
          addToken(currentChar, TokenType.EQUALS);
          break;
        case '~':
          addToken(currentChar, TokenType.TILDE);
          break;
        default:
          if (Character.isDigit(currentChar)) {
            String s = new String();
            s += currentChar;
            while (Character.isDigit(peekChar())) {
              s += currentChar;
              readChar();
            }
            addToken(s, TokenType.INTEGER_CONSTANT);
          }
          if (Character.isAlphabetic(currentChar) || currentChar == '_') {
            String s = new String();
            s += currentChar;
            while (Character.isAlphabetic(peekChar()) || currentChar == '_') {
              s += currentChar;
              readChar();
            }
            if (keywordMap.containsKey(s)) {
              addToken(s, keywordMap.get(s));
            } else {
              addToken(s, TokenType.IDENTIFIER);
            }
          }
          break;
      }
      readChar();
    }
    addToken("", TokenType.EOF);
  }
}
