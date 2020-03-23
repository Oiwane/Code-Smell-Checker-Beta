package input;

import java.io.InputStream;
import java.util.Scanner;

public class Input{
  private InputStream inputStream = System.in;
  private static final Input input = new Input();

  public static Input getInstance() {
    return input;
  }

  public String inputStr() {
    Scanner scanner = new Scanner(inputStream);
    return scanner.next();
  }

  public int inputInt() {
    Scanner scanner = new Scanner(inputStream);
    return scanner.nextInt();
  }
}