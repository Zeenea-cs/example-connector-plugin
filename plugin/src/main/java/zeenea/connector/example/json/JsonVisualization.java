package zeenea.connector.example.json;

import java.util.List;

public final class JsonVisualization extends JsonItem {
  private List<JsonDataset> datasets = List.of();

  public List<JsonDataset> getDatasets() {
    return datasets;
  }

  public void setDatasets(List<JsonDataset> datasets) {
    this.datasets = datasets;
  }
}
