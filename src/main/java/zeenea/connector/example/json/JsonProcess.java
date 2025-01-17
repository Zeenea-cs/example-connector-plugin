/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.json;

import java.util.List;
import java.util.Objects;

public class JsonProcess extends JsonItem {
  private List<JsonItemRef> targets = List.of();
  private List<JsonOperation> operations = List.of();

  public List<JsonItemRef> getTargets() {
    return targets;
  }

  public void setTargets(List<JsonItemRef> targets) {
    this.targets = Objects.requireNonNull(targets);
  }

  public List<JsonOperation> getOperations() {
    return operations;
  }

  public void setOperations(List<JsonOperation> operations) {
    this.operations = operations;
  }
}
