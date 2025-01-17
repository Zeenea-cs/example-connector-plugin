/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.property;

import java.util.List;
import java.util.stream.Collectors;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.exception.InvalidConfigurationException;
import zeenea.connector.property.PropertyDefinition;

/** A list of custom properties. */
public class CustomProperties {
  private static final CustomProperties EMPTY = new CustomProperties(List.of());

  private final List<CustomProperty> properties;

  /**
   * Create a new {@code CustomProperties} from a custom property list.
   *
   * @param properties A list of custom properties.
   */
  public CustomProperties(List<CustomProperty> properties) {
    this.properties = List.copyOf(properties);
  }

  /**
   * Empty Custom properties.
   *
   * @return An empty instance.
   */
  public static CustomProperties empty() {
    return EMPTY;
  }

  /**
   * Parse the definition of a custom property list.
   *
   * <p>The text should contain a list of property definitions each in form:
   *
   * <pre>
   *         type <em>code</em> ( from <em>attributeName</em> ) ?
   *     </pre>
   *
   * <p>Where type is one of:
   *
   * <ul>
   *   <li>"string",
   *   <li>"long text" or just "text",
   *   <li>"tag",
   *   <li>"number",
   *   <li>"url",
   *   <li>"instant".
   * </ul>
   *
   * <p><em>code</em> and <em>name</em> value can be either quoted by single quote ("'") or raw
   * name. A raw name starts with a letter or the underscore ("_") character and is followed by
   * letters, digits, underscore ("_") or dash ("-").
   *
   * @param definitions The custom property list definition.
   * @return A new {@code CustomProperties}.
   */
  public static CustomProperties parse(String definitions) {
    if (definitions == null || definitions.isEmpty()) return EMPTY;
    try {
      var parser = new CustomPropertiesParser(definitions);
      return new CustomProperties(parser.propertyList());
    } catch (TokenMgrException | ParseException e) {
      throw new InvalidConfigurationException("Invalid custom properties: " + e.getMessage());
    }
  }

  /**
   * Parse the definition of custom properties from the configuration.
   *
   * @param configuration Connection configuration.
   * @param configurationField Property to read configuration from.
   * @return The custom property repository.
   */
  public static CustomProperties parse(
      ConnectionConfiguration configuration, String configurationField) {
    return parse(configuration.getStringOptional(configurationField).orElse(""));
  }

  public List<CustomProperty> getProperties() {
    return properties;
  }

  public List<PropertyDefinition> getDefinitions() {
    return properties.stream().map(CustomProperty::getDefinition).collect(Collectors.toList());
  }
}
