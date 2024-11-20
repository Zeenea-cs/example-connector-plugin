package zeenea.connector.example.filter;

import static zeenea.connector.example.filter.RawFilterParserConstants.KEY;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

final class FilterKeyDictionary {
  private final Map<String, FilterKey> keys;
  private final LinkedList<FilterKey> localKeys = new LinkedList<>();

  private FilterKeyDictionary(Map<String, FilterKey> keys) {
    this.keys = keys;
  }

  static FilterKeyDictionary of(Set<FilterKey> keys) {
    return new FilterKeyDictionary(
        keys.stream().collect(Collectors.toMap(FilterKey::name, Function.identity())));
  }

  static FilterKeyDictionary of(FilterKey... keys) {
    return of(Set.of(keys));
  }

  private @Nullable FilterKey getKey(String name) {
    return localKeys.stream()
        .filter(k -> k.name().equals(name))
        .findFirst()
        .orElseGet(() -> keys.get(name));
  }

  public void checkKeyIsValid(Token token) throws FilterParsingException {
    if (token.kind != KEY) {
      throw invalidToken(token, "not a key");
    }
    var key = getKey(token.keyName);
    if (key == null) {
      throw invalidToken(token, "not found");
    }
    token.key = key;
  }

  public void checkKeyIsValid(Token token, FilterKind kind) throws FilterParsingException {
    if (token.kind != KEY) {
      throw invalidToken(token, "not a key");
    }
    var key = getKey(token.keyName);
    if (key == null) {
      throw invalidToken(token, "not found");
    }
    if (!kind.accepts(key.kind()))
      throw invalidToken(
          token, "kind '" + key.kind() + "' don't matches expected kind '" + kind + "'");
    token.key = key;
  }

  private FilterParsingException invalidToken(Token token, String cause) {
    var msg = new StringBuilder();
    msg.append("Invalid key (");
    msg.append(cause);
    msg.append(") \"");
    msg.append(token.image);
    msg.append("\" at line ");
    msg.append(token.beginLine);
    msg.append(", column ");
    msg.append(token.beginColumn);
    msg.append(". Expected values {");
    msg.append(keys.keySet().stream().sorted().collect(Collectors.joining(", ")));
    msg.append("}");
    return new FilterParsingException(msg.toString());
  }

  public void declareLocalKey(Token element) throws FilterParsingException {
    FilterKey elementKey = FilterKey.text(element.image);
    localKeys.push(elementKey);
    element.key = elementKey;
  }
}
