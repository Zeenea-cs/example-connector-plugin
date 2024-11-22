package zeenea.connector.example.filter;

/** Exception triggerd by an error while parsing a filter specification. */
public class FilterParsingException extends RuntimeException {

  public FilterParsingException(String message) {
    super(message);
  }

  public FilterParsingException(String message, Exception cause) {
    super(message, cause);
  }

  public FilterParsingException(Throwable cause) {
    super(cause);
  }
}
