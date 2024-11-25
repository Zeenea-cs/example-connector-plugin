package zeenea.connector.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.example.file.FileItem;
import zeenea.connector.example.file.FileRef;
import zeenea.connector.example.filter.Filter;
import zeenea.connector.example.filter.FilterItem;
import zeenea.connector.example.filter.FilterKey;
import zeenea.connector.example.filter.FilterKeyValue;
import zeenea.connector.example.filter.FilterParser;
import zeenea.connector.example.filter.FilterParsingException;
import zeenea.connector.example.json.JsonItem;
import zeenea.connector.example.property.CustomProperties;
import zeenea.connector.example.property.CustomProperty;
import zeenea.connector.exception.InvalidConfigurationException;

public class ItemFilters {
  public static final FilterKey ID_KEY = FilterKey.text("id");
  public static final FilterKey NAME_KEY = FilterKey.text("name");
  public static final FilterKey PATH_KEY = FilterKey.text("path");

  public static Filter parseFilter(
      ConnectionConfiguration configuration, CustomProperties customProperties) {
    Optional<String> filterString = configuration.getStringOptional(Config.FILTER_CONF);
    if (filterString.isEmpty() || filterString.get().isBlank()) return Filter.always();
    var filterKeys = new HashSet<FilterKey>();
    filterKeys.add(ID_KEY);
    filterKeys.add(NAME_KEY);
    filterKeys.add(PATH_KEY);
    addCustomProperties(customProperties, filterKeys);
    try {
      return FilterParser.of(filterKeys).parse(filterString.get());
    } catch (FilterParsingException e) {
      throw new InvalidConfigurationException("Invalid filter value: " + e.getMessage(), e);
    }
  }

  public static Filter fileFilter(Filter filter) {
    return filter.partial(ItemFilters.PATH_KEY);
  }

  public static FilterItem item(
      FileItem<? extends JsonItem> fileItem, CustomProperties customProperties) {
    var kvList = new ArrayList<FilterKeyValue>();
    var item = fileItem.getItem();
    kvList.add(FilterKeyValue.text(ID_KEY, item.getId()));
    kvList.add(FilterKeyValue.text(NAME_KEY, item.getName()));
    kvList.add(FilterKeyValue.text(PATH_KEY, fileItem.getFileRef().getRelativePath()));
    addCustomPropertiesValues(item, customProperties, kvList);
    return FilterItem.of();
  }

  public static FilterItem fileItem(FileRef f) {
    return FilterItem.of(FilterKeyValue.text(PATH_KEY, f.getRelativePath()));
  }

  private static void addCustomProperties(
      CustomProperties customProperties, HashSet<FilterKey> filterKeys) {
    for (CustomProperty property : customProperties.getProperties()) {
      String propertyCode = property.getCode();
      switch (property.getType()) {
        case STRING:
        case NUMBER:
        case INSTANT:
        case LONG_TEXT:
          try {
            filterKeys.add(FilterKey.text(propertyCode));
          } catch (IllegalArgumentException e) {
            // TODO log at warn level
          }
          break;
        case TAG:
          try {
            filterKeys.add(FilterKey.list(propertyCode));
          } catch (IllegalArgumentException e) {
            // TODO log at warn level
          }
          break;
        case URL:
          try {
            filterKeys.add(FilterKey.text(propertyCode + "_url"));
            filterKeys.add(FilterKey.text(propertyCode + "_label"));
          } catch (IllegalArgumentException e) {
            // TODO log at warn level
          }
          break;
      }
    }
  }

  private static void addCustomPropertiesValues(
      JsonItem item, CustomProperties customProperties, ArrayList<FilterKeyValue> kvList) {
    for (CustomProperty property : customProperties.getProperties()) {
      String propertyCode = property.getCode();
      var value = item.getCustomProperty(property.getAttributeName());
      if (value != null && !value.isNull() && !value.isMissingNode()) {
        switch (property.getType()) {
          case STRING:
          case NUMBER:
          case INSTANT:
          case LONG_TEXT:
            try {
              kvList.add(FilterKeyValue.text(FilterKey.text(propertyCode), value.asText()));
            } catch (IllegalArgumentException e) {
              // TODO log at warn level
            }
            break;

          case TAG:
            var values = new ArrayList<String>();
            if (value.isTextual()) {
              values.add(value.asText());
            } else if (value.isArray()) {
              for (var i = value.elements(); i.hasNext(); ) {
                values.add(i.next().asText());
              }
            }
            kvList.add(FilterKeyValue.textList(FilterKey.list(propertyCode), values));
            break;

          case URL:
            String uri = null;
            String label = null;
            if (value.isTextual()) {
              uri = value.textValue();
            } else if (value.isObject()) {
              var uriPath = value.path("uri");
              if (uriPath.isTextual()) {
                uri = value.textValue();
              }
              var labelPath = value.path("label");
              if (labelPath.isTextual()) {
                label = labelPath.textValue();
              }
            }
            kvList.add(FilterKeyValue.text(FilterKey.text(propertyCode + "_url"), uri));
            kvList.add(FilterKeyValue.text(FilterKey.text(propertyCode + "_label"), label));
            break;
        }
      }
    }
  }
}
