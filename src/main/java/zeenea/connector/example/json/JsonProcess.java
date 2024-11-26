package zeenea.connector.example.json;

import java.util.List;

public class JsonProcess extends JsonItem {
  private List<JsonItemRef> targets = List.of();

  public List<JsonItemRef> getTargets() {
    return targets;
  }

  public void setTargets(List<JsonItemRef> targets) {
    this.targets = targets;
  }
}
