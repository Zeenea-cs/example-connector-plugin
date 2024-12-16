/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import zeenea.connector.example.filter.Filter.MatchesGlob;
import zeenea.connector.example.filter.Filter.MatchesRegex;

public class FilterParserTest {

  private static final FilterKey projectKey = FilterKey.text("project");
  private static final FilterKey tableKey = FilterKey.text("table");
  private static final FilterKey tagsKey = FilterKey.list("tags");
  private static final FilterKey tagKey = FilterKey.text("tag");

  private static final FilterParser parser = FilterParser.of(Set.of(projectKey, tableKey, tagsKey));

  @Test
  @DisplayName("parse should parse always filter")
  void parseShouldParseAlwaysFilter() {
    var actual = parser.parse("always");
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("parse should parse never filter")
  void parseShouldParseNeverFilter() {
    var actual = parser.parse("never");
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("parse should parse = filter")
  void parseShouldParseIsEqualToFilter() {
    var actual = parser.parse("project = 'zeenea'");
    assertEquals(Filter.isEqualTo(projectKey, "zeenea"), actual);
  }

  @Test
  @DisplayName("parse should parse = filter with quotes and backslashes")
  void parseShouldParseIsEqualToWithQuotesAndBackSlashesFilter() {
    var actual = parser.parse("project = 'text\\'quote\\\\'");
    assertEquals(Filter.isEqualTo(projectKey, "text'quote\\"), actual);
  }

  @Test
  @DisplayName("parse should parse != filter")
  void parseShouldParseNotEqualToFilter() {
    var actual = parser.parse("project != 'zeenea'");
    assertEquals(Filter.not(Filter.isEqualTo(projectKey, "zeenea")), actual);
  }

  @Test
  @DisplayName("parse should parse is null filter")
  void parseShouldParseIsNullFilter() {
    var actual = parser.parse("project is null");
    assertEquals(Filter.isNull(projectKey), actual);
  }

  @Test
  @DisplayName("parse should parse is not null filter")
  void parseShouldParseIsNotNullFilter() {
    var actual = parser.parse("project is not null");
    assertEquals(Filter.not(Filter.isNull(projectKey)), actual);
  }

  @Test
  @DisplayName("parse should parse starts with filter")
  void parseShouldParseStartsWithFilter() {
    var actual = parser.parse("project starts with 'zeenea'");
    assertEquals(Filter.startsWith(projectKey, "zeenea"), actual);
  }

  @Test
  @DisplayName("parse should parse ends with filter")
  void parseShouldParseEndsWithFilter() {
    var actual = parser.parse("project ends with 'zeenea'");
    assertEquals(Filter.endsWith(projectKey, "zeenea"), actual);
  }

  @Test
  @DisplayName("parse should parse contains filter")
  void parseShouldParseContainsFilter() {
    var actual = parser.parse("project contains 'zeenea'");
    assertEquals(Filter.contains(projectKey, "zeenea"), actual);
  }

  @Test
  @DisplayName("parse should parse in filter")
  void parseShouldParseInFilter() {
    var actual = parser.parse("project in ('zeenea', 'datacruncher')");
    assertEquals(Filter.in(projectKey, "zeenea", "datacruncher"), actual);
  }

  @Test
  @DisplayName("parse should parse not in filter")
  void parseShouldParseNotInFilter() {
    var actual = parser.parse("project not in ('zeenea', 'datacruncher')");
    assertEquals(Filter.not(Filter.in(projectKey, "zeenea", "datacruncher")), actual);
  }

  @Test
  @DisplayName("parse should parse not filter")
  void parseShouldParseNotFilter() {
    var actual = parser.parse("not project = 'zeenea'");
    assertEquals(Filter.not(Filter.isEqualTo(projectKey, "zeenea")), actual);
  }

  @Test
  @DisplayName("parse should parse not not filter")
  void parseShouldParseNotNotFilter() {
    var actual = parser.parse("not not project = 'zeenea'");
    assertEquals(Filter.isEqualTo(projectKey, "zeenea"), actual);
  }

  @Test
  @DisplayName("parse should parse ! filter")
  void parseShouldParseExclamFilter() {
    var actual = parser.parse("! project = 'zeenea'");
    assertEquals(Filter.not(Filter.isEqualTo(projectKey, "zeenea")), actual);
  }

  @Test
  @DisplayName("parse should parse ! ! filter")
  void parseShouldParseDoubleExclamFilter() {
    var actual = parser.parse("!! project = 'zeenea'");
    assertEquals(Filter.isEqualTo(projectKey, "zeenea"), actual);
  }

  @Test
  @DisplayName("parse should parse matches glob filter")
  void parseShouldParseMatchesGlobFilter() {
    var actual = parser.parse("project ~ 'zeenea*'");
    assertThat(actual).isInstanceOf(MatchesGlob.class);
    MatchesGlob glob = (MatchesGlob) actual;
    assertThat(glob.key).isEqualTo(projectKey);
    assertThat(glob.glob()).isEqualTo("zeenea*");
    assertThat(glob.pattern().pattern()).isEqualTo("zeenea.*");
  }

  @Test
  @DisplayName("parse should parse matches regex filter")
  void parseShouldParseMatchesRegexFilter() {
    var actual = parser.parse("project ~ /zeenea.*/");
    assertThat(actual).isInstanceOf(MatchesRegex.class);
    MatchesRegex regex = (MatchesRegex) actual;
    assertThat(regex.key).isEqualTo(projectKey);
    assertThat(regex.pattern().pattern()).isEqualTo("zeenea.*");
    assertThat(regex.pattern().flags() & Pattern.CASE_INSENSITIVE).isZero();
    assertThat(regex.pattern().flags() & Pattern.COMMENTS).isZero();
  }

  @Test
  @DisplayName("parse should parse matches regex filter with options")
  void parseShouldParseMatchesRegexFilterWithOptions() {
    var actual = parser.parse("project ~ / zeenea\n#Project starting with zeenea\n\\w* /ix");
    assertThat(actual).isInstanceOf(MatchesRegex.class);
    MatchesRegex regex = (MatchesRegex) actual;
    assertThat(regex.key).isEqualTo(projectKey);
    assertThat(regex.pattern().pattern()).isEqualTo("zeenea\n#Project starting with zeenea\n\\w*");
    assertThat(regex.pattern().flags() & Pattern.CASE_INSENSITIVE)
        .isEqualTo(Pattern.CASE_INSENSITIVE);
    assertThat(regex.pattern().flags() & Pattern.COMMENTS).isEqualTo(Pattern.COMMENTS);
    assertTrue(
        regex.matches(
            FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.text("ZeeneaPlus")))));
    assertFalse(
        regex.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.text("Aeneez")))));
  }

  @Test
  @DisplayName("parse should parse and filter")
  void parseShouldParseAndFilter() {
    var actual = parser.parse("project != 'system' and table = 'zeenea_db'");
    assertEquals(
        Filter.and(
            Filter.not(Filter.isEqualTo(projectKey, "system")),
            Filter.isEqualTo(tableKey, "zeenea_db")),
        actual);
  }

  @Test
  @DisplayName("parse should parse and ... and filter")
  void parseShouldParseAndAndFilter() {
    var actual = parser.parse("project != 'system' and project != 'toto' and table = 'zeenea_db'");
    assertEquals(
        Filter.and(
            Filter.not(Filter.isEqualTo(projectKey, "system")),
            Filter.and(
                Filter.not(Filter.isEqualTo(projectKey, "toto")),
                Filter.isEqualTo(tableKey, "zeenea_db"))),
        actual);
  }

  @Test
  @DisplayName("parse should parse or filter")
  void parseShouldParseOrFilter() {
    var actual = parser.parse("project != 'zeenea' or project = 'datacruncher'");
    assertEquals(
        Filter.or(
            Filter.not(Filter.isEqualTo(projectKey, "zeenea")),
            Filter.isEqualTo(projectKey, "datacruncher")),
        actual);
  }

  @Test
  @DisplayName("parse should parse or ... or filter")
  void parseShouldParseOrOrFilter() {
    var actual =
        parser.parse("project != 'zeenea' or project != 'datacruncher' or project = 'toto'");
    assertEquals(
        Filter.or(
            Filter.not(Filter.isEqualTo(projectKey, "zeenea")),
            Filter.or(
                Filter.not(Filter.isEqualTo(projectKey, "datacruncher")),
                Filter.isEqualTo(projectKey, "toto"))),
        actual);
  }

  @Test
  @DisplayName("parse should parse or & and filter")
  void parseShouldParseOrAndFilter() {
    var actual =
        parser.parse("project != 'zeenea' or project != 'datacruncher' and table = 'foobar'");
    assertEquals(
        Filter.or(
            Filter.not(Filter.isEqualTo(projectKey, "zeenea")),
            Filter.and(
                Filter.not(Filter.isEqualTo(projectKey, "datacruncher")),
                Filter.isEqualTo(tableKey, "foobar"))),
        actual);
  }

  @Test
  @DisplayName("parse should parse and & or filter")
  void parseShouldParseAndOrFilter() {
    var actual =
        parser.parse("project != 'zeenea' and project != 'datacruncher' or table = 'foobar'");
    assertEquals(
        Filter.or(
            Filter.and(
                Filter.not(Filter.isEqualTo(projectKey, "zeenea")),
                Filter.not(Filter.isEqualTo(projectKey, "datacruncher"))),
            Filter.isEqualTo(tableKey, "foobar")),
        actual);
  }

  @Test
  @DisplayName("parse should parse '&&' & '||' filter")
  void parseShouldParseAndOrSymFilter() {
    var actual =
        parser.parse("project != 'zeenea' && project != 'datacruncher' || table = 'foobar'");
    assertEquals(
        Filter.or(
            Filter.and(
                Filter.not(Filter.isEqualTo(projectKey, "zeenea")),
                Filter.not(Filter.isEqualTo(projectKey, "datacruncher"))),
            Filter.isEqualTo(tableKey, "foobar")),
        actual);
  }

  @Test
  @DisplayName("parse should parse (or) & and filter")
  void parseShouldParseParenOrAndFilter() {
    var actual =
        parser.parse("(project != 'zeenea' or project != 'datacruncher') and table = 'foobar'");
    assertEquals(
        Filter.and(
            Filter.or(
                Filter.not(Filter.isEqualTo(projectKey, "zeenea")),
                Filter.not(Filter.isEqualTo(projectKey, "datacruncher"))),
            Filter.isEqualTo(tableKey, "foobar")),
        actual);
  }

  @Test
  @DisplayName("parse should parse 'any' filter with single expression")
  void parseShouldParseAnyFilterWithSingleExpression() {
    var actual = parser.parse("any tag in tags match tag = 'valid'");
    assertEquals(Filter.any(tagsKey, tagKey, Filter.isEqualTo(tagKey, "valid")), actual);
  }

  @Test
  @DisplayName("parse should parse 'any' filter with single expression in 'and' close")
  void parseShouldParseAnyFilterWithSingleExpressionInAndClose() {
    var actual = parser.parse("any tag in tags match tag = 'valid' and project = 'zeenea'");
    assertEquals(
        Filter.and(
            Filter.any(tagsKey, tagKey, Filter.isEqualTo(tagKey, "valid")),
            Filter.isEqualTo(projectKey, "zeenea")),
        actual);
  }

  @Test
  @DisplayName("parse should parse 'any' filter with complex expression")
  void parseShouldParseAnyFilterWithComplexExpression() {
    var actual = parser.parse("any tag in tags match (tag = 'valid' and project = 'zeenea')");
    assertEquals(
        Filter.any(
            tagsKey,
            tagKey,
            Filter.and(Filter.isEqualTo(tagKey, "valid"), Filter.isEqualTo(projectKey, "zeenea"))),
        actual);
  }

  @Test
  @DisplayName("parse should parse 'all' filter with single expression")
  void parseShouldParseAllFilterWithSingleExpression() {
    var actual = parser.parse("all tag in tags match tag = 'valid'");
    assertEquals(Filter.all(tagsKey, tagKey, Filter.isEqualTo(tagKey, "valid")), actual);
  }

  @Test
  @DisplayName("parse should parse 'all' filter with single expression in 'and' close")
  void parseShouldParseAllFilterWithSingleExpressionInAndClose() {
    var actual = parser.parse("all tag in tags match tag = 'valid' and project = 'zeenea'");
    assertEquals(
        Filter.and(
            Filter.all(tagsKey, tagKey, Filter.isEqualTo(tagKey, "valid")),
            Filter.isEqualTo(projectKey, "zeenea")),
        actual);
  }

  @Test
  @DisplayName("parse should parse 'all' filter with complex expression")
  void parseShouldParseAllFilterWithComplexExpression() {
    var actual = parser.parse("all tag in tags match (tag = 'valid' and project = 'zeenea')");
    assertEquals(
        Filter.all(
            tagsKey,
            tagKey,
            Filter.and(Filter.isEqualTo(tagKey, "valid"), Filter.isEqualTo(projectKey, "zeenea"))),
        actual);
  }

  @Test
  @DisplayName("parse should simply 'all' filter when expression isn't related to the item key")
  void parseShouldSimplifyAllFilterWhenExpressionNotRelatedToItemKey() {
    var actual = parser.parse("all tag in tags match project = 'zeenea'");
    assertEquals(Filter.isEqualTo(projectKey, "zeenea"), actual);
  }

  @Test
  @DisplayName("Filter.toString should be compatible with parser")
  void filterToStringShouldBeCompatible() {
    var text =
        "(not ((project = 'zeenea') or (project = 'datacruncher'))) and ((table != 'foobar') and (project ~ /z\\/ee.[ne]a.*/ims))";
    var filter = parser.parse(text);
    assertEquals(text, filter.toString());
  }

  @Test
  @DisplayName("parser should reject invalid key")
  void parserShouldRejectInvalidKey() {
    var actual =
        assertThrows(FilterParsingException.class, () -> parser.parse("invalid_key = 'zeenea'"));
    assertEquals(
        "Invalid key (not found) \"invalid_key\" at line 1, column 1. Expected values {project, table, tags}",
        actual.getMessage());
  }

  @Test
  @DisplayName("parser should reject invalid token")
  void parserShouldRejectInvalidToken() {
    var actual = assertThrows(FilterParsingException.class, () -> parser.parse("table = 123"));
    assertInstanceOf(TokenMgrException.class, actual.getCause());
    assertEquals("Lexical error at line 1, column 9.  Encountered: '1' (49),", actual.getMessage());
  }

  @Test
  @DisplayName("parser should reject syntax error")
  void parserShouldRejectSyntaxError() {
    var actual = assertThrows(FilterParsingException.class, () -> parser.parse("table 'zeenea'"));
    assertInstanceOf(ParseException.class, actual.getCause());
    assertThat(actual.getMessage())
        .startsWith("Encountered unexpected token: \"\\'zeenea\\'\" <STRING>");
  }
}
