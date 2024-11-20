package zeenea.connector.example.json;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class JsonItem {
  private String id;
  private String name;
  private String description;
  private List<JsonContact> contacts = List.of();
  private Map<String, JsonNode> customFields = new HashMap<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
    this.contacts = contacts;
  }

  @JsonAnyGetter
  public Map<String, JsonNode> customFields() {
    return customFields;
  }

  public JsonNode getCustomField(String name) {
    return customFields.get(name);
  }

  @JsonAnySetter
  public void setCustomField(String name, JsonNode value) {
    customFields.put(name, value);
  }
}
