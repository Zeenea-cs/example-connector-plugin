package zeenea.connector.example.filter;

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
