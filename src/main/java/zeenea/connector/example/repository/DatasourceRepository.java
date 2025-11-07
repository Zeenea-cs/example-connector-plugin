package zeenea.connector.example.repository;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import zeenea.connector.example.repository.models.*;

@Builder
public class DatasourceRepository {
  private final String username;
  private final String password;
  private final String nullable;
  private final Long optionalNumber;
  private final Boolean flag;

  // Some business logic to build the repository
  private static class CustomDatasourceRepositoryBuilder extends DatasourceRepositoryBuilder {

    @Override
    public DatasourceRepository build() {
      // Authenticate to the datasource
      authenticate(super.username, super.password);

      // Some business logic specific to the datasource
      if (Boolean.TRUE.equals(super.flag)) super.optionalNumber(super.optionalNumber + 1);
      else super.nullable("Site " + super.optionalNumber);

      return super.build();
    }
  }

  public static void authenticate(String username, String password)
      throws zeenea.connector.exception.InvalidConfigurationException {
    if (!"my_username".equals(username) || !"my_password".equals(password)) {
      throw new zeenea.connector.exception.InvalidConfigurationException(
          "Authentication failed for user: " + username);
    }
  }

  public List<DatasourceElement> listAllItems() {
    return allElements();
  }

  public DatasourceElement find(String databaseId, String itemId) {
    return listAllItems().stream()
        .filter(e -> databaseId.equals(e.getDatabase().getId()))
        .filter(item -> itemId.equals(item.getId()))
        .findFirst()
        .orElse(null);
  }

  public void close() {
    // Close the datasource connection properly
  }

  public static List<DatasourceElement> allElements() {
    DatasourceDatabase db1 =
        DatasourceDatabase.builder()
            .id("d63ce9ef-4d8d-4d28-a84f-292849fc3173")
            .name("CustomerDB")
            .build();
    DatasourceDatabase db2 =
        DatasourceDatabase.builder()
            .id("96813f62-80a7-4024-bd28-b563cd22bee5")
            .name("SalesDB")
            .build();

    DatasourceElement customers =
        DatasourceElement.builder()
            .id("b52d59ad-19ab-434c-a0e3-914bf7f99f49")
            .name("Customers")
            .type(DatasourceElementType.TABLE)
            .database(db1)
            .fields(
                List.of(
                    DatasourceField.builder()
                        .name("CustomerID")
                        .dataType(DatasourceFieldType.INTEGER)
                        .build(),
                    DatasourceField.builder()
                        .name("CustomerName")
                        .dataType(DatasourceFieldType.VARCHAR)
                        .build(),
                    DatasourceField.builder()
                        .name("ContactEmail")
                        .dataType(DatasourceFieldType.VARCHAR)
                        .build()))
            .primaryKey("CustomerID")
            .build();

    DatasourceElement orders =
        DatasourceElement.builder()
            .id("446635fd-6773-4862-ad33-f7c99963f6d7")
            .name("Orders")
            .type(DatasourceElementType.TABLE)
            .database(db1)
            .fields(
                List.of(
                    DatasourceField.builder()
                        .name("OrderID")
                        .dataType(DatasourceFieldType.INTEGER)
                        .build(),
                    DatasourceField.builder()
                        .name("OrderDate")
                        .dataType(DatasourceFieldType.DATE)
                        .build(),
                    DatasourceField.builder()
                        .name("CustomerIDReference")
                        .dataType(DatasourceFieldType.REFERENCE)
                        .reference(
                            DatasourceFieldReference.builder()
                                .databaseId(db1.getId())
                                .itemId(customers.getId())
                                .fieldName("CustomerID")
                                .build())
                        .build()))
            .primaryKey("OrderID")
            .foreignKeys(
                Map.of(
                    "CustomerIDReference",
                    DatasourceFieldReference.builder()
                        .databaseId(db1.getId())
                        .itemId(customers.getId())
                        .fieldName("CustomerID")
                        .build()))
            .build();

    DatasourceElement sales =
        DatasourceElement.builder()
            .id("f1e2d3c4-b5a6-7890-abcd-ef0987654321")
            .name("Sales")
            .type(DatasourceElementType.TABLE)
            .database(db2)
            .fields(
                List.of(
                    DatasourceField.builder()
                        .name("SaleID")
                        .dataType(DatasourceFieldType.INTEGER)
                        .build(),
                    DatasourceField.builder()
                        .name("SaleAmount")
                        .dataType(DatasourceFieldType.DECIMAL)
                        .build(),
                    DatasourceField.builder()
                        .name("SaleDate")
                        .dataType(DatasourceFieldType.DATE)
                        .build()))
            .primaryKey("SaleID")
            .build();

    DatasourceElement salesPerMonth =
        DatasourceElement.builder()
            .id("b510a18e-9cda-4140-983e-5bfdfdbfa6b3")
            .name("SalesPerMonth")
            .type(DatasourceElementType.VIEW)
            .database(db2)
            .fields(
                List.of(
                    DatasourceField.builder()
                        .name("Month")
                        .dataType(DatasourceFieldType.VARCHAR)
                        .build(),
                    DatasourceField.builder()
                        .name("TotalSales")
                        .dataType(DatasourceFieldType.DECIMAL)
                        .formula("SUM(SaleAmount)")
                        .sourceField(
                            DatasourceFieldReference.builder()
                                .databaseId(db2.getId())
                                .itemId(sales.getId())
                                .fieldName("SaleAmount")
                                .build())
                        .build()))
            .build();

    return List.of(customers, orders, sales, salesPerMonth);
  }
}
