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

public class JsonField implements Customizable {
  private String name;
  private String description;
  private String nativeType;
  private String dataType;
  private boolean isNullable = false;
  private boolean isMultivalued = false;
  private List<JsonItemRef> sourceFields = List.of();

  private final Map<String, JsonNode> customProperty = new HashMap<>();

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

  public String getNativeType() {
    return nativeType;
  }

  public void setNativeType(String nativeType) {
    this.nativeType = nativeType;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public boolean isNullable() {
    return isNullable;
  }

  public void setNullable(boolean nullable) {
    isNullable = nullable;
  }

  public boolean isMultivalued() {
    return isMultivalued;
  }

  public void setMultivalued(boolean multivalued) {
    isMultivalued = multivalued;
  }

  public List<JsonItemRef> getSourceFields() {
    return sourceFields;
  }

  public void setSourceFields(List<JsonItemRef> sourceFields) {
    this.sourceFields = sourceFields;
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
