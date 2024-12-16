/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.filter;

/** The kind of key or value. */
public enum FilterKind {
  /** Specific kind for empty values (unset and UnknownKey). */
  EMPTY,
  /** Text kind. */
  TEXT,
  /** List kind. */
  LIST;

  /** Check is that can be stored by this. */
  public boolean accepts(FilterKind that) {
    return this == that || that == EMPTY;
  }
}
