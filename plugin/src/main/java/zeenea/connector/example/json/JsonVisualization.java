package zeenea.connector.example.json;

import java.util.List;

public final class JsonVisualization extends JsonItem implements WithFields {
  private List<JsonField> fields = List.of();

  public List<JsonField> getFields() {
    return fields;
  }

  public void setFields(List<JsonField> fields) {
    this.fields = fields;
  }
}
