package zeenea.connector.example.property;

import static java.util.Objects.requireNonNull;

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

/**
 * Define a custom property.
 *
 * <p>It consists of a Zeenea {@code PropertyDefinition} and an attribute name.
 */
public final class CustomProperty {
  private final PropertyDefinition definition;
  private final String attributeName;

  private CustomProperty(PropertyDefinition definition, String attributeName) {
    this.definition = definition;
    this.attributeName = attributeName;
  }

  /**
   * Create a new custom property.
   *
   * @param type Property type.
   * @param code Property code.
   * @param attributeName JSON attribute name.
   * @return The new custom property.
   */
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

  /**
   * Create a new string custom property.
   *
   * @param code Property code.
   * @param attributeName JSON attribute name.
   * @return The new custom property.
   */
  public static CustomProperty string(String code, String attributeName) {
    return new CustomProperty(
        new StringPropertyDefinition(requireNonNull(code)), requireNonNull(attributeName));
  }

  /**
   * Create a new long text custom property.
   *
   * @param code Property code.
   * @param attributeName JSON attribute name.
   * @return The new custom property.
   */
  public static CustomProperty longText(String code, String attributeName) {
    return new CustomProperty(
        new LongTextPropertyDefinition(requireNonNull(code)), requireNonNull(attributeName));
  }

  /**
   * Create a new instant custom property.
   *
   * @param code Property code.
   * @param attributeName JSON attribute name.
   * @return The new custom property.
   */
  public static CustomProperty instant(String code, String attributeName) {
    return new CustomProperty(
        new InstantPropertyDefinition(requireNonNull(code)), requireNonNull(attributeName));
  }

  /**
   * Create a new number custom property.
   *
   * @param code Property code.
   * @param attributeName JSON attribute name.
   * @return The new custom property.
   */
  public static CustomProperty number(String code, String attributeName) {
    return new CustomProperty(
        new NumberPropertyDefinition(requireNonNull(code)), requireNonNull(attributeName));
  }

  /**
   * Create a new tag custom property.
   *
   * @param code Property code.
   * @param attributeName JSON attribute name.
   * @return The new custom property.
   */
  public static CustomProperty tag(String code, String attributeName) {
    return new CustomProperty(
        new TagPropertyDefinition(requireNonNull(code)), requireNonNull(attributeName));
  }

  /**
   * Create a new url custom property.
   *
   * @param code Property code.
   * @param attributeName JSON attribute name.
   * @return The new custom property.
   */
  public static CustomProperty url(String code, String attributeName) {
    return new CustomProperty(
        new UrlPropertyDefinition(requireNonNull(code)), requireNonNull(attributeName));
  }

  /**
   * @return The property definition code.
   */
  public String getCode() {
    return definition.getCode();
  }

  /**
   * @return The property definition type.
   */
  public PropertyType getType() {
    return definition.getType();
  }

  /**
   * @return The property definition.
   */
  public PropertyDefinition getDefinition() {
    return definition;
  }

  /**
   * @return The property attribute name.
   */
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
