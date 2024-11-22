package zeenea.connector.example.dataset;

import java.util.List;
import zeenea.connector.example.json.JsonDataset;
import zeenea.connector.example.json.JsonProcess;

public class DatasetRoot {
  private List<JsonDataset> datasets;
  private List<JsonProcess> lineage;

  public List<JsonDataset> getDatasets() {
    return datasets;
  }

  public void setDatasets(List<JsonDataset> datasets) {
    this.datasets = datasets;
  }

  public List<JsonProcess> getLineage() {
    return lineage;
  }

  public void setLineage(List<JsonProcess> lineage) {
    this.lineage = lineage;
  }
}
