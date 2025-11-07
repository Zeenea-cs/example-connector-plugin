package zeenea.connector.example;

import java.util.Set;
import zeenea.connector.property.*;

public class ExampleMetadata {

  public static final StringPropertyDefinition DATABASE = new StringPropertyDefinition("Database");
  public static final StringPropertyDefinition FORMULA = new StringPropertyDefinition("Formula");

  // Other examples
  public static final InstantPropertyDefinition INSTANT = new InstantPropertyDefinition("instant");
  public static final LongTextPropertyDefinition LONGTEXT =
      new LongTextPropertyDefinition("longText");
  public static final NumberPropertyDefinition NUMBER = new NumberPropertyDefinition("number");
  public static final StringPropertyDefinition STRING = new StringPropertyDefinition("string");
  public static final TagPropertyDefinition TAG = new TagPropertyDefinition("tag");
  public static final UrlPropertyDefinition URL = new UrlPropertyDefinition("url");

  public static Set<PropertyDefinition> getPropertyDefinitions() {
    return Set.of(DATABASE, FORMULA, INSTANT, LONGTEXT, NUMBER, STRING, TAG, URL);
  }
}
