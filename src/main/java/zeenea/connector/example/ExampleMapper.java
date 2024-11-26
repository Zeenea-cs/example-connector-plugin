package zeenea.connector.example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import zeenea.connector.common.ConnectionReference;
import zeenea.connector.common.ConnectionReferenceAlias;
import zeenea.connector.common.ConnectionReferenceCode;
import zeenea.connector.common.IdentificationProperty;
import zeenea.connector.common.ItemIdentifier;
import zeenea.connector.common.ItemReference;
import zeenea.connector.contact.Contact;
import zeenea.connector.dataset.DataType;
import zeenea.connector.dataset.ForeignKey;
import zeenea.connector.example.file.FileItem;
import zeenea.connector.example.json.Customizable;
import zeenea.connector.example.json.JsonContact;
import zeenea.connector.example.json.JsonField;
import zeenea.connector.example.json.JsonForeignKey;
import zeenea.connector.example.json.JsonItem;
import zeenea.connector.example.json.JsonItemRef;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.TracingContext;
import zeenea.connector.example.property.CustomProperties;
import zeenea.connector.example.property.CustomProperty;
import zeenea.connector.field.Field;
import zeenea.connector.property.InstantPropertyDefinition;
import zeenea.connector.property.NumberPropertyDefinition;
import zeenea.connector.property.PropertiesBuilder;
import zeenea.connector.property.PropertyValue;
import zeenea.connector.property.StringPropertyDefinition;
import zeenea.connector.property.TagPropertyDefinition;
import zeenea.connector.property.UrlPropertyDefinition;

/**
 * This class contains the object mapping. This is often done in the connection object but, because
 * they are reused by the three connections, we put them in a separate class.
 */
public class ExampleMapper {
  /** Item identifier segments. */
  private static final Pattern ID_SEP = Pattern.compile("/");

  private static final String DEFAULT_KEY = "id";
  private static final String FIELD_KEY = "field";

  private static final SimpleLogger log = SimpleLogger.of(ExampleMapper.class);

  private final String currentConnectionCode;

  public ExampleMapper(String currentConnectionCode) {
    this.currentConnectionCode = currentConnectionCode;
  }

  public ItemIdentifier parseItemId(String id) {
    return ItemIdentifier.of(
        ID_SEP
            .splitAsStream(id)
            .filter(Predicate.not(String::isEmpty))
            .map(this::parseIdProperty)
            .collect(Collectors.toList()));
  }

  public List<String> parseItemLabels(String id) {
    return ID_SEP
        .splitAsStream(id)
        .filter(Predicate.not(String::isEmpty))
        .map(this::parseIdProperty)
        .map(IdentificationProperty::getValue)
        .collect(Collectors.toList());
  }

  public IdentificationProperty parseIdProperty(String property) {
    var index = property.indexOf('=');
    if (index > 0) {
      return IdentificationProperty.of(property.substring(0, index), property.substring(index + 1));
    } else {
      return IdentificationProperty.of(DEFAULT_KEY, index == 0 ? property.substring(1) : property);
    }
  }

  public ItemIdentifier fieldId(String name) {
    return ItemIdentifier.of(IdentificationProperty.of(FIELD_KEY, name));
  }

  public List<Contact> contacts(JsonItem item) {
    return list(item.getContacts(), this::contact);
  }

  public List<ItemReference> itemReferences(List<JsonItemRef> refList) {
    return list(refList, this::itemReference);
  }

  public List<Field> fields(
      TracingContext ctx, List<JsonField> fields, CustomProperties customProperties) {
    var list = new ArrayList<Field>();
    int fieldIdx = 0;
    for (JsonField field : fields) {
      var properties =
          customProperties(ctx, PropertiesBuilder.create(), field, customProperties).build();

      /*
       * Get native type and data type.
       * If one value is not set, it takes the value of the other.
       * If none are set, both have value "unknown".
       */
      var nativeType = field.getNativeType();
      if (nativeType == null) {
        nativeType = field.getDataType();
        if (nativeType == null) {
          nativeType = "unknown";
        }
      }
      var dataType = field.getDataType();
      if (dataType == null) {
        dataType = nativeType;
      }

      list.add(
          Field.builder()
              .identifier(fieldId(field.getName()))
              .name(field.getName())
              .description(field.getDescription())
              .nativeIndex(fieldIdx++)
              .nativeType(nativeType)
              .dataType(dataType(dataType))
              .nullable(field.isNullable())
              .multivalued(field.isMultivalued())
              .properties(properties)
              .build());
    }
    return list;
  }

  public Map<String, PropertyValue> properties(
      TracingContext ctx,
      FileItem<? extends JsonItem> fileItem,
      CustomProperties customProperties) {
    var item = fileItem.getItem();
    PropertiesBuilder properties =
        PropertiesBuilder.create().put(Metadata.PATH_MD, fileItem.getFileRef().getRelativePath());
    return customProperties(ctx, properties, item, customProperties).build();
  }

  private PropertiesBuilder customProperties(
      TracingContext ctx,
      PropertiesBuilder builder,
      Customizable item,
      CustomProperties customProperties) {
    for (CustomProperty property : customProperties.getProperties()) {
      var value = item.getCustomProperty(property.getAttributeName());
      if (value != null && !value.isNull() && !value.isMissingNode()) {
        switch (property.getType()) {
          case STRING:
          case LONG_TEXT:
            builder.put((StringPropertyDefinition) property.getDefinition(), value.asText());
            break;

          case TAG:
            if (value.isTextual()) {
              builder.put(
                  (TagPropertyDefinition) property.getDefinition(), List.of(value.asText()));
            } else if (value.isArray()) {
              var values = new ArrayList<String>();
              for (var i = value.elements(); i.hasNext(); ) {
                values.add(i.next().asText());
              }
              builder.put((TagPropertyDefinition) property.getDefinition(), values);
            }
            break;

          case NUMBER:
            if (value.isNumber()) {
              builder.put(
                  (NumberPropertyDefinition) property.getDefinition(), value.decimalValue());
            } else if (value.isTextual()) {
              try {
                builder.put(
                    (NumberPropertyDefinition) property.getDefinition(),
                    new BigDecimal(value.textValue()));
              } catch (NumberFormatException e) {
                log.entry("example_mapper_invalid_number")
                    .with("property_code", property.getCode())
                    .with("json_attribute_name", property.getAttributeName())
                    .with("value", value.textValue())
                    .quiet()
                    .warn(e);
              }
            }
            break;

          case INSTANT:
            if (value.isTextual()) {
              try {
                builder.put(
                    (InstantPropertyDefinition) property.getDefinition(),
                    Instant.parse(value.textValue()));
              } catch (DateTimeParseException e) {
                log.entry("example_mapper_invalid_instant")
                    .with("property_code", property.getCode())
                    .with("json_attribute_name", property.getAttributeName())
                    .with("value", value.textValue())
                    .quiet()
                    .warn(e);
              }
            }
            break;

          case URL:
            URI uri = null;
            String label = null;

            if (value.isTextual()) {
              try {
                uri = new URI(value.textValue());
              } catch (URISyntaxException e) {
                log.entry("example_mapper_invalid_uri")
                    .with("property_code", property.getCode())
                    .with("json_attribute_name", property.getAttributeName())
                    .with("value", value.textValue())
                    .quiet()
                    .warn(e);
              }
            } else if (value.isObject()) {
              var uriPath = value.path("uri");
              if (uriPath.isTextual()) {
                try {
                  uri = new URI(value.textValue());
                } catch (URISyntaxException e) {
                  log.entry("example_mapper_invalid_uri")
                      .with("property_code", property.getCode())
                      .with("json_attribute_name", property.getAttributeName())
                      .with("value", value.textValue())
                      .quiet()
                      .warn(e);
                }
              }
              var labelPath = value.path("label");
              if (labelPath.isTextual()) {
                label = labelPath.textValue();
              }
            }
            if (uri != null) {
              builder.put((UrlPropertyDefinition) property.getDefinition(), uri, label);
            }
            break;
        }
      }
    }
    return builder;
  }

  // Wait for a fix of zeenea.connector.dataset.ForeignKey.Builder#build visibility.
  private static final Method BUILD_FK;

  static {
    try {
      BUILD_FK = ForeignKey.Builder.class.getDeclaredMethod("build");
      BUILD_FK.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public List<ForeignKey> foreignKeys(List<JsonForeignKey> foreignKeys) {
    // Wait for a fix of zeenea.connector.dataset.ForeignKey.Builder#build visibility.
    return list(
        foreignKeys,
        fk -> {
          try {
            return (ForeignKey)
                BUILD_FK.invoke(
                    ForeignKey.builder()
                        .name(fk.getName())
                        .targetDataset(fk.getTargetDataset())
                        .targetFields(fk.getTargetFields())
                        .sourceFields(fk.getSourceFields()));
          } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
          }
        });
  }

  private <E, R> List<R> list(List<E> list, Function<? super E, ? extends R> elementMapper) {
    return list.stream().map(elementMapper).collect(Collectors.toList());
  }

  private ItemReference itemReference(JsonItemRef itemRef) {
    ConnectionReference connectionRef;
    if (itemRef.getConnectionCode() != null) {
      if ("current_connection".equals(itemRef.getConnectionCode())) {
        connectionRef = ConnectionReferenceCode.of(currentConnectionCode);
      } else {
        connectionRef = ConnectionReferenceCode.of(itemRef.getConnectionCode());
      }
    } else if (itemRef.getConnectionAlias() != null) {
      connectionRef = ConnectionReferenceAlias.of(itemRef.getConnectionAlias());
    } else {
      connectionRef = null;
    }
    var id = parseItemId(itemRef.getId());
    return ItemReference.of(id, connectionRef);
  }

  private Contact contact(JsonContact contact) {
    return Contact.builder()
        .role(contact.getRole())
        .email(contact.getEmail())
        .name(contact.getName())
        .phoneNumber(contact.getPhone())
        .build();
  }

  private DataType dataType(String type) {
    switch (type.toLowerCase(Locale.ROOT)) {
      case "string":
        return DataType.String;
      case "byte":
        return DataType.Byte;
      case "short":
        return DataType.Short;
      case "integer":
        return DataType.Integer;
      case "long":
        return DataType.Long;
      case "float":
        return DataType.Float;
      case "double":
        return DataType.Double;
      case "boolean":
        return DataType.Boolean;
      case "date":
        return DataType.Date;
      case "time":
        return DataType.Time;
      case "timestamp":
        return DataType.Timestamp;
      case "binary":
        return DataType.Binary;
      case "bigdecimal":
        return DataType.BigDecimal;
      case "geopoint":
        return DataType.GeoPoint;
      case "geoshape":
        return DataType.GeoShape;
      case "struct":
        return DataType.Struct;
      case "map":
        return DataType.Map;
      case "null":
        return DataType.Null;
      default:
        return DataType.Unknown;
    }
  }
}
