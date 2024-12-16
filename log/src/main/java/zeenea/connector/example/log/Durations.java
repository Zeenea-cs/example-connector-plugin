/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.log;

import java.time.Duration;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

/** Tool to print human readable durations. */
public class Durations {
  private static final Duration TEN_MILLIS = Duration.ofMillis(10);

  public static @NotNull String elapsedSince(@NotNull Instant startTime) {
    StringBuilder sb = new StringBuilder();
    appendTo(sb, startTime, Instant.now());
    return sb.toString();
  }

  public static @NotNull String toString(@NotNull Duration duration) {
    StringBuilder sb = new StringBuilder();
    appendTo(sb, duration);
    return sb.toString();
  }

  public static void appendTo(StringBuilder sb, Instant startTime, Instant endTime) {
    appendTo(sb, Duration.between(startTime, endTime));
  }

  public static void appendTo(StringBuilder sb, Duration duration) {
    if (duration.isZero()) {
      sb.append("0 ms");
      return;

    } else if (duration.isNegative()) {
      sb.append("- ");
      appendTo(sb, duration.negated());

    } else if (duration.compareTo(TEN_MILLIS) >= 0) {
      boolean nonEmpty = false;
      long hours = duration.toHours();
      if (hours > 0L) {
        sb.append(hours).append(" h");
        duration = duration.minusHours(hours);
        nonEmpty = true;
      }
      long minutes = duration.toMinutes();
      if (minutes > 0L) {
        if (nonEmpty) sb.append(' ');
        sb.append(minutes).append(" m");
        duration = duration.minusMinutes(minutes);
        nonEmpty = true;
      }
      long seconds = duration.getSeconds();
      if (seconds > 0L) {
        if (nonEmpty) sb.append(' ');
        sb.append(seconds).append(" s");
        duration = duration.minusSeconds(seconds);
        nonEmpty = true;
      }
      long millis = duration.toMillis();
      if (millis > 0L) {
        if (nonEmpty) sb.append(' ');
        sb.append(millis).append(" ms");
      }
    } else {
      // When duration is lower than 10 ms
      int nanos = duration.getNano();
      if (nanos >= 1_000_000) {
        sb.append(nanos / 1_000_000).append(" ms");
        nanos = nanos % 1_000_000;
        if (nanos >= 1_000) {
          sb.append(' ').append(nanos / 1_000).append(" μs");
        }
      } else {
        if (nanos >= 1_000) {
          int micros = nanos / 1_000;
          sb.append(micros).append(" μs");
          nanos = nanos % 1_000;
          if (nanos >= 1) sb.append(' ').append(nanos).append(" ns");
        } else {
          sb.append(nanos).append(" ns");
        }
      }
    }
  }
}
