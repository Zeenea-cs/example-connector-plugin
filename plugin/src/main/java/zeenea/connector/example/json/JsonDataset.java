package zeenea.connector.example.json;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class JsonDataset extends JsonItem {
  private @NotNull List<JsonField> fields;
  private List<JsonForeignKey> foreignKeys = List.of();

  public @NotNull List<JsonField> getFields() {
    return fields;
  }

  public void setFields(@NotNull List<JsonField> fields) {
    this.fields = fields;
  }

  public List<JsonForeignKey> getForeignKeys() {
    return foreignKeys;
  }

  public void setForeignKeys(List<JsonForeignKey> foreignKeys) {
    this.foreignKeys = foreignKeys;
  }
}
