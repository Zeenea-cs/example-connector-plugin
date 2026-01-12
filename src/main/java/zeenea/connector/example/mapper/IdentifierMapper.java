package zeenea.connector.example.mapper;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import zeenea.connector.common.*;
import zeenea.connector.example.repository.models.DatasourceElement;

public class IdentifierMapper {

  public static final String DATABASE_ID = "databaseId";
  public static final String ID = "id";
  public static final String DATABASE_NAME = "databaseName";
  public static final String NAME = "name";
  public static final String FIELD_NAME = "fieldName";

  public static ItemInventory mapToItemInventory(DatasourceElement element) {
    // Identification that will uniquely identify the item for a program (UUID, technical name,
    // etc.)
    // These values will be received as parameter to the "ExampleConnection.extractItems" method
    ItemIdentifier itemIdentifier = mapToItemIdentifier(element);

    // Identification that will uniquely identify the item for a Human (name, path, etc.)
    // These values will be shown when importing the item
    LabelIdentifier labelIdentifier = mapToLabelIdentifier(element);

    return ItemInventory.of(itemIdentifier, labelIdentifier);
  }

  public static ItemIdentifier mapToItemIdentifier(DatasourceElement element) {
    return mapToItemIdentifier(element.getDatabase().getId(), element.getId());
  }

  public static ItemIdentifier mapToItemIdentifier(String databaseId, String id) {
    return ItemIdentifier.of(
        IdentificationProperty.of(DATABASE_ID, databaseId), IdentificationProperty.of(ID, id));
  }

  public static ItemIdentifier mapToItemIdentifier(String fieldName) {
    return ItemIdentifier.of(IdentificationProperty.of(FIELD_NAME, fieldName));
  }

  public static ItemIdentifier mapSourceFieldToItemIdentifier(
      String databaseId, String itemId, String fieldName) {
    return ItemIdentifier.of(
        IdentificationProperty.of(DATABASE_ID, databaseId),
        IdentificationProperty.of(ID, itemId),
        IdentificationProperty.of(FIELD_NAME, fieldName));
  }

  public static ItemReference mapToItemReference(String databaseId, String id) {
    return ItemReference.of(mapToItemIdentifier(databaseId, id));
  }

  private static LabelIdentifier mapToLabelIdentifier(DatasourceElement element) {
    return LabelIdentifier.of(
        IdentificationProperty.of(DATABASE_NAME, element.getDatabase().getName()),
        IdentificationProperty.of(NAME, element.getName()));
  }

  public static @NotNull Optional<String> getDatabaseId(ItemIdentifier itemIdentifier) {
    return itemIdentifier.getUniquePropertyValue(DATABASE_ID);
  }

  public static @NotNull Optional<String> getId(ItemIdentifier itemIdentifier) {
    return itemIdentifier.getUniquePropertyValue(ID);
  }
}
