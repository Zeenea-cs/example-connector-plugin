package zeenea.connector.example;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static zeenea.connector.example.ExampleConfiguration.AUTHENTICATION_USERNAME;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.Item;
import zeenea.connector.common.IdentificationProperty;
import zeenea.connector.common.ItemIdentifier;

public class ExtractionIntegrationTest {

  public static String jsonify(Object actual) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
    mapper.findAndRegisterModules();
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actual);
  }

  public static Options approvalOptions() {
    return new Options().forFile().withExtension(".json");
  }

  @Test
  public void testExtractionOfCustomers() throws JsonProcessingException {
    // Given
    ConnectionConfiguration config = mock(ConnectionConfiguration.class);
    when(config.getStringOptional(AUTHENTICATION_USERNAME)).thenReturn(Optional.of("my_username"));
    when(config.getStringOptional(ExampleConfiguration.AUTHENTICATION_PASSWORD))
        .thenReturn(Optional.of("my_password"));

    ExampleConnection connection = (ExampleConnection) new ExampleConnector().newConnection(config);

    // When
    ItemIdentifier itemId =
        ItemIdentifier.of(
            IdentificationProperty.of("databaseId", "d63ce9ef-4d8d-4d28-a84f-292849fc3173"),
            IdentificationProperty.of("id", "b52d59ad-19ab-434c-a0e3-914bf7f99f49"));
    List<Item> extraction = connection.extractItems(Stream.of(itemId)).collect(Collectors.toList());

    // Then
    Assertions.assertThat(extraction).hasSize(1);
    String json = jsonify(extraction.get(0));
    Approvals.verify(json, approvalOptions());
  }

  @Test
  public void testExtractionOfOrders() throws JsonProcessingException {
    // Given
    ConnectionConfiguration config = mock(ConnectionConfiguration.class);
    when(config.getStringOptional(AUTHENTICATION_USERNAME)).thenReturn(Optional.of("my_username"));
    when(config.getStringOptional(ExampleConfiguration.AUTHENTICATION_PASSWORD))
        .thenReturn(Optional.of("my_password"));

    ExampleConnection connection = (ExampleConnection) new ExampleConnector().newConnection(config);

    // When
    ItemIdentifier itemId =
        ItemIdentifier.of(
            IdentificationProperty.of("databaseId", "d63ce9ef-4d8d-4d28-a84f-292849fc3173"),
            IdentificationProperty.of("id", "446635fd-6773-4862-ad33-f7c99963f6d7"));
    List<Item> extraction = connection.extractItems(Stream.of(itemId)).collect(Collectors.toList());

    // Then
    Assertions.assertThat(extraction).hasSize(1);
    String json = jsonify(extraction.get(0));
    Approvals.verify(json, approvalOptions());
  }

  @Test
  public void testExtractionOfSales() throws JsonProcessingException {
    // Given
    ConnectionConfiguration config = mock(ConnectionConfiguration.class);
    when(config.getStringOptional(AUTHENTICATION_USERNAME)).thenReturn(Optional.of("my_username"));
    when(config.getStringOptional(ExampleConfiguration.AUTHENTICATION_PASSWORD))
        .thenReturn(Optional.of("my_password"));

    ExampleConnection connection = (ExampleConnection) new ExampleConnector().newConnection(config);

    // When
    ItemIdentifier itemId =
        ItemIdentifier.of(
            IdentificationProperty.of("databaseId", "96813f62-80a7-4024-bd28-b563cd22bee5"),
            IdentificationProperty.of("id", "f1e2d3c4-b5a6-7890-abcd-ef0987654321"));
    List<Item> extraction = connection.extractItems(Stream.of(itemId)).collect(Collectors.toList());

    // Then
    Assertions.assertThat(extraction).hasSize(1);
    String json = jsonify(extraction.get(0));
    Approvals.verify(json, approvalOptions());
  }

  @Test
  public void testExtractionOfSalesPerMonth() throws JsonProcessingException {
    // Given
    ConnectionConfiguration config = mock(ConnectionConfiguration.class);
    when(config.getStringOptional(AUTHENTICATION_USERNAME)).thenReturn(Optional.of("my_username"));
    when(config.getStringOptional(ExampleConfiguration.AUTHENTICATION_PASSWORD))
        .thenReturn(Optional.of("my_password"));

    ExampleConnection connection = (ExampleConnection) new ExampleConnector().newConnection(config);

    // When
    ItemIdentifier itemId =
        ItemIdentifier.of(
            IdentificationProperty.of("databaseId", "96813f62-80a7-4024-bd28-b563cd22bee5"),
            IdentificationProperty.of("id", "b510a18e-9cda-4140-983e-5bfdfdbfa6b3"));
    List<Item> extraction = connection.extractItems(Stream.of(itemId)).collect(Collectors.toList());

    // Then
    Assertions.assertThat(extraction).hasSize(1);
    String json = jsonify(extraction.get(0));
    Approvals.verify(json, approvalOptions());
  }
}
