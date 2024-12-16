/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ListFilterTest {
  private static final FilterKey LIST_KEY = FilterKey.list("list");
  private static final FilterKey ITEM_KEY = FilterKey.text("item");
  private static final FilterKey OTHER_KEY = FilterKey.text("other");

  @Test
  @DisplayName("ALL filter matches should accept list with all items matching the item filter")
  void allFilterMatchesShouldAcceptListWithAllItemsMatchingTheItemFilter() {
    var filter = Filter.all(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.textList(LIST_KEY, "a", "b")));
    assertTrue(actual);
  }

  @Test
  @DisplayName("ALL filter matches should reject list with one items not matching the item filter")
  void allFilterMatchesShouldRejectListWithOneItemsMatchingTheItemFilter() {
    var filter = Filter.all(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.textList(LIST_KEY, "a", "c")));
    assertFalse(actual);
  }

  @Test
  @DisplayName("ALL filter matches should reject list with no item matching the item filter")
  void allFilterMatchesShouldRejectListWithNoItemsMatchingTheItemFilter() {
    var filter = Filter.all(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.textList(LIST_KEY, "c", "d")));
    assertFalse(actual);
  }

  @Test
  @DisplayName("ALL filter matches should accept empty list")
  void allFilterMatchesShouldAcceptEmptyList() {
    var filter = Filter.all(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.textList(LIST_KEY)));
    assertTrue(actual);
  }

  @Test
  @DisplayName("ANY filter matches should accept list with all items matching the item filter")
  void anyFilterMatchesShouldAcceptListWithAllItemsMatchingTheItemFilter() {
    var filter = Filter.any(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.textList(LIST_KEY, "a", "b")));
    assertTrue(actual);
  }

  @Test
  @DisplayName("ANY filter matches should reject list with one items matching the item filter")
  void anyFilterMatchesShouldRejectListWithOneItemsMatchingTheItemFilter() {
    var filter = Filter.any(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.textList(LIST_KEY, "a", "c")));
    assertTrue(actual);
  }

  @Test
  @DisplayName("ANY filter matches should reject list with no item matching the item filter")
  void anyFilterMatchesShouldRejectListWithNoItemsMatchingTheItemFilter() {
    var filter = Filter.any(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.textList(LIST_KEY, "c", "d")));
    assertFalse(actual);
  }

  @Test
  @DisplayName("ANY filter matches should reject empty list")
  void anyFilterMatchesShouldRejectEmptyList() {
    var filter = Filter.any(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.matches(FilterItem.of(FilterKeyValue.textList(LIST_KEY)));
    assertFalse(actual);
  }

  @Test
  @DisplayName("ANY (always) filter matches should reject empty list")
  void anyAlwaysFilterMatchesShouldRejectEmptyList() {
    var filter = Filter.any(LIST_KEY, ITEM_KEY, Filter.always());
    var actual = filter.matches(FilterItem.of(FilterKeyValue.textList(LIST_KEY)));
    assertFalse(actual);
  }

  @Test
  @DisplayName("ALL filter should be ALWAYS if the item filter is ALWAYS")
  void allFilterShouldBeAlwaysIfTheItemFilterIsAlways() {
    var filter = Filter.all(LIST_KEY, ITEM_KEY, Filter.always());
    assertEquals(Filter.always(), filter);
  }

  @Test
  @DisplayName("ALL filter should be NEVER if the item filter is NEVER")
  void allFilterShouldBeNeverIfTheItemFilterIsNever() {
    var filter = Filter.all(LIST_KEY, ITEM_KEY, Filter.never());
    assertEquals(Filter.never(), filter);
  }

  @Test
  @DisplayName("ALL filter should be the element filter if it doesn't depends on the element key")
  void allFilterShouldBeTheElementFilterIfItDoesntDependsOnTheElementKey() {
    var filter = Filter.all(LIST_KEY, ITEM_KEY, Filter.in(OTHER_KEY, "a", "b"));
    assertEquals(Filter.in(OTHER_KEY, "a", "b"), filter);
  }

  @Test
  @DisplayName("ANY filter should be never if the item filter is never")
  void anyFilterShouldBeNeverIfTheItemFilterIsAlways() {
    var filter = Filter.any(LIST_KEY, ITEM_KEY, Filter.never());
    assertEquals(Filter.never(), filter);
  }

  @Test
  @DisplayName(
      "ANY filter should NOT be the element filter if it doesn't depends on the element key")
  void anyFilterShouldBeTheElementFilterIfItDoesntDependsOnTheElementKey() {
    var filter = Filter.any(LIST_KEY, ITEM_KEY, Filter.in(OTHER_KEY, "a", "b"));
    assertInstanceOf(Filter.AnyMatch.class, filter);
  }

  @Test
  @DisplayName("withContext should rewrite ALL as always if a matching text value is set")
  void testWithContextShouldRewriteAllAsAlwaysIfAMatchingTextValueIsSet() {
    var filter = Filter.all(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.textList(LIST_KEY, "a", "b")));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite ALL as never if a non matching text value is set")
  void testWithContextShouldRewriteAllAsNeverIfANonMatchingTextValueIsSet() {
    var filter = Filter.all(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.textList(LIST_KEY, "a", "c")));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite ALL as never if the key is unset")
  void testWithContextShouldRewriteAllAsNeverIfTheKeyIsUnset() {
    var filter = Filter.all(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(LIST_KEY, FilterValue.unset())));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should not rewrite ALL if key is not in the context")
  void testWithContextShouldNotRewriteAllIfKeyIsNotInTheContext() {
    var filter = Filter.all(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.text(OTHER_KEY, "any")));
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName("withContext should rewrite ANY as always if a matching text value is set")
  void testWithContextShouldRewriteAnyAsAlwaysIfAMatchingTextValueIsSet() {
    var filter = Filter.any(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.textList(LIST_KEY, "a", "c")));
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite ANY as never if a non matching text value is set")
  void testWithContextShouldRewriteAnyAsNeverIfANonMatchingTextValueIsSet() {
    var filter = Filter.any(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.textList(LIST_KEY, "c", "d")));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should rewrite ANY as never if the key is unset")
  void testWithContextShouldRewriteAnyAsNeverIfTheKeyIsUnset() {
    var filter = Filter.any(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual =
        filter.withContext(FilterItem.of(FilterKeyValue.of(LIST_KEY, FilterValue.unset())));
    assertEquals(Filter.never(), actual);
  }

  @Test
  @DisplayName("withContext should not rewrite ANY if key is not in the context")
  void testWithContextShouldNotRewriteAnyIfKeyIsNotInTheContext() {
    var filter = Filter.any(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.withContext(FilterItem.of(FilterKeyValue.text(OTHER_KEY, "any")));
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName(
      "partial should not rewrite ALL if the list key is present and item filter use only item key")
  void testPartialShouldNotRewriteAllIfTheListKeyIsPresentAndInnerFilterUseOnlyItemKey() {
    var filter = Filter.all(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.partial(LIST_KEY);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName(
      "partial should rewrite ALL item filter if the list key is present and the item filter is rewritten")
  void partialShouldRewriteAllItemFilterIfTheListKeyIsPresentAndTheItemFilterIsRewritten() {
    var filter =
        Filter.all(
            LIST_KEY,
            ITEM_KEY,
            Filter.and(Filter.in(ITEM_KEY, "a", "b"), Filter.isEqualTo(OTHER_KEY, "something")));
    var actual = filter.partial(LIST_KEY);
    var expected = Filter.all(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName(
      "partial should rewrite ALL to ALWAYS if the list key is present and the item filter is rewritten to ALWAYS")
  void partialShouldRewriteAllToAlwaysIfTheListKeyIsPresentAndTheItemFilterIsRewrittenToAlways() {
    var filter =
        Filter.all(
            LIST_KEY,
            ITEM_KEY,
            Filter.or(Filter.in(ITEM_KEY, "a", "b"), Filter.isEqualTo(OTHER_KEY, "something")));
    var actual = filter.partial(LIST_KEY);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should rewrite ALL to ALWAYS if the list key is not present")
  void partialShouldRewriteAllToAlwaysIfTheListKeyIsNotPresent() {
    var filter = Filter.all(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.partial(OTHER_KEY);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName(
      "partial should not rewrite ANY if the list key is present and item filter use only item key")
  void testPartialShouldNotRewriteAnyIfTheListKeyIsPresentAndInnerFilterUseOnlyItemKey() {
    var filter = Filter.any(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.partial(LIST_KEY);
    assertEquals(filter, actual);
  }

  @Test
  @DisplayName(
      "partial should rewrite ANY item filter if the list key is present and the item filter is rewritten")
  void partialShouldRewriteAnyItemFilterIfTheListKeyIsPresentAndTheItemFilterIsRewritten() {
    var filter =
        Filter.any(
            LIST_KEY,
            ITEM_KEY,
            Filter.and(Filter.in(ITEM_KEY, "a", "b"), Filter.isEqualTo(OTHER_KEY, "something")));
    var actual = filter.partial(LIST_KEY);
    var expected = Filter.any(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName(
      "partial should rewrite ANY to ALWAYS if the list key is present and the item filter is rewritten to ALWAYS")
  void partialShouldRewriteAnyToAlwaysIfTheListKeyIsPresentAndTheItemFilterIsRewrittenToAlways() {
    var filter =
        Filter.any(
            LIST_KEY,
            ITEM_KEY,
            Filter.or(Filter.in(ITEM_KEY, "a", "b"), Filter.isEqualTo(OTHER_KEY, "something")));
    var actual = filter.partial(LIST_KEY);
    assertEquals(Filter.always(), actual);
  }

  @Test
  @DisplayName("partial should rewrite ANY to ALWAYS if the list key is not present")
  void partialShouldRewriteAnyToAlwaysIfTheListKeyIsNotPresent() {
    var filter = Filter.any(LIST_KEY, ITEM_KEY, Filter.in(ITEM_KEY, "a", "b"));
    var actual = filter.partial(OTHER_KEY);
    assertEquals(Filter.always(), actual);
  }
}
