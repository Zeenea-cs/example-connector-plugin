package zeenea.connector.example.log;

import java.util.regex.Pattern;
import org.jetbrains.annotations.Contract;

public class Strings {
  private static final Pattern START_OF_LINE = Pattern.compile("^", Pattern.MULTILINE);

  /**
   * INdent a block of text.
   *
   * @param text The text to indent.
   * @param count The number of spaces to add.
   * @return The indented text.
   */
  @Contract(pure = true, value = "null, _ -> null; !null, _ -> !null")
  public static String indent(String text, int count) {
    if (text == null) return null;
    if (count > 0) {
      return START_OF_LINE.matcher(text).replaceAll(" ".repeat(count));
    }
    return text;
  }
}
