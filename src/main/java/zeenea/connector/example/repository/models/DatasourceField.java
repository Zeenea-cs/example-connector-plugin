package zeenea.connector.example.repository.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatasourceField {
  private final String name;
  private final DatasourceFieldType dataType;
  private final String formula;
  private final DatasourceFieldReference sourceField;
  private final DatasourceFieldReference reference;
}
