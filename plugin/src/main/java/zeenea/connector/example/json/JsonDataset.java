package zeenea.connector.example.json;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class JsonDataset extends JsonItem implements WithFields {
  private @NotNull List<JsonField> fields;
  private List<String> primaryKey = List.of();
  private List<JsonForeignKey> foreignKeys = List.of();

  public @NotNull List<JsonField> getFields() {
    return fields;
  }

  public void setFields(@NotNull List<JsonField> fields) {
    this.fields = fields;
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
