package zeenea.connector.example.json;

import java.util.List;
import java.util.Objects;

public final class JsonVisualization extends JsonItem {
  private List<JsonField> fields = List.of();

  public List<JsonField> getFields() {
    return fields;
  }

  public void setFields(List<JsonField> fields) {
    this.fields = Objects.requireNonNull(fields);
  }
}
