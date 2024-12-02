package zeenea.connector.example.json;

import java.util.List;
import java.util.Objects;

public class JsonDataset extends JsonItem {
  private List<JsonField> fields = List.of();
  private List<String> primaryKey = List.of();
  private List<JsonForeignKey> foreignKeys = List.of();

  public List<JsonField> getFields() {
    return fields;
  }

  public void setFields(List<JsonField> fields) {
    this.fields = Objects.requireNonNull(fields);
  }

  public List<String> getPrimaryKey() {
    return primaryKey;
  }

  public void setPrimaryKey(List<String> primaryKey) {
    this.primaryKey = primaryKey;
  }

  public List<JsonForeignKey> getForeignKeys() {
    return foreignKeys;
  }

  public void setForeignKeys(List<JsonForeignKey> foreignKeys) {
    this.foreignKeys = foreignKeys;
  }
}
