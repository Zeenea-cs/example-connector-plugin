package zeenea.connector.example.visualization;

import java.util.List;
import zeenea.connector.example.json.JsonVisualization;

public class VisualizationRoot {
  private List<JsonVisualization> visualizations = List.of();

  public List<JsonVisualization> getVisualizations() {
    return visualizations;
  }

  public void setVisualizations(List<JsonVisualization> visualizations) {
    this.visualizations = visualizations;
  }
}
