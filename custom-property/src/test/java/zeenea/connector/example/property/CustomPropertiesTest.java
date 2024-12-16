/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.property;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomPropertiesTest {
  @Test
  @DisplayName("parse(\"\") should return an empty list")
  void parseEmptyShouldReturnAnEmptyList() {
    CustomProperties actual = CustomProperties.parse("");
    assertThat(actual.getProperties()).isEmpty();
  }

  @Test
  @DisplayName("parse() should return a single string property without code")
  void parseSingleStringPropertyWithoutCode() {
    CustomProperties actual = CustomProperties.parse("string my_property");
    assertThat(actual.getProperties())
        .containsExactly(CustomProperty.string("my_property", "my_property"));
  }

  @Test
  @DisplayName("parse() should return a single string property without code ; quoted variant")
  void parseSingleStringPropertyWithoutCodeQuoted() {
    CustomProperties actual = CustomProperties.parse("string 'my property'");
    assertThat(actual.getProperties())
        .containsExactly(CustomProperty.string("my property", "my property"));
  }

  @Test
  @DisplayName("parse() should return a single string property with code")
  void parseSingleStringPropertyWithCode() {
    CustomProperties actual = CustomProperties.parse("STRING my-property FROM my_custom_property");
    assertThat(actual.getProperties())
        .containsExactly(CustomProperty.string("my-property", "my_custom_property"));
  }

  @Test
  @DisplayName("parse() should return a single string property with code ; quoted variant")
  void parseSingleStringPropertyWithCodeQuoted() {
    CustomProperties actual =
        CustomProperties.parse("STRING 'my property' FROM 'my custom property'");
    assertThat(actual.getProperties())
        .containsExactly(CustomProperty.string("my property", "my custom property"));
  }

  @Test
  @DisplayName("parse() should read a list of properties")
  void parseListOfProperties() {
    CustomProperties actual =
        CustomProperties.parse(
            "STRING 'my property' FROM 'my custom property'\n"
                + "tag tag_property\n"
                + "long text 'long text_property' from 'long text prop'\n"
                + "text 'text/property' from txt_property\n"
                + "number count\n"
                + "url wikipeadia_link\n"
                + "instant 'origin date' from creation_date\n");
    assertThat(actual.getProperties())
        .containsExactly(
            CustomProperty.string("my property", "my custom property"),
            CustomProperty.tag("tag_property", "tag_property"),
            CustomProperty.longText("long text_property", "long text prop"),
            CustomProperty.longText("text/property", "txt_property"),
            CustomProperty.number("count", "count"),
            CustomProperty.url("wikipeadia_link", "wikipeadia_link"),
            CustomProperty.instant("origin date", "creation_date"));
  }
}
