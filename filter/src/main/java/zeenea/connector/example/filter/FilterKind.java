package zeenea.connector.example.filter;

public enum FilterKind {
  EMPTY,
  TEXT,
  LIST;

  /** Check is that can be stored by this. */
  public boolean accepts(FilterKind that) {
    return this == that || that == EMPTY;
  }
}
