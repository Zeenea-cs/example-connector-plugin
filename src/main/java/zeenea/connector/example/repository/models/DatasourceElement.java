package zeenea.connector.example.repository.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Builder
@Getter
public class DatasourceElement {
  private final String id;
  private final String name;
  private final DatasourceDatabase database;

  // Let's assume type can be TABLE or VIEW which will define if it is a Dataset or a Visualization.
  private final DatasourceElementType type;

  private final String primaryKey;
  private final Map<String, DatasourceFieldReference> foreignKeys;
  private final List<DatasourceField> fields;
}
