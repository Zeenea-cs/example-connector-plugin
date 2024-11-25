package zeenea.connector.example;

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
import zeenea.connector.example.file.FileItem;
import zeenea.connector.example.json.Customizable;
import zeenea.connector.example.json.JsonContact;
import zeenea.connector.example.json.JsonField;
import zeenea.connector.example.json.JsonItem;
import zeenea.connector.example.json.JsonItemRef;
import zeenea.connector.example.json.JsonProcess;
import zeenea.connector.example.json.WithFields;
import zeenea.connector.example.property.CustomProperties;
import zeenea.connector.example.property.CustomProperty;
import zeenea.connector.field.Field;
import zeenea.connector.process.DataProcess;
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

  public static ItemIdentifier parseItemId(String id) {
    return ItemIdentifier.of(
        ID_SEP
            .splitAsStream(id)
            .filter(Predicate.not(String::isEmpty))
            .map(ExampleMapper::parseIdProperty)
            .collect(Collectors.toList()));
  }

  public static List<String> parseItemLabels(String id) {
    return ID_SEP
        .splitAsStream(id)
        .filter(Predicate.not(String::isEmpty))
        .map(ExampleMapper::parseIdProperty)
        .map(p -> p.getKey() + "=" + p.getValue())
        .collect(Collectors.toList());
  }

  public static IdentificationProperty parseIdProperty(String property) {
    var index = property.indexOf('=');
    if (index > 0) {
      return IdentificationProperty.of(property.substring(0, index), property.substring(index + 1));
    } else {
      return IdentificationProperty.of(DEFAULT_KEY, index == 0 ? property.substring(1) : property);
    }
  }

  public static ItemIdentifier fieldId(String name) {
    return ItemIdentifier.of(IdentificationProperty.of(FIELD_KEY, name));
  }

  public static DataProcess dataProcess(
      FileItem<JsonProcess> fileItem, CustomProperties customProperties) {
    var process = fileItem.getItem();
    return DataProcess.builder()
        .id(parseItemId(process.getId()))
        .name(process.getName())
        .description(process.getDescription())
        .properties(properties(fileItem, customProperties))
        .contacts(contacts(process))
        .sources(sources(process))
        .targets(list(process.getTargets(), ExampleMapper::itemReference))
        .build();
  }

  public static List<Contact> contacts(JsonItem item) {
    return list(item.getContacts(), ExampleMapper::contact);
  }

  public static List<ItemReference> sources(JsonItem item) {
    return list(item.getSources(), ExampleMapper::itemReference);
  }

  public static List<Field> fields(WithFields item, CustomProperties customProperties) {
    var list = new ArrayList<Field>();
    int fieldIdx = 0;
    for (JsonField field : item.getFields()) {
      var properties =
          customProperties(PropertiesBuilder.create(), field, customProperties).build();

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

  public static Map<String, PropertyValue> properties(
      FileItem<? extends JsonItem> fileItem, CustomProperties customProperties) {
    var item = fileItem.getItem();
    PropertiesBuilder properties =
        PropertiesBuilder.create().put(Metadata.PATH_MD, fileItem.getFileRef().getRelativePath());
    return customProperties(properties, item, customProperties).build();
  }

  private static PropertiesBuilder customProperties(
      PropertiesBuilder builder, Customizable item, CustomProperties customProperties) {
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
                //  TODO log at warn level
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
                // TODO log at warn level
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
                // TODO log at warn level
              }
            } else if (value.isObject()) {
              var uriPath = value.path("uri");
              if (uriPath.isTextual()) {
                try {
                  uri = new URI(value.textValue());
                } catch (URISyntaxException e) {
                  // TODO log at warn level
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

  private static <E, R> List<R> list(List<E> list, Function<? super E, ? extends R> elementMapper) {
    return list.stream().map(elementMapper).collect(Collectors.toList());
  }

  private static ItemReference itemReference(JsonItemRef itemRef) {
    ConnectionReference connectionRef;
    if (itemRef.getConnectionCode() != null) {
      connectionRef = ConnectionReferenceCode.of(itemRef.getConnectionCode());
    } else if (itemRef.getConnectionAlias() != null) {
      connectionRef = ConnectionReferenceAlias.of(itemRef.getConnectionAlias());
    } else {
      connectionRef = null;
    }
    var id = parseItemId(itemRef.getId());
    return ItemReference.of(id, connectionRef);
  }

  private static Contact contact(JsonContact contact) {
    return Contact.builder()
        .role(contact.getRole())
        .email(contact.getEmail())
        .name(contact.getName())
        .phoneNumber(contact.getPhone())
        .build();
  }

  private static DataType dataType(String type) {
    switch (type.toLowerCase(Locale.ROOT)) {
      case "boolean":
        return DataType.Boolean;
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
      case "string":
        return DataType.String;
      case "date":
        return DataType.Date;
      case "timestamp":
        return DataType.Timestamp;
      case "binary":
        return DataType.Binary;
      case "struct":
        return DataType.Struct;
      default:
        return DataType.Unknown;
    }
  }
}
