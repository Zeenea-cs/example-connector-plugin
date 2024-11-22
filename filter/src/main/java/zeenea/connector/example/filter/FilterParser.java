package zeenea.connector.example.filter;

import java.util.Set;

public class FilterParser {
  private final FilterKeyDictionary keyDictionary;

  private FilterParser(Set<FilterKey> keySet) {
    this.keyDictionary = FilterKeyDictionary.of(keySet);
  }

  public static FilterParser of(FilterKey... keys) {
    return new FilterParser(Set.of(keys));
  }

  public static FilterParser of(Set<FilterKey> keySet) {
    return new FilterParser(keySet);
  }

  public Filter parse(String filterSpec) {
    try {
      if (filterSpec == null || filterSpec.isEmpty()) return Filter.always();
      RawFilterParser rawParser = new RawFilterParser(filterSpec);
      rawParser.keyDic = keyDictionary;
      return rawParser.filter();
    } catch (TokenMgrException | ParseException e) {
      throw new FilterParsingException(e.getMessage(), e);
    }
  }
}
