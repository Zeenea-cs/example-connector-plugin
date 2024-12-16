/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.json;

public class JsonParsingException extends RuntimeException {
  private String body;

  public JsonParsingException(String message) {
    super(message);
  }

  public JsonParsingException(String message, Throwable cause) {
    super(message, cause);
  }

  public JsonParsingException(String message, String body, Throwable cause) {
    super(message, cause);
    this.body = body;
  }

  public String getBody() {
    return this.body;
  }
}
