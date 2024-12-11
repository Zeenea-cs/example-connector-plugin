package zeenea.connector.example.lineage;

import java.time.Instant;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.common.DataSourceIdentifier;
import zeenea.connector.common.IdentificationProperty;
import zeenea.connector.common.ItemIdentifier;
import zeenea.connector.common.ItemReference;
import zeenea.connector.contact.Contact;
import zeenea.connector.example.Metadata;
import zeenea.connector.example.TestConfiguration;
import zeenea.connector.example.TestPath;
import zeenea.connector.process.DataProcess;
import zeenea.connector.process.Operation;
import zeenea.connector.property.InstantPropertyDefinition;
import zeenea.connector.property.PropertiesBuilder;
import zeenea.connector.property.PropertyDefinition;
import zeenea.connector.property.StringPropertyDefinition;

class ExampleLineageConnectionTest {

  private final ExampleLineageConnector connector = new ExampleLineageConnector();

  private final ConnectionConfiguration configuration =
      TestConfiguration.builder()
          .connectorId("example-lineage")
          .connectionCode("test_lineage")
          .connectionName("Example Lineage")
          .pathParam("path", TestPath.parentFolder("dataset/example.lineage.ndjson"))
          .stringParam("custom_properties", "string type\ninstant last_execution from 'lastRun'")
          .stringParam("filter", "type = 'procedure'")
          .build();

  private final StringPropertyDefinition typeProperty = PropertyDefinition.string("type");
  private final InstantPropertyDefinition lastExecution =
      PropertyDefinition.instant("last_execution");

  @Disabled /* FIXME Waiting the implementation of Operation.equals() to enable the test.  */
  @Test
  @DisplayName("synchronize() should return the items")
  void extractItemsShouldReturnItems() {
    try (var connection = connector.newConnection(configuration);
        var actual = connection.synchronize()) {

      var list = actual.collect(Collectors.toList());
      Assertions.assertThat(list)
          .containsExactly(
              DataProcess.builder()
                  .id(
                      ItemIdentifier.of(
                          IdentificationProperty.of("id", "7fa78329-6ceb-404d-9f82-ffba0292a686")))
                  .name("Create Artist Album Count")
                  .description("Create the artist album count table")
                  .properties(
                      PropertiesBuilder.create()
                          .put(Metadata.PATH_MD, "example.lineage.ndjson")
                          .put(typeProperty, "procedure")
                          .put(lastExecution, Instant.parse("2024-12-03T11:23:42.231Z"))
                          .build())
                  .contacts(
                      Contact.builder()
                          .role("owner")
                          .email("jean-michel.jarre@example.com")
                          .name("Jean Michel Jarre")
                          .phoneNumber("+1-212-555-7532")
                          .build())
                  .sources(
                      ItemReference.builder()
                          .itemIdentifier(
                              IdentificationProperty.of("schema", "music"),
                              IdentificationProperty.of("table", "albums"))
                          .dataSourceIdentifier(
                              DataSourceIdentifier.of(
                                  IdentificationProperty.of("alias", "example_dataset")))
                          .build(),
                      ItemReference.builder()
                          .itemIdentifier(
                              IdentificationProperty.of("schema", "music"),
                              IdentificationProperty.of("table", "artists"))
                          .dataSourceIdentifier(
                              DataSourceIdentifier.of(
                                  IdentificationProperty.of("alias", "example_dataset")))
                          .build())
                  .targets(
                      ItemReference.builder()
                          .itemIdentifier(
                              IdentificationProperty.of("schema", "music"),
                              IdentificationProperty.of("table", "artist_album_count"))
                          .dataSourceIdentifier(
                              DataSourceIdentifier.of(
                                  IdentificationProperty.of("alias", "example_dataset")))
                          .build())
                  .operations(
                      Operation.builder()
                          .sources(
                              ItemReference.builder()
                                  .itemIdentifier(
                                      IdentificationProperty.of("schema", "music"),
                                      IdentificationProperty.of("table", "artists"),
                                      IdentificationProperty.of("field", "name"))
                                  .dataSourceIdentifier(
                                      DataSourceIdentifier.of(
                                          IdentificationProperty.of("alias", "example_dataset")))
                                  .build())
                          .targets(
                              ItemReference.builder()
                                  .itemIdentifier(
                                      IdentificationProperty.of("schema", "music"),
                                      IdentificationProperty.of("table", "artist_album_count"),
                                      IdentificationProperty.of("field", "artist_name"))
                                  .dataSourceIdentifier(
                                      DataSourceIdentifier.of(
                                          IdentificationProperty.of("alias", "example_dataset")))
                                  .build())
                          .build())
                  .build());
    }
  }
}
