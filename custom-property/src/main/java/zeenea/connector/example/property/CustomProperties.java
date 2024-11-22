package zeenea.connector.example.property;

import java.util.List;
import java.util.stream.Collectors;
import zeenea.connector.exception.InvalidConfigurationException;
import zeenea.connector.property.PropertyDefinition;

public class CustomProperties {
  private final List<CustomProperty> properties;

  public CustomProperties(List<CustomProperty> properties) {
    this.properties = List.copyOf(properties);
  }

  public static CustomProperties parse(String definitions) {
    if (definitions == null || definitions.isEmpty()) return new CustomProperties(List.of());
    try {
      var parser = new CustomPropertiesParser(definitions);
      return new CustomProperties(parser.propertyList());
    } catch (TokenMgrException | ParseException e) {
      throw new InvalidConfigurationException("Invalid custom properties: " + e.getMessage());
    }
  }

  public List<CustomProperty> getProperties() {
    return properties;
  }

  public List<PropertyDefinition> getDefinitions() {
    return properties.stream().map(CustomProperty::getDefinition).collect(Collectors.toList());
  }
}
