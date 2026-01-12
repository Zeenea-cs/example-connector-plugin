package zeenea.connector.example.mapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import zeenea.connector.common.ItemReference;
import zeenea.connector.dataset.DataType;
import zeenea.connector.example.ExampleMetadata;
import zeenea.connector.example.repository.models.DatasourceField;
import zeenea.connector.field.Field;
import zeenea.connector.property.PropertiesBuilder;
import zeenea.connector.property.PropertyValue;
import zeenea.connector.property.StringPropertyValue;

public class FieldMapper {

  public static @NotNull List<Field> map(List<DatasourceField> fields) {
    AtomicInteger counter = new AtomicInteger(0);
    return fields.stream().map(f -> map(f, counter.getAndIncrement())).collect(Collectors.toList());
  }

  public static @NotNull Field map(DatasourceField field, int index) {
    return Field.builder()
        .id(IdentifierMapper.mapToItemIdentifier(field.getName()))
        .name(field.getName())
        .dataType(convertDatasourceTypeToZeeneaType(field))
        .nativeType(field.getDataType().name())
        .nativeIndex(index)
        .nullable(false)
        .multivalued(false)
        .description(null)
        .properties(getProperties(field))
        .sourceFields(getSourceFields(field))
        .build();
  }

  private static DataType convertDatasourceTypeToZeeneaType(DatasourceField field) {
    switch (field.getDataType()) {
      case INTEGER:
        return DataType.Integer;
      case DECIMAL:
        return DataType.Float;
      case VARCHAR:
        return DataType.String;
      case DATE:
        return DataType.Timestamp;
      case REFERENCE:
        return DataType.Unknown;
      default:
        return DataType.Unknown;
    }
  }

  private static Map<String, PropertyValue> getProperties(DatasourceField field) {
    PropertiesBuilder properties = PropertiesBuilder.create();
    Optional.ofNullable(field.getFormula())
        .ifPresent(
            formula -> properties.put(ExampleMetadata.FORMULA, new StringPropertyValue(formula)));
    return properties.build();
  }

  private static List<ItemReference> getSourceFields(DatasourceField field) {
    if (field.getSourceField() == null) return List.of();
    return List.of(
        ItemReference.of(
            IdentifierMapper.mapSourceFieldToItemIdentifier(
                field.getSourceField().getDatabaseId(),
                field.getSourceField().getItemId(),
                field.getSourceField().getFieldName())));
  }
}
