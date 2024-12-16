/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GlobTest {

  @Test
  @DisplayName("toRegex should keep simple value")
  void toRegexShouldKeepSimpleValue() {
    Assertions.assertEquals("simple", Glob.toRegex("simple"));
  }

  @Test
  @DisplayName("toRegex should translate ? to .")
  void toRegexShouldTranslateQuestionMarkToDot() {
    Assertions.assertEquals("ex.mple", Glob.toRegex("ex?mple"));
  }

  @Test
  @DisplayName("toRegex should translate * to .*")
  void toRegexShouldTranslateStarToDotStar() {
    Assertions.assertEquals("mega.*", Glob.toRegex("mega*"));
  }

  @Test
  @DisplayName("toRegex should translate ? to . and * to .*")
  void toRegexShouldTranslateQuestionMarkToDotAndStarToDotStar() {
    Assertions.assertEquals("m.ga.*", Glob.toRegex("m?ga*"));
  }

  @Test
  @DisplayName("toRegex should escape \\ character before end of text")
  void toRegexShouldEscapeAntislahesBeforeEndOfText() {
    Assertions.assertEquals("simple\\\\", Glob.toRegex("simple\\"));
  }

  @Test
  @DisplayName("toRegex should escape \\ character before w")
  void toRegexShouldEscapeAntislahesBeforeW() {
    Assertions.assertEquals("simple\\\\w", Glob.toRegex("simple\\w"));
  }

  @Test
  @DisplayName("toRegex should escape . character")
  void toRegexShouldEscapeDot() {
    Assertions.assertEquals("what\\.ever.*", Glob.toRegex("what.ever*"));
  }

  @Test
  @DisplayName("toRegex should escape () character")
  void toRegexShouldParenteses() {
    Assertions.assertEquals("f\\(x\\)", Glob.toRegex("f(x)"));
  }

  @Test
  @DisplayName("toRegex should support ? character escape")
  void toRegexShouldSupportQuestionMarkEscape() {
    Assertions.assertEquals("what\\?", Glob.toRegex("what\\?"));
  }

  @Test
  @DisplayName("toRegex should support * character escape")
  void toRegexShouldSupportStarEscape() {
    Assertions.assertEquals("star\\*", Glob.toRegex("star\\*"));
  }
}
