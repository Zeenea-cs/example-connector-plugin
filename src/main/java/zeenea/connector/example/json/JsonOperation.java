/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.json;

import java.util.List;

public class JsonOperation {
  private List<JsonItemRef> sources = List.of();
  private List<JsonItemRef> targets = List.of();

  public List<JsonItemRef> getSources() {
    return sources;
  }

  public void setSources(List<JsonItemRef> sources) {
    this.sources = sources;
  }

  public List<JsonItemRef> getTargets() {
    return targets;
  }

  public void setTargets(List<JsonItemRef> targets) {
    this.targets = targets;
  }
}
