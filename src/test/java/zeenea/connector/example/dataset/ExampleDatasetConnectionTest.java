package zeenea.connector.example.dataset;

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
import zeenea.connector.dataset.Dataset;
import zeenea.connector.dataset.ForeignKey;
import zeenea.connector.example.Fix;
import zeenea.connector.example.Metadata;
import zeenea.connector.example.TestConfiguration;
import zeenea.connector.example.TestPath;
import zeenea.connector.field.Field;
import zeenea.connector.property.NumberPropertyDefinition;
import zeenea.connector.property.PropertiesBuilder;
import zeenea.connector.property.PropertyDefinition;
import zeenea.connector.property.StringPropertyDefinition;

class ExampleDatasetConnectionTest {

  private final ExampleDatasetConnector connector = new ExampleDatasetConnector();

  private final ConnectionConfiguration configuration =
      TestConfiguration.builder()
          .connectorId("example-dataset")
          .connectionCode("test_dataset")
          .connectionName("Example Dataset")
          .pathParam("path", TestPath.parentFolder("dataset/example.dataset.ndjson"))
          .stringParam(
              "custom_properties", "string type\nstring schema\nnumber 'row count' from rows")
          .build();

  private final StringPropertyDefinition typeProperty = PropertyDefinition.string("type");
  private final StringPropertyDefinition schemaProperty = PropertyDefinition.string("schema");
  private final NumberPropertyDefinition rowCountProperty = PropertyDefinition.number("row count");

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
                          IdentificationProperty.of("schema", "music"),
                          IdentificationProperty.of("table", "albums")))
                  .labels("music", "albums")
                  .build(),
              ItemInventory.builder()
                  .itemIdentifier(
                      ItemIdentifier.of(
                          IdentificationProperty.of("schema", "music"),
                          IdentificationProperty.of("table", "artists")))
                  .labels("music", "artists")
                  .build(),
              ItemInventory.builder()
                  .itemIdentifier(
                      ItemIdentifier.of(
                          IdentificationProperty.of("schema", "music"),
                          IdentificationProperty.of("table", "artist_album_count")))
                  .labels("music", "artist_album_count")
                  .build());
    }
  }

  @Test
  @DisplayName("extractItems should return the items")
  void extractItemsShouldReturnItems() {
    var ids =
        List.of(
            ItemIdentifier.of(
                IdentificationProperty.of("schema", "music"),
                IdentificationProperty.of("table", "albums")),
            ItemIdentifier.of(
                IdentificationProperty.of("schema", "music"),
                IdentificationProperty.of("table", "artist_album_count")));

    try (var connection = connector.newConnection(configuration);
        var actual = connection.extractItems(ids.stream())) {

      var list = actual.collect(Collectors.toList());
      Assertions.assertThat(list)
          .containsExactly(
              Dataset.builder()
                  .id(
                      ItemIdentifier.of(
                          IdentificationProperty.of("schema", "music"),
                          IdentificationProperty.of("table", "albums")))
                  .name("albums")
                  .description("Albums that group music plays")
                  .properties(
                      PropertiesBuilder.create()
                          .put(Metadata.PATH_MD, "example.dataset.ndjson")
                          .put(typeProperty, "table")
                          .put(schemaProperty, "music")
                          .put(rowCountProperty, 4321)
                          .build())
                  .fields(
                      List.of(
                          Field.builder()
                              .identifier(
                                  ItemIdentifier.of(IdentificationProperty.of("field", "album_id")))
                              .name("album_id")
                              .description("Album identifier")
                              .nativeIndex(0)
                              .nativeType("numeric")
                              .dataType(DataType.BigDecimal)
                              .nullable(false)
                              .multivalued(false)
                              .build(),
                          Field.builder()
                              .identifier(
                                  ItemIdentifier.of(IdentificationProperty.of("field", "title")))
                              .name("title")
                              .description("Album Title")
                              .nativeIndex(1)
                              .nativeType("varchar(160)")
                              .dataType(DataType.String)
                              .nullable(false)
                              .multivalued(false)
                              .build(),
                          Field.builder()
                              .identifier(
                                  ItemIdentifier.of(
                                      IdentificationProperty.of("field", "artist_id")))
                              .name("artist_id")
                              .description("Link to the Artist that performed the album")
                              .nativeIndex(2)
                              .nativeType("numeric")
                              .dataType(DataType.BigDecimal)
                              .nullable(false)
                              .multivalued(false)
                              .build()))
                  .contacts(
                      Contact.builder()
                          .role("owner")
                          .email("jean-michel.jarre@example.com")
                          .name("Jean Michel Jarre")
                          .phoneNumber("+1-212-555-7532")
                          .build())
                  .primaryKeys("album_id")
                  .foreignKeys(
                      Fix.build(
                          ForeignKey.builder()
                              .name("fk_albums_artists")
                              .targetDataset("/schema=music/table=artists")
                              .targetFields("artist_id")
                              .sourceFields("artist_id")))
                  .build(),
              Dataset.builder()
                  .id(
                      ItemIdentifier.of(
                          IdentificationProperty.of("schema", "music"),
                          IdentificationProperty.of("table", "artist_album_count")))
                  .name("artist_album_count")
                  .properties(
                      PropertiesBuilder.create()
                          .put(Metadata.PATH_MD, "example.dataset.ndjson")
                          .put(typeProperty, "view")
                          .put(schemaProperty, "music")
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
                              IdentificationProperty.of("table", "albums"))
                          .connectionCode("test_dataset")
                          .build(),
                      ItemReference.builder()
                          .itemIdentifier(
                              IdentificationProperty.of("schema", "music"),
                              IdentificationProperty.of("table", "artists"))
                          .connectionCode("test_dataset")
                          .build())
                  .build());
    }
  }
}
