package zeenea.connector.example.json;

import com.fasterxml.jackson.databind.JsonNode;

public interface Customizable {
  JsonNode getCustomProperty(String name);
}
