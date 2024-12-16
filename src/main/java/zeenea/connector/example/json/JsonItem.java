/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.json;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class JsonItem implements Customizable {
  private String id;
  private String label;
  private String name;
  private String description;
  private List<JsonContact> contacts = List.of();
  private List<JsonItemRef> sources = List.of();
  private final Map<String, JsonNode> customProperty = new HashMap<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<JsonContact> getContacts() {
    return contacts;
  }

  public void setContacts(List<JsonContact> contacts) {
    this.contacts = Objects.requireNonNull(contacts);
  }

  public List<JsonItemRef> getSources() {
    return sources;
  }

  public void setSources(List<JsonItemRef> sources) {
    this.sources = Objects.requireNonNull(sources);
  }

  @JsonAnyGetter
  public Map<String, JsonNode> getCustomPropertyMap() {
    return customProperty;
  }

  public JsonNode getCustomProperty(String name) {
    return customProperty.get(name);
  }

  @JsonAnySetter
  public void setCustomProperty(String name, JsonNode value) {
    customProperty.put(name, value);
  }
}
