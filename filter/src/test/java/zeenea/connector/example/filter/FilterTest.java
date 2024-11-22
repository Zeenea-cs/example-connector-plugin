package zeenea.connector.example.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static zeenea.connector.example.filter.FilterValue.text;

import java.util.Set;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FilterTest {
  private FilterKey projectKey = FilterKey.text("project");
  private FilterKey tableKey = FilterKey.text("table");
  private FilterKey otherKey = FilterKey.text("other");

  @Test
  @DisplayName("is null filter should accept unset matching entry")
  void test_is_null_filter_should_accept_unset_matching_entry() {
    var filter = Filter.isNull(projectKey);
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertTrue(actual);
  }

  @Test
  @DisplayName("is null filter should reject defined value matching entry")
  void test_is_null_filter_should_reject_defined_value_matching_entry() {
    var filter = Filter.isNull(projectKey);
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not null"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not is null filter should reject unset matching entry")
  void test_not_is_null_filter_should_reject_unset_matching_entry() {
    var filter = Filter.not(Filter.isNull(projectKey));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not is null filter should accept defined value matching entry")
  void test_not_is_null_filter_should_accept_defined_value_matching_entry() {
    var filter = Filter.not(Filter.isNull(projectKey));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not null"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("equals filter matches should accept text matching the text entry")
  void test_equals_filter_matches_should_accept_text_matching_the_text_entry() {
    var filter = Filter.isEqualTo(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("equals filter matches should reject text not matching the text entry")
  void test_equals_filter_matches_should_reject_text_not_matching_the_text_entry() {
    var filter = Filter.isEqualTo(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("equals filter matches should reject unset not matching entry")
  void test_equals_filter_matches_should_reject_unset_not_matching_entry() {
    var filter = Filter.isEqualTo(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not equals filter matches should reject text matching the text entry")
  void test_not_equals_filter_matches_should_reject_text_matching_the_text_entry() {
    var filter = Filter.not(Filter.isEqualTo(projectKey, "valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not equals filter matches should accept text not matching the text entry")
  void test_not_equals_filter_matches_should_accept_text_not_matching_the_text_entry() {
    var filter = Filter.not(Filter.isEqualTo(projectKey, "valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("not equals filter matches should accept unset not matching entry")
  void test_not_equals_filter_matches_should_accept_unset_not_matching_entry() {
    var filter = Filter.not(Filter.isEqualTo(projectKey, "valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertTrue(actual);
  }

  @Test
  @DisplayName("starts with filter matches should accept text equals the text entry")
  void test_starts_with_filter_matches_should_accept_text_equals_the_text_entry() {
    var filter = Filter.startsWith(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("starts with filter matches should accept text prefixed by the text entry")
  void test_starts_with_filter_matches_should_accept_text_prefixed_by_the_text_entry() {
    var filter = Filter.startsWith(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid aussi"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("starts with filter matches should reject text not matching the text entry")
  void test_starts_with_filter_matches_should_reject_text_not_matching_the_text_entry() {
    var filter = Filter.startsWith(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("starts with filter matches should reject unset not matching entry")
  void test_starts_with_filter_matches_should_reject_unset_not_matching_entry() {
    var filter = Filter.startsWith(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not starts with filter matches should reject text matching the text entry")
  void test_not_starts_with_filter_matches_should_reject_text_matching_the_text_entry() {
    var filter = Filter.not(Filter.startsWith(projectKey, "valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not starts with filter matches should reject text prefixed by the text entry")
  void test_not_starts_with_filter_matches_should_reject_text_prefixed_by_the_text_entry() {
    var filter = Filter.not(Filter.startsWith(projectKey, "valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid aussi"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not starts with filter matches should accept text not matching the text entry")
  void test_not_starts_with_filter_matches_should_accept_text_not_matching_the_text_entry() {
    var filter = Filter.not(Filter.startsWith(projectKey, "valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("not starts with filter matches should accept unset not matching entry")
  void test_not_starts_with_filter_matches_should_accept_unset_not_matching_entry() {
    var filter = Filter.not(Filter.startsWith(projectKey, "valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertTrue(actual);
  }

  @Test
  @DisplayName("ends with filter matches should accept text equals the text entry")
  void test_ends_with_filter_matches_should_accept_text_equals_the_text_entry() {
    var filter = Filter.endsWith(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("ends with filter matches should accept text suffixed by the text entry")
  void test_ends_with_filter_matches_should_accept_text_suffixed_by_the_text_entry() {
    var filter = Filter.endsWith(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("also valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("ends with filter matches should reject text not matching the text entry")
  void test_ends_with_filter_matches_should_reject_text_not_matching_the_text_entry() {
    var filter = Filter.endsWith(projectKey, "valid");
    var actual =
        filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid, really!"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("ends with filter matches should reject unset not matching entry")
  void test_ends_with_filter_matches_should_reject_unset_not_matching_entry() {
    var filter = Filter.endsWith(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not ends with filter matches should reject text matching the text entry")
  void test_not_ends_with_filter_matches_should_reject_text_matching_the_text_entry() {
    var filter = Filter.not(Filter.endsWith(projectKey, "valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not ends with filter matches should reject text suffixed by the text entry")
  void test_not_ends_with_filter_matches_should_reject_text_suffixed_by_the_text_entry() {
    var filter = Filter.not(Filter.endsWith(projectKey, "valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("also valid"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not ends with filter matches should accept text not matching the text entry")
  void test_not_ends_with_filter_matches_should_accept_text_not_matching_the_text_entry() {
    var filter = Filter.not(Filter.endsWith(projectKey, "valid"));
    var actual =
        filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid, really!"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("not ends with filter matches should accept unset not matching entry")
  void test_not_ends_with_filter_matches_should_accept_unset_not_matching_entry() {
    var filter = Filter.not(Filter.endsWith(projectKey, "valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertTrue(actual);
  }

  @Test
  @DisplayName("contains filter matches should accept text equals the text entry")
  void test_contains_filter_matches_should_accept_text_equals_the_text_entry() {
    var filter = Filter.contains(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("contains filter matches should accept text suffixed by the text entry")
  void test_contains_filter_matches_should_accept_text_suffixed_by_the_text_entry() {
    var filter = Filter.contains(projectKey, "valid");
    var actual =
        filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("also valid too"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("contains filter matches should reject text not matching the text entry")
  void test_contains_filter_matches_should_reject_text_not_matching_the_text_entry() {
    var filter = Filter.contains(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("no, really!"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("contains filter matches should reject unset not matching entry")
  void test_contains_filter_matches_should_reject_unset_not_matching_entry() {
    var filter = Filter.contains(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not contains filter matches should reject text matching the text entry")
  void test_not_contains_filter_matches_should_reject_text_matching_the_text_entry() {
    var filter = Filter.not(Filter.contains(projectKey, "valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("contains filter matches should reject text suffixed by the text entry")
  void test_contains_filter_matches_should_reject_text_suffixed_by_the_text_entry() {
    var filter = Filter.not(Filter.contains(projectKey, "valid"));
    var actual =
        filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("also valid too"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("contains filter matches should accept text not matching the text entry")
  void test_contains_filter_matches_should_accept_text_not_matching_the_text_entry() {
    var filter = Filter.not(Filter.contains(projectKey, "valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not, really!"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("contains filter matches should accept unset not matching entry")
  void test_contains_filter_matches_should_accept_unset_not_matching_entry() {
    var filter = Filter.not(Filter.contains(projectKey, "valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertTrue(actual);
  }

  @Test
  @DisplayName("glob filter matches should accept text matching the text entry 1")
  void test_glob_filter_matches_should_accept_text_matching_the_text_entry_1() {
    var filter = Filter.glob(projectKey, "v?lid*");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("glob filter matches should accept text matching the text entry 2")
  void test_glob_filter_matches_should_accept_text_matching_the_text_entry_2() {
    var filter = Filter.glob(projectKey, "v?lid*");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valide"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("glob filter matches should accept text matching the text entry 3")
  void test_glob_filter_matches_should_accept_text_matching_the_text_entry_3() {
    var filter = Filter.glob(projectKey, "v?lid*");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("volide"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("glob filter matches should accept text equals to a simlple text pattern")
  void test_glob_filter_matches_should_accept_text_equals_to_a_simlple_text_pattern() {
    var filter = Filter.glob(projectKey, "valid");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("glob filter matches should reject text not matching the text entry")
  void test_glob_filter_matches_should_reject_text_not_matching_the_text_entry() {
    var filter = Filter.glob(projectKey, "v?lid*");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("glob filter matches should reject unset not matching entry")
  void test_glob_filter_matches_should_reject_unset_not_matching_entry() {
    var filter = Filter.glob(projectKey, "v?lid*");
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not glob filter matches should reject text matching the text entry")
  void test_not_glob_filter_matches_should_reject_text_matching_the_text_entry() {
    var filter = Filter.not(Filter.glob(projectKey, "v?lid*"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valide"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not glob filter matches should accept text not matching the text entry")
  void test_not_glob_filter_matches_should_accept_text_not_matching_the_text_entry() {
    var filter = Filter.not(Filter.glob(projectKey, "v?lid*"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("not glob filter matches should accept unset not matching entry")
  void test_not_glob_filter_matches_should_accept_unset_not_matching_entry() {
    var filter = Filter.not(Filter.glob(projectKey, "v?lid*"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertTrue(actual);
  }

  @Test
  @DisplayName("regex filter matches should accept text matching the text entry 1")
  void test_regex_filter_matches_should_accept_text_matching_the_text_entry_1() {
    var filter = Filter.regex(projectKey, Pattern.compile("v.lid.*"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("regex filter matches should accept text matching the text entry 2")
  void test_regex_filter_matches_should_accept_text_matching_the_text_entry_2() {
    var filter = Filter.regex(projectKey, Pattern.compile("v.lid.*"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valide"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("regex filter matches should accept text matching the text entry 3")
  void test_regex_filter_matches_should_accept_text_matching_the_text_entry_3() {
    var filter = Filter.regex(projectKey, Pattern.compile("v.lid.*"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("volide"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("regex filter matches should reject text not matching the text entry")
  void test_regex_filter_matches_should_reject_text_not_matching_the_text_entry() {
    var filter = Filter.regex(projectKey, Pattern.compile("v.lid.*"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("regex filter matches should reject unset not matching entry")
  void test_regex_filter_matches_should_reject_unset_not_matching_entry() {
    var filter = Filter.regex(projectKey, Pattern.compile("v.lid.*"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not regex filter matches should reject text matching the text entry")
  void test_not_regex_filter_matches_should_reject_text_matching_the_text_entry() {
    var filter = Filter.not(Filter.regex(projectKey, Pattern.compile("v.lid.*")));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valide"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not regex filter matches should accept text not matching the text entry")
  void test_not_regex_filter_matches_should_accept_text_not_matching_the_text_entry() {
    var filter = Filter.not(Filter.regex(projectKey, Pattern.compile("v.lid.*")));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("not regex filter matches should accept unset not matching entry")
  void test_not_regex_filter_matches_should_accept_unset_not_matching_entry() {
    var filter = Filter.not(Filter.regex(projectKey, Pattern.compile("v.lid.*")));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertTrue(actual);
  }

  @Test
  @DisplayName("in filter matches should accept text matching single text entry")
  void test_in_filter_matches_should_accept_text_matching_single_text_entry() {
    var filter = Filter.in(projectKey, Set.of("valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("in filter matches should accept text matching one text entry")
  void test_in_filter_matches_should_accept_text_matching_one_text_entry() {
    var filter = Filter.in(projectKey, Set.of("valid", "other"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("in filter matches should reject text never matching text entry")
  void test_in_filter_matches_should_reject_text_never_matching_text_entry() {
    var filter = Filter.in(projectKey, Set.of("valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("invalid"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("in filter matches should reject unset never matching entry")
  void test_in_filter_matches_should_reject_unset_never_matching_entry() {
    var filter = Filter.in(projectKey, Set.of("valid"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertFalse(actual);
  }

  @Test
  @DisplayName("in filter matches should reject not set never matching entry")
  void test_in_filter_matches_should_reject_not_set_never_matching_entry() {
    var filter = Filter.in(projectKey, Set.of("valid"));
    var actual = filter.matches(FilterItem.of((FilterKeyValue.of(tableKey, text("table_name")))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not in matches should reject text matching single text entry")
  void test_not_in_matches_should_reject_text_matching_single_text_entry() {
    var filter = Filter.not(Filter.in(projectKey, Set.of("valid")));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not in matches should reject text matching one text entry")
  void test_not_in_matches_should_reject_text_matching_one_text_entry() {
    var filter = Filter.not(Filter.in(projectKey, Set.of("valid", "other")));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("not in matches should accept text never matching text entry")
  void test_not_in_matches_should_accept_text_never_matching_text_entry() {
    var filter = Filter.not(Filter.in(projectKey, Set.of("valid")));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("not matching"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("not in matches should accept unset never matching entry")
  void test_not_in_matches_should_accept_unset_never_matching_entry() {
    var filter = Filter.not(Filter.in(projectKey, Set.of("valid")));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertTrue(actual);
  }

  @Test
  @DisplayName("not in matches should accept not set never matching entry")
  void test_not_in_matches_should_accept_not_set_never_matching_entry() {
    var filter = Filter.not(Filter.in(projectKey, Set.of("valid")));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(tableKey, text("table_name"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("always filter matches should accept text value")
  void test_always_filter_matches_should_accept_text_value() {
    var filter = Filter.always();
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("any value"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("always filter matches should accept unset value")
  void test_always_filter_matches_should_accept_unset_value() {
    var filter = Filter.always();
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertTrue(actual);
  }

  @Test
  @DisplayName("always filter matches should accept not set value")
  void test_always_filter_matches_should_accept_not_set_value() {
    var filter = Filter.always();
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(tableKey, text("table_name"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("never filter matches should reject text value")
  void test_never_filter_matches_should_reject_text_value() {
    var filter = Filter.never();
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, text("any value"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("never filter matches should reject unset value")
  void test_never_filter_matches_should_reject_unset_value() {
    var filter = Filter.never();
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertFalse(actual);
  }

  @Test
  @DisplayName("never filter matches should reject not set value")
  void test_never_filter_matches_should_reject_not_set_value() {
    var filter = Filter.never();
    var actual = filter.matches(FilterItem.of(FilterKeyValue.of(tableKey, text("table_name"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("and filter matches should accept when both filter are valid")
  void test_and_filter_matches_should_accept_when_both_filter_are_valid() {
    var filter =
        Filter.and(Filter.in(projectKey, Set.of("system")), Filter.in(tableKey, Set.of("zeenea")));
    var actual =
        filter.matches(
            FilterItem.of(
                FilterKeyValue.of(projectKey, text("system")),
                FilterKeyValue.of(tableKey, text("zeenea"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("and filter matches should reject when left filter isn't valid")
  void test_and_filter_matches_should_reject_when_left_filter_isnt_valid() {
    var filter =
        Filter.and(Filter.in(projectKey, Set.of("system")), Filter.in(tableKey, Set.of("zeenea")));
    var actual =
        filter.matches(
            FilterItem.of(
                FilterKeyValue.of(projectKey, text("system")),
                FilterKeyValue.of(tableKey, text("not_zeenea"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("and filter matches should reject when right filter isn't valid")
  void test_and_filter_matches_should_reject_when_right_filter_isnt_valid() {
    var filter =
        Filter.and(Filter.in(projectKey, Set.of("system")), Filter.in(tableKey, Set.of("zeenea")));
    var actual =
        filter.matches(
            FilterItem.of(
                FilterKeyValue.of(projectKey, text("not_system")),
                FilterKeyValue.of(tableKey, text("zeenea"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("and filter matches should reject when no filter is valid")
  void test_and_filter_matches_should_reject_when_no_filter_is_valid() {
    var filter =
        Filter.and(Filter.in(projectKey, Set.of("system")), Filter.in(tableKey, Set.of("zeenea")));
    var actual =
        filter.matches(
            FilterItem.of(
                FilterKeyValue.of(projectKey, text("not_system")),
                FilterKeyValue.of(tableKey, text("not_zeenea"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("or filter matches should accept when both filter are valid")
  void test_or_filter_matches_should_accept_when_both_filter_are_valid() {
    var filter =
        Filter.or(Filter.in(projectKey, Set.of("system")), Filter.in(tableKey, Set.of("zeenea")));
    var actual =
        filter.matches(
            FilterItem.of(
                FilterKeyValue.of(projectKey, text("system")),
                FilterKeyValue.of(tableKey, text("zeenea"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("or filter matches should accept when left filter isn't valid")
  void test_or_filter_matches_should_accept_when_left_filter_isnt_valid() {
    var filter =
        Filter.or(Filter.in(projectKey, Set.of("system")), Filter.in(tableKey, Set.of("zeenea")));
    var actual =
        filter.matches(
            FilterItem.of(
                FilterKeyValue.of(projectKey, text("system")),
                FilterKeyValue.of(tableKey, text("not_zeenea"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("or filter matches should accept when right filter isn't valid")
  void test_or_filter_matches_should_accept_when_right_filter_isnt_valid() {
    var filter =
        Filter.or(Filter.in(projectKey, Set.of("system")), Filter.in(tableKey, Set.of("zeenea")));
    var actual =
        filter.matches(
            FilterItem.of(
                FilterKeyValue.of(projectKey, text("not_system")),
                FilterKeyValue.of(tableKey, text("zeenea"))));
    assertTrue(actual);
  }

  @Test
  @DisplayName("or filter matches should reject when no filter is valid")
  void test_or_filter_matches_should_reject_when_no_filter_is_valid() {
    var filter =
        Filter.or(Filter.in(projectKey, Set.of("system")), Filter.in(tableKey, Set.of("zeenea")));
    var actual =
        filter.matches(
            FilterItem.of(
                FilterKeyValue.of(projectKey, text("not_system")),
                FilterKeyValue.of(tableKey, text("not_zeenea"))));
    assertFalse(actual);
  }

  @Test
  @DisplayName("withContext should rewrite is null as never if a text value is set")
  void test_withContext_should_rewrite_is_null_as_never_if_a_text_value_is_set() {
    var filter = Filter.isNull(projectKey);
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("value"))));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite is null as always if the key is unset")
  void test_withContext_should_rewrite_is_null_as_always_if_the_key_is_unset() {
    var filter = Filter.isNull(projectKey);
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should not rewrite is null if key is not in the context")
  void test_withContext_should_not_rewrite_is_null_if_key_is_not_in_the_context() {
    var filter = Filter.isNull(projectKey);
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("any"))));
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("withContext should rewrite is not null as always if a text value is set")
  void test_withContext_should_rewrite_is_not_null_as_always_if_a_text_value_is_set() {
    var filter = Filter.not(Filter.isNull(projectKey));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("value"))));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite is not null as never if the key is unset")
  void test_withContext_should_rewrite_is_not_null_as_never_if_the_key_is_unset() {
    var filter = Filter.not(Filter.isNull(projectKey));
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should not rewrite is not null if key is not in the context")
  void test_withContext_should_not_rewrite_is_not_null_if_key_is_not_in_the_context() {
    var filter = Filter.not(Filter.isNull(projectKey));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("any"))));
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("withContext should rewrite equals as always if a matching text value is set")
  void test_withContext_should_rewrite_equals_as_always_if_a_matching_text_value_is_set() {
    var filter = Filter.isEqualTo(projectKey, "valid");
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite equals as always if a non matching text value is set")
  void test_withContext_should_rewrite_equals_as_always_if_a_non_matching_text_value_is_set() {
    var filter = Filter.isEqualTo(projectKey, "valid");
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid"))));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite equals as never if the key is unset")
  void test_withContext_should_rewrite_equals_as_never_if_the_key_is_unset() {
    var filter = Filter.isEqualTo(projectKey, "valid");
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should not rewrite equals if key is not in the context")
  void test_withContext_should_not_rewrite_equals_if_key_is_not_in_the_context() {
    var filter = Filter.isEqualTo(projectKey, "valid");
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("any"))));
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("withContext should rewrite not equals as never if a matching text value is set")
  void test_withContext_should_rewrite_not_equals_as_never_if_a_matching_text_value_is_set() {
    var filter = Filter.not(Filter.isEqualTo(projectKey, "valid"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName(
      "withContext should rewrite not equals as always if a non matching text value is set")
  void test_withContext_should_rewrite_not_equals_as_always_if_a_non_matching_text_value_is_set() {
    var filter = Filter.not(Filter.isEqualTo(projectKey, "valid"));
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid"))));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite not equals as always if the key is unset")
  void test_withContext_should_rewrite_not_equals_as_always_if_the_key_is_unset() {
    var filter = Filter.not(Filter.isEqualTo(projectKey, "valid"));
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should not rewrite not equals if key is not in the context")
  void test_withContext_should_not_rewrite_not_equals_if_key_is_not_in_the_context() {
    var filter = Filter.not(Filter.isEqualTo(projectKey, "valid"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("any"))));
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("withContext should rewrite in as always if a matching text value is set")
  void test_withContext_should_rewrite_in_as_always_if_a_matching_text_value_is_set() {
    var filter = Filter.in(projectKey, "valid", "ok");
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite in as never if a non matching text value is set")
  void test_withContext_should_rewrite_in_as_never_if_a_non_matching_text_value_is_set() {
    var filter = Filter.in(projectKey, "valid", "ok");
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid"))));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite in as never if the key is unset")
  void test_withContext_should_rewrite_in_as_never_if_the_key_is_unset() {
    var filter = Filter.in(projectKey, "valid", "ok");
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should not rewrite in if key is not in the context")
  void test_withContext_should_not_rewrite_in_if_key_is_not_in_the_context() {
    var filter = Filter.in(projectKey, "valid", "ok");
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("any"))));
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("withContext should rewrite not in as never if a matching text value is set")
  void test_withContext_should_rewrite_not_in_as_never_if_a_matching_text_value_is_set() {
    var filter = Filter.not(Filter.in(projectKey, "valid", "ok"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("valid"))));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite not in as always if a non matching text value is set")
  void test_withContext_should_rewrite_not_in_as_always_if_a_non_matching_text_value_is_set() {
    var filter = Filter.not(Filter.in(projectKey, "valid", "ok"));
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("not_valid"))));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite not in as always if the key is unset")
  void test_withContext_should_rewrite_not_in_as_always_if_the_key_is_unset() {
    var filter = Filter.not(Filter.in(projectKey, "valid", "ok"));
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, FilterValue.unset())));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should not rewrite not in if key is not in the context")
  void test_withContext_should_not_rewrite_not_in_if_key_is_not_in_the_context() {
    var filter = Filter.not(Filter.in(projectKey, "valid", "ok"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("any"))));
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName(
      "withContext should rewrite AND to the left operand when the right filter matches the context")
  void
      test_withContext_should_rewrite_AND_to_the_left_operand_when_the_right_filter_matches_the_context() {
    var filter =
        Filter.and(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("zeenea"))));
    assertEquals(Filter.isEqualTo(projectKey, "system"), actual);
  }

  @Test
  @DisplayName(
      "withContext should rewrite AND to the right operand when the left filter matches the context")
  void
      test_withContext_should_rewrite_AND_to_the_right_operand_when_the_left_filter_matches_the_context() {
    var filter =
        Filter.and(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("system"))));
    assertEquals(Filter.isEqualTo(tableKey, "zeenea"), actual);
  }

  @Test
  @DisplayName("withContext should rewrite AND to never when the right don't match the context")
  void test_withContext_should_rewrite_AND_to_never_when_the_right_dont_match_the_context() {
    var filter =
        Filter.and(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("not_zeenea"))));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite AND to never when the left don't match the context")
  void test_withContext_should_rewrite_AND_to_never_when_the_left_dont_match_the_context() {
    var filter =
        Filter.and(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("not_system"))));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should not rewrite AND when no filter is in the context")
  void test_withContext_should_not_rewrite_AND_when_no_filter_is_in_the_context() {
    var filter =
        Filter.and(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(otherKey, text("any text"))));
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName(
      "withContext should rewrite NOT AND to the left operand when the not right filter matches the context")
  void
      test_withContext_should_rewrite_NOT_AND_to_the_left_operand_when_the_not_right_filter_matches_the_context() {
    var filter =
        Filter.not(
            Filter.and(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("zeenea"))));
    assertEquals(Filter.not(Filter.isEqualTo(projectKey, "system")), actual);
  }

  @Test
  @DisplayName(
      "withContext should rewrite NOT AND to the right operand when the not left filter matches the context")
  void
      test_withContext_should_rewrite_NOT_AND_to_the_right_operand_when_the_not_left_filter_matches_the_context() {
    var filter =
        Filter.not(
            Filter.and(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("system"))));
    assertEquals(Filter.not(Filter.isEqualTo(tableKey, "zeenea")), actual);
  }

  @Test
  @DisplayName(
      "withContext should rewrite NOT AND to the right operand when the not left filter matches the context with double not optimization")
  void
      test_withContext_should_rewrite_NOT_AND_to_the_right_operand_when_the_not_left_filter_matches_the_context_with_double_not_optimization() {
    var filter =
        Filter.not(
            Filter.and(
                Filter.isEqualTo(projectKey, "system"),
                Filter.not(Filter.isEqualTo(tableKey, "zeenea"))));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("system"))));
    assertEquals(Filter.isEqualTo(tableKey, "zeenea"), actual);
  }

  @Test
  @DisplayName(
      "withContext should rewrite NOT AND to always when the right don't match the context")
  void test_withContext_should_rewrite_NOT_AND_to_always_when_the_right_dont_match_the_context() {
    var filter =
        Filter.not(
            Filter.and(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("not_zeenea"))));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite NOT AND to always when the left don't match the context")
  void test_withContext_should_rewrite_NOT_AND_to_always_when_the_left_dont_match_the_context() {
    var filter =
        Filter.not(
            Filter.and(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("not_system"))));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should not rewrite NOT AND when no filter is in the context")
  void test_withContext_should_not_rewrite_NOT_AND_when_no_filter_is_in_the_context() {
    var filter =
        Filter.not(
            Filter.and(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(otherKey, text("any text"))));
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("withContext should rewrite OR to Always when right operand matches the context")
  void test_withContext_should_rewrite_OR_to_Always_when_right_operand_matches_the_context() {
    var filter =
        Filter.or(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("zeenea"))));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite OR to Always when left operand matches the context")
  void test_withContext_should_rewrite_OR_to_Always_when_left_operand_matches_the_context() {
    var filter =
        Filter.or(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("system"))));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite OR to Always when both operands matches the context")
  void test_withContext_should_rewrite_OR_to_Always_when_both_operands_matches_the_context() {
    var filter =
        Filter.or(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual =
        filter.withContext(
            FilterItem.of(
                FilterKeyValue.of(projectKey, text("system")),
                FilterKeyValue.of(tableKey, text("zeenea"))));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName(
      "withContext should rewrite OR to the left operand when the right one don't match the context")
  void
      test_withContext_should_rewrite_OR_to_the_left_operand_when_the_right_one_dont_match_the_context() {
    var filter =
        Filter.or(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("not_zeenea"))));
    assertEquals(Filter.isEqualTo(projectKey, "system"), actual);
  }

  @Test
  @DisplayName(
      "withContext should rewrite OR to the right operand when and the left don't match the context")
  void
      test_withContext_should_rewrite_OR_to_the_right_operand_when_and_the_left_dont_match_the_context() {
    var filter =
        Filter.or(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("not_system"))));
    assertEquals(Filter.isEqualTo(tableKey, "zeenea"), actual);
  }

  @Test
  @DisplayName("withContext should not rewrite OR when no filter is in the context")
  void test_withContext_should_not_rewrite_OR_when_no_filter_is_in_the_context() {
    var filter =
        Filter.or(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(otherKey, text("any value"))));
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("withContext should rewrite NOT OR to Never when right operand matches the context")
  void test_withContext_should_rewrite_NOT_OR_to_Never_when_right_operand_matches_the_context() {
    var filter =
        Filter.not(
            Filter.or(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("zeenea"))));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite NOT OR to Never when left operand matches the context")
  void test_withContext_should_rewrite_NOT_OR_to_Never_when_left_operand_matches_the_context() {
    var filter =
        Filter.not(
            Filter.or(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("system"))));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite NOT OR to Never when both operands matches the context")
  void test_withContext_should_rewrite_NOT_OR_to_Never_when_both_operands_matches_the_context() {
    var filter =
        Filter.not(
            Filter.or(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual =
        filter.withContext(
            FilterItem.of(
                FilterKeyValue.of(projectKey, text("system")),
                FilterKeyValue.of(tableKey, text("zeenea"))));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName(
      "withContext should rewrite NOT OR to the not left operand when the right one don't match the context")
  void
      test_withContext_should_rewrite_NOT_OR_to_the_not_left_operand_when_the_right_one_dont_match_the_context() {
    var filter =
        Filter.not(
            Filter.or(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("not_zeenea"))));
    assertEquals(Filter.not(Filter.isEqualTo(projectKey, "system")), actual);
  }

  @Test
  @DisplayName(
      "withContext should rewrite NOT OR to the not left operand when the right one don't match the context with double not optimization")
  void
      test_withContext_should_rewrite_NOT_OR_to_the_not_left_operand_when_the_right_one_dont_match_the_context_with_double_not_optimization() {
    var filter =
        Filter.not(
            Filter.or(
                Filter.not(Filter.isEqualTo(projectKey, "system")),
                Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(tableKey, text("not_zeenea"))));
    assertEquals(Filter.isEqualTo(projectKey, "system"), actual);
  }

  @Test
  @DisplayName(
      "withContext should rewrite NOT OR to the not right operand when and the left don't match the context")
  void
      test_withContext_should_rewrite_NOT_OR_to_the_not_right_operand_when_and_the_left_dont_match_the_context() {
    var filter =
        Filter.not(
            Filter.or(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(projectKey, text("not_system"))));
    assertEquals(Filter.not(Filter.isEqualTo(tableKey, "zeenea")), actual);
  }

  @Test
  @DisplayName("withContext should not rewrite NOT OR when no filters is in the context")
  void test_withContext_should_not_rewrite_NOT_OR_when_no_filters_is_in_the_context() {
    var filter =
        Filter.not(
            Filter.or(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.of(otherKey, text("any value"))));
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("partial should not rewrite IS NULL when key in present")
  void test_partial_should_not_rewrite_IS_NULL_when_key_in_present() {
    var filter = Filter.isNull(projectKey);
    var actual = filter.partial(projectKey);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("partial should rewrite IS NULL as Always if the key is not present")
  void test_partial_should_rewrite_IS_NULL_as_Always_if_the_key_is_not_present() {
    var filter = Filter.isNull(projectKey);
    var actual = filter.partial(tableKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should not rewrite NOT IS NULL to Always if the key is present")
  void test_partial_should_not_rewrite_NOT_IS_NULL_to_Always_if_the_key_is_present() {
    var filter = Filter.not(Filter.isNull(projectKey));
    var actual = filter.partial(projectKey);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("partial should rewrite NOT IS NULL to Always if the key is not present")
  void test_partial_should_rewrite_NOT_IS_NULL_to_Always_if_the_key_is_not_present() {
    var filter = Filter.not(Filter.isNull(projectKey));
    var actual = filter.partial(tableKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should not rewrite Equals if the key is present")
  void test_partial_should_not_rewrite_Equals_if_the_key_is_present() {
    var filter = Filter.isEqualTo(projectKey, "valid");
    var actual = filter.partial(projectKey);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("partial should rewrite Equals as Always if the key is not present")
  void test_partial_should_rewrite_Equals_as_Always_if_the_key_is_not_present() {
    var filter = Filter.isEqualTo(projectKey, "valid");
    var actual = filter.partial(tableKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should not rewrite NOT Equals if the key is present")
  void test_partial_should_not_rewrite_NOT_Equals_if_the_key_is_present() {
    var filter = Filter.not(Filter.isEqualTo(projectKey, "valid"));
    var actual = filter.partial(projectKey);
    assertEquals(filter, actual);
    ;
  }

  @Test
  @DisplayName("partial should rewrite NOT Equals as Always if the key is not present")
  void test_partial_should_rewrite_NOT_Equals_as_Always_if_the_key_is_not_present() {
    var filter = Filter.not(Filter.isEqualTo(projectKey, "valid"));
    var actual = filter.partial(tableKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should not rewrite Starts With if the key is present")
  void test_partial_should_not_rewrite_Starts_With_if_the_key_is_present() {
    var filter = Filter.startsWith(projectKey, "valid");
    var actual = filter.partial(projectKey);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("partial should rewrite Starts With as Always if the key is not present")
  void test_partial_should_rewrite_Starts_With_as_Always_if_the_key_is_not_present() {
    var filter = Filter.startsWith(projectKey, "valid");
    var actual = filter.partial(tableKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should not rewrite NOT Starts With if the key is present")
  void test_partial_should_not_rewrite_NOT_Starts_With_if_the_key_is_present() {
    var filter = Filter.not(Filter.startsWith(projectKey, "valid"));
    var actual = filter.partial(projectKey);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("partial should rewrite NOT Starts With as Always if the key is not present")
  void test_partial_should_rewrite_NOT_Starts_With_as_Always_if_the_key_is_not_present() {
    var filter = Filter.not(Filter.startsWith(projectKey, "valid"));
    var actual = filter.partial(tableKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should not rewrite Ends With if the key is present")
  void test_partial_should_not_rewrite_Ends_With_if_the_key_is_present() {
    var filter = Filter.endsWith(projectKey, "valid");
    var actual = filter.partial(projectKey);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("partial should rewrite Ends With as Always if the key is not present")
  void test_partial_should_rewrite_Ends_With_as_Always_if_the_key_is_not_present() {
    var filter = Filter.endsWith(projectKey, "valid");
    var actual = filter.partial(tableKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should not rewrite NOT Ends With if the key is present")
  void test_partial_should_not_rewrite_NOT_Ends_With_if_the_key_is_present() {
    var filter = Filter.not(Filter.endsWith(projectKey, "valid"));
    var actual = filter.partial(projectKey);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("partial should rewrite NOT Ends With as Always if the key is not present")
  void test_partial_should_rewrite_NOT_Ends_With_as_Always_if_the_key_is_not_present() {
    var filter = Filter.not(Filter.endsWith(projectKey, "valid"));
    var actual = filter.partial(tableKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should not rewrite Contains if the key is present")
  void test_partial_should_not_rewrite_Contains_if_the_key_is_present() {
    var filter = Filter.contains(projectKey, "valid");
    var actual = filter.partial(projectKey);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("partial should rewrite Contains as Always if the key is not present")
  void test_partial_should_rewrite_Contains_as_Always_if_the_key_is_not_present() {
    var filter = Filter.contains(projectKey, "valid");
    var actual = filter.partial(tableKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should not rewrite NOT Contains if the key is present")
  void test_partial_should_not_rewrite_NOT_Contains_if_the_key_is_present() {
    var filter = Filter.not(Filter.contains(projectKey, "valid"));
    var actual = filter.partial(projectKey);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("partial should rewrite NOT Contains as Always if the key is not present")
  void test_partial_should_rewrite_NOT_Contains_as_Always_if_the_key_is_not_present() {
    var filter = Filter.not(Filter.contains(projectKey, "valid"));
    var actual = filter.partial(tableKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should not rewrite AND if both operands key are present")
  void test_partial_should_not_rewrite_AND_if_both_operands_key_are_present() {
    var filter =
        Filter.and(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.partial(projectKey, tableKey);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName(
      "partial should rewrite AND to left operand if it's key is present and right's key isn't")
  void test_partial_should_rewrite_AND_to_left_operand_if_its_key_is_present_and_rights_key_isnt() {
    var filter =
        Filter.and(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.partial(projectKey);
    assertEquals(Filter.isEqualTo(projectKey, "system"), actual);
  }

  @Test
  @DisplayName(
      "partial should rewrite AND to right operand if it's key is present and left's key isn't")
  void test_partial_should_rewrite_AND_to_right_operand_if_its_key_is_present_and_lefts_key_isnt() {
    var filter =
        Filter.and(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.partial(tableKey);
    assertEquals(Filter.isEqualTo(tableKey, "zeenea"), actual);
  }

  @Test
  @DisplayName("partial should rewrite AND Always if no operand key is present")
  void test_partial_should_rewrite_AND_Always_if_no_operand_key_is_present() {
    var filter =
        Filter.and(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.partial(otherKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should not rewrite NOT AND if both operands key are present")
  void test_partial_should_not_rewrite_NOT_AND_if_both_operands_key_are_present() {
    var filter =
        Filter.not(
            Filter.and(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.partial(projectKey, tableKey);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName(
      "partial should rewrite NOT AND to left operand if it's key is present and right's key isn't")
  void
      test_partial_should_rewrite_NOT_AND_to_left_operand_if_its_key_is_present_and_rights_key_isnt() {
    var filter =
        Filter.not(
            Filter.and(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.partial(projectKey);
    assertEquals(Filter.not(Filter.isEqualTo(projectKey, "system")), actual);
  }

  @Test
  @DisplayName(
      "partial should rewrite NOT AND to right operand if it's key is present and left's key isn't")
  void
      test_partial_should_rewrite_NOT_AND_to_right_operand_if_its_key_is_present_and_lefts_key_isnt() {
    var filter =
        Filter.not(
            Filter.and(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.partial(tableKey);
    assertEquals(Filter.not(Filter.isEqualTo(tableKey, "zeenea")), actual);
  }

  @Test
  @DisplayName(
      "partial should rewrite NOT AND to right operand if it's key is present and left's key isn't with double Not optimisation")
  void
      test_partial_should_rewrite_NOT_AND_to_right_operand_if_its_key_is_present_and_lefts_key_isnt_with_double_Not_optimisation() {
    var filter =
        Filter.not(
            Filter.and(
                Filter.isEqualTo(projectKey, "system"),
                Filter.not(Filter.isEqualTo(tableKey, "zeenea"))));
    var actual = filter.partial(tableKey);
    assertEquals(Filter.isEqualTo(tableKey, "zeenea"), actual);
  }

  @Test
  @DisplayName("partial should rewrite NOT AND Always if no operand key is present")
  void test_partial_should_rewrite_NOT_AND_Always_if_no_operand_key_is_present() {
    var filter =
        Filter.not(
            Filter.and(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.partial(otherKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should not rewrite OR if both operands key are present")
  void test_partial_should_not_rewrite_OR_if_both_operands_key_are_present() {
    var filter =
        Filter.or(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.partial(projectKey, tableKey);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("partial should rewrite OR to Always if the right operand key is not present")
  void test_partial_should_rewrite_OR_to_Always_if_the_right_operand_key_is_not_present() {
    var filter =
        Filter.or(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.partial(projectKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should rewrite OR to Always if the left operand key is not present")
  void test_partial_should_rewrite_OR_to_Always_if_the_left_operand_key_is_not_present() {
    var filter =
        Filter.or(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.partial(tableKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should rewrite OR Always if no operand key is present")
  void test_partial_should_rewrite_OR_Always_if_no_operand_key_is_present() {
    var filter =
        Filter.or(Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea"));
    var actual = filter.partial(otherKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should not rewrite NOT OR if both operands key are present")
  void test_partial_should_not_rewrite_NOT_OR_if_both_operands_key_are_present() {
    var filter =
        Filter.not(
            Filter.or(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.partial(projectKey, tableKey);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("partial should rewrite NOT OR to Always if the right operand key is not present")
  void test_partial_should_rewrite_NOT_OR_to_Always_if_the_right_operand_key_is_not_present() {
    var filter =
        Filter.not(
            Filter.or(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.partial(projectKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should rewrite NOT OR to Always if the left operand key is not present")
  void test_partial_should_rewrite_NOT_OR_to_Always_if_the_left_operand_key_is_not_present() {
    var filter =
        Filter.not(
            Filter.or(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.partial(tableKey);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should rewrite NOT OR Always if no operand key is present")
  void test_partial_should_rewrite_NOT_OR_Always_if_no_operand_key_is_present() {
    var filter =
        Filter.not(
            Filter.or(
                Filter.isEqualTo(projectKey, "system"), Filter.isEqualTo(tableKey, "zeenea")));
    var actual = filter.partial(otherKey);
    assertEquals(Filter.always(), actual);
  }
}
