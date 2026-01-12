package zeenea.connector.example;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static zeenea.connector.example.ExampleConfiguration.AUTHENTICATION_USERNAME;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import zeenea.connector.Connection;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.exception.InvalidConfigurationException;

public class ConfigurationTest {

  @Test
  public void empty_username_should_throw_a_invalid_configuration_exception() {
    // Given
    ConnectionConfiguration config = mock(ConnectionConfiguration.class);
    when(config.getStringOptional(AUTHENTICATION_USERNAME)).thenReturn(Optional.empty());

    // When / Then
    assertThatThrownBy(
            () -> {
              ExampleConnector connector = new ExampleConnector();
              try (Connection ignored = connector.newConnection(config)) {}
            })
        .isInstanceOf(InvalidConfigurationException.class)
        .hasMessageContaining("Missing authentication username");
  }

  @Test
  public void empty_password_should_throw_a_invalid_configuration_exception() {
    // Given
    ConnectionConfiguration config = mock(ConnectionConfiguration.class);
    when(config.getStringOptional(AUTHENTICATION_USERNAME)).thenReturn(Optional.of("user"));
    when(config.getStringOptional(ExampleConfiguration.AUTHENTICATION_PASSWORD))
        .thenReturn(Optional.empty());

    // When / Then
    assertThatThrownBy(
            () -> {
              ExampleConnector connector = new ExampleConnector();
              try (Connection ignored = connector.newConnection(config)) {}
            })
        .isInstanceOf(InvalidConfigurationException.class)
        .hasMessageContaining("Missing authentication password");
  }
}
