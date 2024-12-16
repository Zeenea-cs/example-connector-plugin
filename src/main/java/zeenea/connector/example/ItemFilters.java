/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.regex.Pattern;
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

  private static final Pattern INVALID_PROPERTY_CHAR = Pattern.compile("[^_a-zA-Z0-9]");

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
    return FilterItem.of(kvList);
  }

  public static FilterItem fileItem(FileRef f) {
    return FilterItem.of(FilterKeyValue.text(PATH_KEY, f.getRelativePath()));
  }

  private static void addCustomProperties(
      CustomProperties customProperties, HashSet<FilterKey> filterKeys) {
    for (CustomProperty property : customProperties.getProperties()) {
      String propertyCode = filterKeyName(property.getCode());
      switch (property.getType()) {
        case STRING:
        case NUMBER:
        case INSTANT:
        case LONG_TEXT:
          filterKeys.add(FilterKey.text(propertyCode));
          break;
        case TAG:
          filterKeys.add(FilterKey.list(propertyCode));
          break;
        case URL:
          filterKeys.add(FilterKey.text(propertyCode + "_url"));
          filterKeys.add(FilterKey.text(propertyCode + "_label"));
          break;
      }
    }
  }

  private static void addCustomPropertiesValues(
      JsonItem item, CustomProperties customProperties, ArrayList<FilterKeyValue> kvList) {
    for (CustomProperty property : customProperties.getProperties()) {
      String propertyCode = filterKeyName(property.getCode());
      var value = item.getCustomProperty(property.getAttributeName());
      if (value != null && !value.isNull() && !value.isMissingNode()) {
        switch (property.getType()) {
          case STRING:
          case NUMBER:
          case INSTANT:
          case LONG_TEXT:
            kvList.add(FilterKeyValue.text(FilterKey.text(propertyCode), value.asText()));
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

  /**
   * Because custom properties can have code which are invalid as a filter key, this method tranform
   * them in a valid key.
   *
   * <p>Transformation list:
   *
   * <ol>
   *   <li>Any invalid character is replaced by an underscore.
   *   <li>If the first character is a digit, an undescore is added at the begin of the key.
   * </ol>
   *
   * @param code Custom property code.
   * @return A valid filter key.
   */
  private static String filterKeyName(String code) {
    if (code.isEmpty()) return "_";
    var matcher = INVALID_PROPERTY_CHAR.matcher(code);
    var filterKey = matcher.replaceAll("_");
    if (Character.isDigit(filterKey.charAt(0))) {
      filterKey = "_" + filterKey;
    }
    return filterKey;
  }
}
