package zeenea.connector.example.visualization;

import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.common.IdentificationProperty;
import zeenea.connector.common.ItemIdentifier;
import zeenea.connector.common.ItemInventory;
import zeenea.connector.common.ItemReference;
import zeenea.connector.contact.Contact;
import zeenea.connector.dataset.DataType;
import zeenea.connector.example.Metadata;
import zeenea.connector.example.TestConfiguration;
import zeenea.connector.example.TestPath;
import zeenea.connector.field.Field;
import zeenea.connector.property.PropertiesBuilder;
import zeenea.connector.property.PropertyDefinition;
import zeenea.connector.property.StringPropertyDefinition;
import zeenea.connector.visualization.Visualization;

class ExampleVisualizationConnectionTest {

  private final ExampleVisualizationConnector connector = new ExampleVisualizationConnector();

  private final ConnectionConfiguration configuration =
      TestConfiguration.builder()
          .connectorId("example-visualization")
          .connectionCode("test_visualization")
          .connectionName("Example Visualization")
          .pathParam("path", TestPath.parentFolder("dataset/example.visualization.ndjson"))
          .stringParam("custom_properties", "string type")
          .build();

  private final StringPropertyDefinition typeProperty = PropertyDefinition.string("type");

  @Test
  @DisplayName("inventory() should list the item identifiers")
  void testInventory() {

    try (var connection = connector.newConnection(configuration);
        var actual = connection.inventory()) {

      Assertions.assertThat(actual)
          .containsExactly(
              ItemInventory.builder()
                  .itemIdentifier(
                      ItemIdentifier.of(
                          IdentificationProperty.of("folder", "sales"),
                          IdentificationProperty.of("folder", "music"),
                          IdentificationProperty.of(
                              "uuid", "3485bcdd-7163-4d60-8b42-bbe29f004ca9")))
                  .labels("sales", "music", "Album Sales")
                  .build());
    }
  }

  @Test
  @DisplayName("extractItems should return the items")
  void extractItemsShouldReturnItems() {
    var ids =
        List.of(
            ItemIdentifier.of(
                IdentificationProperty.of("folder", "sales"),
                IdentificationProperty.of("folder", "music"),
                IdentificationProperty.of("uuid", "3485bcdd-7163-4d60-8b42-bbe29f004ca9")));

    try (var connection = connector.newConnection(configuration);
        var actual = connection.extractItems(ids.stream())) {

      var list = actual.collect(Collectors.toList());
      Assertions.assertThat(list)
          .containsExactly(
              Visualization.builder()
                  .id(
                      ItemIdentifier.of(
                          IdentificationProperty.of("folder", "sales"),
                          IdentificationProperty.of("folder", "music"),
                          IdentificationProperty.of(
                              "uuid", "3485bcdd-7163-4d60-8b42-bbe29f004ca9")))
                  .name("Album Sales")
                  .description("Album Sales Report")
                  .properties(
                      PropertiesBuilder.create()
                          .put(Metadata.PATH_MD, "example.visualization.ndjson")
                          .put(typeProperty, "report")
                          .build())
                  .contacts(
                      Contact.builder()
                          .role("owner")
                          .email("jean-michel.jarre@example.com")
                          .name("Jean Michel Jarre")
                          .phoneNumber("+1-212-555-7532")
                          .build())
                  .fields(
                      List.of(
                          Field.builder()
                              .identifier(
                                  ItemIdentifier.of(
                                      IdentificationProperty.of("field", "artist_name")))
                              .name("artist_name")
                              .nativeIndex(0)
                              .nativeType("varchar(120)")
                              .dataType(DataType.String)
                              .nullable(true)
                              .multivalued(false)
                              .build(),
                          Field.builder()
                              .identifier(
                                  ItemIdentifier.of(
                                      IdentificationProperty.of("field", "albums_count")))
                              .name("albums_count")
                              .nativeIndex(1)
                              .nativeType("int8")
                              .dataType(DataType.Long)
                              .nullable(true)
                              .multivalued(false)
                              .build()))
                  .sourceDatasets(
                      ItemReference.builder()
                          .itemIdentifier(
                              IdentificationProperty.of("schema", "music"),
                              IdentificationProperty.of("table", "artist_album_count"))
                          .connectionAlias("example_dataset")
                          .build())
                  .build());
    }
  }
}
