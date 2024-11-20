package zeenea.connector.example.lineage;

import java.util.List;
import zeenea.connector.example.json.JsonProcess;

public class LineageRoot {
  private List<JsonProcess> lineage = List.of();

  public List<JsonProcess> getLineage() {
    return lineage;
  }

  public void setLineage(List<JsonProcess> lineage) {
    this.lineage = lineage;
  }
}
