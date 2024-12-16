/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.log;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represent a context in connection work. */
public class TracingContext {
  private static final ThreadLocal<TracingContext> current = new ThreadLocal<>();
  private static final AtomicInteger nextValue = new AtomicInteger();

  private final @NotNull String text;

  private TracingContext(@NotNull String text) {
    this.text = text;
  }

  /** Create the context of the creation of a new connection. */
  public static @NotNull TracingContext newConnection(String connectionCode) {
    return new TracingContext(connectionCode + "/new_connection#" + nextNumber());
  }

  /** Create the context of the creation of an inventory. */
  public static @NotNull TracingContext inventory(@NotNull String connectionCode) {
    return new TracingContext(connectionCode + "/inventory#" + nextNumber());
  }

  /** Create the context of the extraction of items. */
  public static @NotNull TracingContext extractItems(@NotNull String connectionCode) {
    return new TracingContext(connectionCode + "/extract_items#" + nextNumber());
  }

  /** Create the context of the extraction of a synchronization. */
  public static @NotNull TracingContext synchronize(@NotNull String connectionCode) {
    return new TracingContext(connectionCode + "/synchonize#" + nextNumber());
  }

  /** Create the context of the extraction of a test. */
  public static @NotNull TracingContext test() {
    return new TracingContext("test/test#" + nextNumber());
  }

  private static @NotNull String nextNumber() {
    return Integer.toUnsignedString(nextValue.getAndIncrement());
  }

  /**
   * Provide with the current zeenea.connector.example.log.TracingContext if one is defined.
   *
   * @return The current {@link TracingContext} or null is none is defined.
   */
  public static @Nullable TracingContext current() {
    return current.get();
  }

  /**
   * Provide with the current zeenea.connector.example.log.TracingContext.
   *
   * @return The current {@link TracingContext} or fails.
   * @throws NoSuchElementException if no context is defiened.
   */
  public static @NotNull TracingContext get() {
    TracingContext tracingContext = current.get();
    if (tracingContext == null) {
      throw new NoSuchElementException("No tracing context defined");
    }
    return tracingContext;
  }

  public <T> T with(Supplier<T> action) {
    TracingContext old = current.get();
    current.set(this);
    try {
      return action.get();
    } finally {
      if (old != null) current.set(old);
      else current.remove();
    }
  }

  @Override
  public String toString() {
    return text;
  }
}
