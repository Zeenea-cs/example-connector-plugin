package zeenea.connector.example.json;

import java.util.List;

public final class JsonForeignKey {
  private String name;
  private String targetDataset;
  private List<String> targetFields;
  private List<String> sourceFields;

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
    this.targetFields = targetFields;
  }

  public List<String> getSourceFields() {
    return sourceFields;
  }

  public void setSourceFields(List<String> sourceFields) {
    this.sourceFields = sourceFields;
  }
}
