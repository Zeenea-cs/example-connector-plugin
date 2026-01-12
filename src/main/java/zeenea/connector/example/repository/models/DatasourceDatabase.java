package zeenea.connector.example.repository.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatasourceDatabase {
  private final String id;
  private final String name;
}
