/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.property;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import zeenea.connector.property.PropertyDefinition;
import zeenea.connector.property.PropertyType;

class CustomPropertyTest {
  @Test
  @DisplayName("getCode() return definition code")
  void customPropertyCodeIsDefinitionCode() {
    var actual = CustomProperty.string("code", "attribute");
    assertEquals("code", actual.getCode());
  }

  @Test
  @DisplayName("getType() return definition type")
  void customPropertyTypeIsDefinitionType() {
    var actual = CustomProperty.string("code", "attribute");
    assertEquals(PropertyType.STRING, actual.getType());
  }

  @Test
  @DisplayName("getAttribute() return attribute name")
  void customPropertyAttributeIsAttributeName() {
    var actual = CustomProperty.string("code", "attribute");
    assertEquals("attribute", actual.getAttributeName());
  }

  @Test
  @DisplayName("getDefinition() return definition")
  void customDefinitionTypeIsDefinition() {
    var actual = CustomProperty.string("code", "attribute");
    assertEquals(PropertyDefinition.string("code"), actual.getDefinition());
  }
}
