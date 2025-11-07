package zeenea.connector.example.mapper;

import static zeenea.connector.example.ExampleMetadata.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import zeenea.connector.Item;
import zeenea.connector.common.ItemIdentifier;
import zeenea.connector.common.ItemReference;
import zeenea.connector.contact.Contact;
import zeenea.connector.dataset.Dataset;
import zeenea.connector.dataset.ForeignKey;
import zeenea.connector.example.repository.models.DatasourceElement;
import zeenea.connector.example.repository.models.DatasourceElementType;
import zeenea.connector.example.repository.models.DatasourceFieldType;
import zeenea.connector.property.*;
import zeenea.connector.visualization.Visualization;

public class ItemMapper {

  public static Item mapToItem(DatasourceElement datasourceElement) {
    if (Objects.requireNonNull(datasourceElement.getType()) == DatasourceElementType.TABLE)
      return mapToDataset(datasourceElement);
    return mapToVisualization(datasourceElement);
  }

  private static Visualization mapToVisualization(DatasourceElement element) {
    return Visualization.builder()
        .id(IdentifierMapper.mapToItemIdentifier(element))
        .name(element.getName())
        .description(null)
        .properties(
            PropertiesBuilder.create()
                .put(DATABASE, new StringPropertyValue(element.getDatabase().getName()))
                .put(INSTANT, new InstantPropertyValue(Instant.ofEpochMilli(897681600)))
                .put(LONGTEXT, "This is a long text property value example.")
                .put(NUMBER, new NumberPropertyValue(BigDecimal.valueOf(42)))
                .put(STRING, new StringPropertyValue("This is a string property value example."))
                .put(TAG, new TagPropertyValue(List.of("tag1", "tag2", "tag3")))
                .put(URL, new UrlPropertyValue(URI.create("https://www.example.com/path/to/item")))
                .build())
        .contacts(Contact.of("example.of@email.com", "name", "phoneNumber", "role"))
        .fields(FieldMapper.map(element.getFields()))
        .sourceDatasets(getSourceDatasets(element))
        .build();
  }

  private static Dataset mapToDataset(DatasourceElement element) {
    return Dataset.builder()
        .id(IdentifierMapper.mapToItemIdentifier(element))
        .name(element.getName())
        .description(null)
        .primaryKeyIdentifiers(getPrimaryKeys(element))
        .foreignKeys(getForeignKeys(element))
        .properties(
            Map.of(
                DATABASE.getName(), new StringPropertyValue(element.getDatabase().getName()),
                INSTANT.getName(), new InstantPropertyValue(Instant.ofEpochMilli(897681600)),
                LONGTEXT.getName(),
                    new LongTextPropertyValue("This is a long text property value example."),
                NUMBER.getName(), new NumberPropertyValue(BigDecimal.valueOf(42)),
                STRING.getName(),
                    new StringPropertyValue("This is a string property value example."),
                TAG.getName(), new TagPropertyValue(List.of("tag1", "tag2", "tag3")),
                URL.getName(),
                    new UrlPropertyValue(URI.create("https://www.example.com/path/to/item"))))
        .contacts(Contact.of("example.of@email.com", "name", "phoneNumber", "role"))
        .fields(FieldMapper.map(element.getFields()))
        .sourceDatasets(getSourceDatasets(element))
        .build();
  }

  private static @NotNull List<ItemIdentifier> getPrimaryKeys(DatasourceElement element) {
    return List.of(IdentifierMapper.mapToItemIdentifier(element.getPrimaryKey()));
  }

  private static List<ItemReference> getSourceDatasets(DatasourceElement element) {
    return element.getFields().stream()
        .filter(f -> Objects.nonNull(f.getSourceField()))
        .map(f -> new Pair<>(f.getSourceField().getDatabaseId(), f.getSourceField().getItemId()))
        .map(p -> IdentifierMapper.mapToItemReference(p.getValue0(), p.getValue1()))
        .collect(Collectors.toList());
  }

  private static @NotNull List<ForeignKey> getForeignKeys(DatasourceElement element) {
    return element.getFields().stream()
        .filter(f -> DatasourceFieldType.REFERENCE == f.getDataType())
        .filter(f -> f.getReference() != null)
        .map(
            f ->
                ForeignKey.builder()
                    .name("FK_" + element.getName() + "_" + f.getName())
                    .sourceFieldIdentifiers(IdentifierMapper.mapToItemIdentifier(f.getName()))
                    .targetDatasetIdentifier(
                        IdentifierMapper.mapToItemIdentifier(
                            f.getReference().getDatabaseId(), f.getReference().getItemId()))
                    .targetFieldIdentifiers(
                        IdentifierMapper.mapToItemIdentifier(f.getReference().getFieldName()))
                    .build())
        .collect(Collectors.toList());
  }
}
