package zeenea.connector.example.log;

import java.util.Objects;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Contract;

public class Strings {
  private static final Pattern START_OF_LINE = Pattern.compile("^", Pattern.MULTILINE);

  /**
   * Indent a block of text.
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

  /**
   * Add the given prefix if the text isn't already prefixed.
   *
   * @param prefix The refix to add.
   * @param text Text to add prefix to.
   * @return The text with the prefix added if it was not already present.
   */
  @Contract(value = "null, _ -> fail; _, null -> null; !null, !null -> !null", pure = true)
  public static String ensurePrefix(String prefix, String text) {
    Objects.requireNonNull(prefix);
    if (text == null || prefix.isEmpty() || text.startsWith(prefix)) {
      return text;
    }
    return prefix + text;
  }

  @Contract(pure = true, value = "_, null -> fail; null, _ -> null; !null, !null -> !null")
  public static String chopSuffix(String string, String suffix) {
    Objects.requireNonNull(suffix);
    if (string == null || suffix.isEmpty() || !string.endsWith(suffix)) {
      return string;
    }
    return string.substring(0, string.length() - suffix.length());
  }
}
