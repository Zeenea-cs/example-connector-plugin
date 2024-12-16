/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

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
