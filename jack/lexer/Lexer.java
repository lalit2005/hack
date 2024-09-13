package lexer;

import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;

public class Lexer {
  private PushbackReader reader;
  private ArrayList<Token> tokensList;
  private char currentChar;

  public Lexer(FileReader fileStream) {
    this.reader = new PushbackReader(fileStream);
    this.tokensList = new ArrayList<>();
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
          addToken(currentChar, TokenType.ELSE);
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
            addToken(s, TokenType.STRING_CONSTANT);
          }
          break;
      }
      readChar();
    }
    for (Token token : tokensList) {
      System.out.println(token.getValue() + " : " + token.getType());
    }
  }
}
