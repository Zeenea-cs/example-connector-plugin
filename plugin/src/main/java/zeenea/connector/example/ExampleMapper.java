package zeenea.connector.example;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
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
import zeenea.connector.example.file.FileItem;
import zeenea.connector.example.file.FileRef;
import zeenea.connector.example.json.JsonContact;
import zeenea.connector.example.json.JsonItem;
import zeenea.connector.example.json.JsonItemRef;
import zeenea.connector.example.json.JsonProcess;
import zeenea.connector.example.property.CustomProperties;
import zeenea.connector.example.property.CustomProperty;
import zeenea.connector.process.DataProcess;
import zeenea.connector.property.InstantPropertyDefinition;
import zeenea.connector.property.NumberPropertyDefinition;
import zeenea.connector.property.PropertiesBuilder;
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

  public static ItemIdentifier parseItemId(String id) {
    return ItemIdentifier.of(
        ID_SEP
            .splitAsStream(id)
            .filter(Predicate.not(String::isEmpty))
            .map(ExampleMapper::parseIdProperty)
            .collect(Collectors.toList()));
  }

  public static IdentificationProperty parseIdProperty(String property) {
    var index = property.indexOf('=');
    if (index > 0) {
      return IdentificationProperty.of(property.substring(0, index), property.substring(index + 1));
    } else {
      return IdentificationProperty.of("id", index == 0 ? property.substring(1) : property);
    }
  }

  public static DataProcess dataProcess(
      FileItem<JsonProcess> fileItem, CustomProperties customProperties) {
    PropertiesBuilder properties =
        PropertiesBuilder.create().put(Metadata.PATH_MD, fileItem.getFileRef().getRelativePath());
    var process = fileItem.getItem();
    customProperties(process, properties, customProperties);

    return DataProcess.builder()
        .id(parseItemId(process.getId()))
        .name(process.getName())
        .description(process.getDescription())
        .properties(properties.build())
        .contacts(list(process.getContacts(), ExampleMapper::contact))
        .sources(list(process.getSources(), ExampleMapper::itemReference))
        .targets(list(process.getTargets(), ExampleMapper::itemReference))
        .build();
  }

  private static void customProperties(
      JsonItem item, PropertiesBuilder propertiesBuilder, CustomProperties customProperties) {
    for (CustomProperty property : customProperties.getProperties()) {
      var value = item.getCustomField(property.getAttributeName());
      if (value != null && !value.isNull() && !value.isMissingNode()) {
        switch (property.getType()) {
          case STRING:
          case LONG_TEXT:
            propertiesBuilder.put(
                (StringPropertyDefinition) property.getDefinition(), value.asText());
            break;

          case TAG:
            if (value.isTextual()) {
              propertiesBuilder.put(
                  (TagPropertyDefinition) property.getDefinition(), List.of(value.asText()));
            } else if (value.isArray()) {
              var values = new ArrayList<String>();
              for (var i = value.elements(); i.hasNext(); ) {
                values.add(i.next().asText());
              }
              propertiesBuilder.put((TagPropertyDefinition) property.getDefinition(), values);
            }
            break;

          case NUMBER:
            if (value.isNumber()) {
              propertiesBuilder.put(
                  (NumberPropertyDefinition) property.getDefinition(), value.decimalValue());
            } else if (value.isTextual()) {
              try {
                propertiesBuilder.put(
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
                propertiesBuilder.put(
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
              propertiesBuilder.put((UrlPropertyDefinition) property.getDefinition(), uri, label);
            }
            break;
        }
      }
    }
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
}
