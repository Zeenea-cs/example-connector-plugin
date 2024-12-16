/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.json;

import java.util.List;
import java.util.Objects;

public final class JsonForeignKey {
  private String name;
  private String targetDataset;
  private List<String> targetFields = List.of();
  private List<String> sourceFields = List.of();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTargetDataset() {
    return targetDataset;
  }

  public void setTargetDataset(String targetDataset) {
    this.targetDataset = targetDataset;
  }

  public List<String> getTargetFields() {
    return targetFields;
  }

  public void setTargetFields(List<String> targetFields) {
    this.targetFields = Objects.requireNonNull(targetFields);
  }

  public List<String> getSourceFields() {
    return sourceFields;
  }

  public void setSourceFields(List<String> sourceFields) {
    this.sourceFields = Objects.requireNonNull(sourceFields);
  }
}
