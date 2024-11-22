package zeenea.connector.example.property;

import java.util.Objects;
import java.util.StringJoiner;
import zeenea.connector.property.InstantPropertyDefinition;
import zeenea.connector.property.LongTextPropertyDefinition;
import zeenea.connector.property.NumberPropertyDefinition;
import zeenea.connector.property.PropertyDefinition;
import zeenea.connector.property.PropertyType;
import zeenea.connector.property.StringPropertyDefinition;
import zeenea.connector.property.TagPropertyDefinition;
import zeenea.connector.property.UrlPropertyDefinition;

public final class CustomProperty {
  private final PropertyDefinition definition;
  private final String attributeName;

  public CustomProperty(PropertyDefinition definition, String attributeName) {
    this.definition = definition;
    this.attributeName = attributeName;
  }

  public static CustomProperty of(PropertyType type, String code, String attributeName) {
    switch (type) {
      case STRING:
        return string(code, attributeName);
      case TAG:
        return tag(code, attributeName);
      case LONG_TEXT:
        return longText(code, attributeName);
      case NUMBER:
        return number(code, attributeName);
      case INSTANT:
        return instant(code, attributeName);
      case URL:
        return url(code, attributeName);
      default:
        throw new IllegalArgumentException("Unknown property type: " + type);
    }
  }

  public static CustomProperty string(String code, String attributeName) {
    return new CustomProperty(new StringPropertyDefinition(code), attributeName);
  }

  public static CustomProperty longText(String code, String attributeName) {
    return new CustomProperty(new LongTextPropertyDefinition(code), attributeName);
  }

  public static CustomProperty instant(String code, String attributeName) {
    return new CustomProperty(new InstantPropertyDefinition(code), attributeName);
  }

  public static CustomProperty number(String code, String attributeName) {
    return new CustomProperty(new NumberPropertyDefinition(code), attributeName);
  }

  public static CustomProperty tag(String code, String attributeName) {
    return new CustomProperty(new TagPropertyDefinition(code), attributeName);
  }

  public static CustomProperty url(String code, String attributeName) {
    return new CustomProperty(new UrlPropertyDefinition(code), attributeName);
  }

  public String getCode() {
    return definition.getCode();
  }

  public PropertyType getType() {
    return definition.getType();
  }

  public PropertyDefinition getDefinition() {
    return definition;
  }

  public String getAttributeName() {
    return attributeName;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof CustomProperty)) return false;
    CustomProperty property = (CustomProperty) o;
    return Objects.equals(definition, property.definition)
        && Objects.equals(attributeName, property.attributeName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(definition, attributeName);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CustomProperty.class.getSimpleName() + "[", "]")
        .add("definition=" + definition)
        .add("attributeName='" + attributeName + "'")
        .toString();
  }
}
