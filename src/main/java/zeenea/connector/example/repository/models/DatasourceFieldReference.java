package zeenea.connector.example.repository.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatasourceFieldReference {
  private final String databaseId;
  private final String itemId;
  private final String fieldName;
}
