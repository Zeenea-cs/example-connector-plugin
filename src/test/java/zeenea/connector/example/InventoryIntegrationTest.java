package zeenea.connector.example;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static zeenea.connector.example.ExampleConfiguration.AUTHENTICATION_USERNAME;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.common.IdentificationProperty;
import zeenea.connector.common.ItemInventory;
import zeenea.connector.common.LabelIdentifier;

public class InventoryIntegrationTest {

  @Test
  public void testInventory() {
    // Given
    ConnectionConfiguration config = mock(ConnectionConfiguration.class);
    when(config.getStringOptional(AUTHENTICATION_USERNAME)).thenReturn(Optional.of("my_username"));
    when(config.getStringOptional(ExampleConfiguration.AUTHENTICATION_PASSWORD))
        .thenReturn(Optional.of("my_password"));

    ExampleConnection connection = (ExampleConnection) new ExampleConnector().newConnection(config);

    // When
    List<ItemInventory> inventory = connection.inventory().collect(Collectors.toList());

    // Then
    Assertions.assertThat(inventory).hasSize(4);

    // verify item identifiers
    Assertions.assertThat(inventory)
        .extracting(ItemInventory::getItemIdentifier)
        .extracting(
            id ->
                id.getIdentificationProperties().stream()
                    .map(p -> p.getKey() + "=" + p.getValue())
                    .collect(Collectors.joining(",")))
        .containsExactly(
            "databaseId=d63ce9ef-4d8d-4d28-a84f-292849fc3173,id=b52d59ad-19ab-434c-a0e3-914bf7f99f49",
            "databaseId=d63ce9ef-4d8d-4d28-a84f-292849fc3173,id=446635fd-6773-4862-ad33-f7c99963f6d7",
            "databaseId=96813f62-80a7-4024-bd28-b563cd22bee5,id=f1e2d3c4-b5a6-7890-abcd-ef0987654321",
            "databaseId=96813f62-80a7-4024-bd28-b563cd22bee5,id=b510a18e-9cda-4140-983e-5bfdfdbfa6b3");

    // verify label identifiers
    Assertions.assertThat(inventory)
        .extracting(ItemInventory::getLabelIdentifier)
        .containsExactly(
            LabelIdentifier.of(
                IdentificationProperty.of("databaseName", "CustomerDB"),
                IdentificationProperty.of("name", "Customers")
            ),
            LabelIdentifier.of(
                IdentificationProperty.of("databaseName", "CustomerDB"), IdentificationProperty.of("name", "Orders")),
            LabelIdentifier.of(
                IdentificationProperty.of("databaseName", "SalesDB"), IdentificationProperty.of("name", "Sales")),
            LabelIdentifier.of(
                IdentificationProperty.of("databaseName", "SalesDB"), IdentificationProperty.of("name", "SalesPerMonth")));
  }
}
