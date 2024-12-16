/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.log;

import static zeenea.connector.example.log.Strings.indent;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/** This class is used to create logs and exceptions with consistant messages. */
public class SimpleLogger {

  private static final Map<Class<?>, SimpleLogger> instances = new ConcurrentHashMap<>();

  private final Logger log;

  private SimpleLogger(Class<?> klass) {
    this.log = LoggerFactory.getLogger(klass);
  }

  public static SimpleLogger of(Class<?> klass) {
    return instances.computeIfAbsent(klass, SimpleLogger::new);
  }

  public Entry entry(String code) {
    return new Entry(code);
  }

  public boolean isTraceEnabled() {
    return log.isTraceEnabled();
  }

  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }

  public boolean isInfoEnabled() {
    return log.isInfoEnabled();
  }

  public boolean isWarnEnabled() {
    return log.isWarnEnabled();
  }

  public boolean isErrorEnabled() {
    return log.isErrorEnabled();
  }

  public enum Verbosity {
    NORMAL,
    QUIET,
    VERY_QUIET
  }

  public class Entry {

    private final String code;
    private final List<EntryValue> values = new ArrayList<>();

    private Supplier<?> body;
    private List<EntryValue> namedBodies;

    private TracingContext context;
    private Instant startTime;
    private Instant endTime;
    private long startTimeNanos = -1;
    private long endTimeNanos = -1;

    private Verbosity verbosity = Verbosity.NORMAL;

    private Entry(String code) {
      this.code = code;
    }

    public Entry more(Consumer<Entry> adder) {
      adder.accept(this);
      return this;
    }

    public Entry with(String name, Object value) {
      this.values.add(new EntryValue(name, () -> value));
      return this;
    }

    public Entry with(String name, Supplier<?> value) {
      this.values.add(new EntryValue(name, value));
      return this;
    }

    public Entry body(Object body) {
      return body(() -> body);
    }

    public Entry body(Supplier<?> body) {
      this.body = body;
      return this;
    }

    public Entry body(String name, Object body) {
      return body(name, () -> body);
    }

    public Entry body(String name, Supplier<?> body) {
      if (namedBodies == null) namedBodies = new ArrayList<>();
      namedBodies.add(new EntryValue(name, body));
      return this;
    }

    public Entry context(TracingContext ctx) {
      this.context = ctx;
      return this;
    }

    public Entry startTime(Instant startTime) {
      this.startTime = startTime;
      if (this.endTime == null) {
        this.endTime = Instant.now();
      }
      return this;
    }

    public Entry endTime(Instant endTime) {
      this.endTime = endTime;
      return this;
    }

    public Entry startTimeNanos(long startTime) {
      this.startTimeNanos = startTime;
      if (this.endTimeNanos < 0) {
        this.endTimeNanos = System.nanoTime();
      }
      return this;
    }

    public Entry endTimeNanos(long endTime) {
      this.endTimeNanos = endTime;
      return this;
    }

    public Entry verbosity(Verbosity verbosity) {
      this.verbosity = verbosity;
      return this;
    }

    public Entry verbose() {
      return verbosity(Verbosity.NORMAL);
    }

    public Entry quiet() {
      return verbosity(Verbosity.QUIET);
    }

    public Entry veryQuiet() {
      return verbosity(Verbosity.VERY_QUIET);
    }

    /** Logs entry at ERROR level. */
    public void error() {
      log(Level.ERROR);
    }

    /**
     * Logs entry at ERROR level with given cause.
     *
     * @param cause cause exception.
     */
    public void error(Throwable cause) {
      log(Level.ERROR, cause);
    }

    /** Logs entry at WARN level. */
    public void warn() {
      log(Level.WARN);
    }

    /**
     * Logs entry at WARN level with given cause.
     *
     * @param cause cause exception.
     */
    public void warn(Throwable cause) {
      log(Level.WARN, cause);
    }

    /** Logs entry at INFO level. */
    public void info() {
      log(Level.INFO);
    }

    /**
     * Logs entry at INFO level with given cause.
     *
     * @param cause cause exception.
     */
    public void info(Throwable cause) {
      log(Level.INFO, cause);
    }

    /** Logs entry at DEBUG level. */
    public void debug() {
      log(Level.DEBUG);
    }

    /**
     * Logs entry at DEBUG level with given cause.
     *
     * @param cause cause exception.
     */
    public void debug(Throwable cause) {
      log(Level.DEBUG, cause);
    }

    /** Logs entry at TRACE level. */
    public void trace() {
      log(Level.TRACE);
    }

    /**
     * Logs entry at TRACE level with given cause.
     *
     * @param cause cause exception.
     */
    public void trace(Throwable cause) {
      log(Level.TRACE, cause);
    }

    /**
     * Build an exception with a message with the same content as simple log entries. The message is
     * logged at the DEBUG level only in case the exception was not fully proceeded.
     *
     * <p>This is the preferred way to prepare exceptions.
     *
     * @param ex Throwable factory that takes the message as an argument.
     * @param <T> Type of the Throwable.
     * @return The new Throwable.
     */
    public <T extends Throwable> T exception(Function<String, T> ex) {
      StringBuilder msgBuilder = commonMessage();
      addBodyMessage(msgBuilder);
      String msg = msgBuilder.toString();
      log.debug(msg);
      return ex.apply(msg);
    }

    /**
     * Build an exception with a message with the same content as simple log entries. The message is
     * logged at the DEBUG level only in case the exception was not fully proceeded.
     *
     * <p>This is the preferred way to prepare exceptions.
     *
     * @param ex Throwable factory that takes the message and the cause as arguments.
     * @param <T> Type of the Throwable.
     * @return The new Throwable.
     */
    public <T extends Throwable, U extends Throwable> T exception(
        U cause, BiFunction<String, U, T> ex) {
      StringBuilder msgBuilder = commonMessage();
      addBodyMessage(msgBuilder);
      String msg = msgBuilder.toString();
      log.debug(msg, cause);
      return ex.apply(msg, cause);
    }

    private void log(Level level) {
      if (isEnabled(level)) {
        StringBuilder msgBuilder = commonMessage();
        addBodyMessage(msgBuilder);
        writeLog(level, msgBuilder.toString());
      }
    }

    private void log(Level level, Throwable cause) {
      if (isEnabled(level)) {
        StringBuilder msgBuilder = commonMessage();
        addBodyMessage(msgBuilder);
        if (verbosity == Verbosity.NORMAL
            || verbosity == Verbosity.QUIET && log.isDebugEnabled()
            || log.isTraceEnabled()) {
          writeLog(level, msgBuilder.toString(), cause);
        } else {
          addCauseMessage(msgBuilder, cause);
          writeLog(level, msgBuilder.toString());
        }
      }
    }

    private StringBuilder commonMessage() {
      StringBuilder sb = new StringBuilder();
      sb.append(code);
      if (context == null) {
        context = TracingContext.current();
      }
      if (context != null) {
        sb.append(' ').append(context);
      }
      if (startTime != null) {
        sb.append(" duration='");
        Durations.appendTo(sb, startTime, endTime);
        sb.append('\'');
      } else if (startTimeNanos >= 0) {
        sb.append(" duration='");
        Durations.appendTo(sb, Duration.ofNanos(endTimeNanos - startTimeNanos));
        sb.append('\'');
      }
      for (EntryValue value : values) {
        sb.append(' ');
        sb.append(value.name());
        sb.append("='");
        sb.append(value.value().replace("'", "''"));
        sb.append('\'');
      }
      return sb;
    }

    private void addBodyMessage(StringBuilder msgBuilder) {
      Object bodyVal = body != null ? body.get() : null;
      if (bodyVal != null) {
        msgBuilder.append(System.lineSeparator());
        msgBuilder.append("    ");
        msgBuilder.append("-".repeat(72));
        msgBuilder.append(System.lineSeparator());
        msgBuilder.append(indent(bodyVal.toString(), 4));
      }
      if (namedBodies != null) {
        for (var namedBody : namedBodies) {
          String name = namedBody.name();
          var nameLen = name.length();
          int leftLineLen, rightLineLen;
          if (nameLen < 62) {
            leftLineLen = (70 - nameLen) / 2;
            rightLineLen = 70 - nameLen - leftLineLen;
          } else {
            leftLineLen = rightLineLen = 4;
          }
          msgBuilder.append(System.lineSeparator());
          msgBuilder.append(indent("-".repeat(leftLineLen), 4));
          msgBuilder.append(' ');
          msgBuilder.append(name);
          msgBuilder.append(' ');
          msgBuilder.append("-".repeat(rightLineLen));
          msgBuilder.append(System.lineSeparator());
          msgBuilder.append(indent(namedBody.value(), 4));
        }
      }
      if (bodyVal != null || (namedBodies != null && !namedBodies.isEmpty())) {
        msgBuilder.append(System.lineSeparator());
        msgBuilder.append("    ");
        msgBuilder.append("-".repeat(72));
      }
    }

    private void addCauseMessage(StringBuilder msgBuilder, Throwable cause) {
      Throwable c = cause;
      while (c != null) {
        msgBuilder.append(System.lineSeparator()).append(indent("Cause: " + c.getMessage(), 4));
        c = c.getCause();
      }
    }

    private boolean isEnabled(Level level) {
      switch (level) {
        case ERROR:
          return log.isErrorEnabled();
        case WARN:
          return log.isWarnEnabled();
        case INFO:
          return log.isInfoEnabled();
        case DEBUG:
          return log.isDebugEnabled();
        case TRACE:
          return log.isTraceEnabled();
        default:
          log.error("Unknown log level: " + level);
          return false;
      }
    }

    private void writeLog(Level level, String message) {
      switch (level) {
        case ERROR:
          log.error(message);
          break;
        case WARN:
          log.warn(message);
          break;
        case INFO:
          log.info(message);
          break;
        case DEBUG:
          log.debug(message);
          break;
        case TRACE:
          log.trace(message);
          break;
        default:
          log.error("Unknown log level: " + level);
      }
    }

    private void writeLog(Level level, String message, Throwable cause) {
      switch (level) {
        case ERROR:
          log.error(message, cause);
          break;
        case WARN:
          log.warn(message, cause);
          break;
        case INFO:
          log.info(message, cause);
          break;
        case DEBUG:
          log.debug(message, cause);
          break;
        case TRACE:
          log.trace(message, cause);
          break;
        default:
          log.error("Unknown log level: " + level);
      }
    }
  }

  private static final class EntryValue {

    private final String name;
    private final Supplier<?> valueSupplier;

    public EntryValue(String name, Supplier<?> valueSupplier) {
      this.name = name;
      this.valueSupplier = Objects.requireNonNull(valueSupplier);
    }

    String name() {
      return name;
    }

    private String value() {
      try {
        Object value = valueSupplier.get();
        return value != null ? value.toString() : "";
      } catch (RuntimeException e) {
        return "Failed to read value: " + e.getMessage();
      }
    }
  }
}
