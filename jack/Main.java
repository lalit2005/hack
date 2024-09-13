import lexer.Lexer;
import java.io.FileReader;
import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    if (args.length != 1) {
      throw new java.lang.RuntimeException("Invalid arguments provided");
    }
    String filepath = args[0];
    try (FileReader reader = new FileReader(filepath)) {
      Lexer l = new Lexer(reader);
      l.tokenize();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
