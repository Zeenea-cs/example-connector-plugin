package zeenea.connector.example.filter;

public class Glob {
  public static String toRegex(String glob) {
    var n = glob.length();
    var sb = new StringBuilder(n + n / 2);

    for (var i = 0; i < n; ++i) {
      var ch = glob.charAt(i);
      switch (ch) {
        case '?':
          sb.append('.');
          break;
        case '*':
          sb.append(".*");
          break;
        case '\\':
          sb.append('\\');
          i += 1;
          if (i >= n) {
            // Escape final '\' character
            sb.append('\\');
          } else {
            var next = glob.charAt(i);
            // Double the escape '\' character to avoir interpretation except for "\\", "\?" and
            // "\*".
            if (next != '\\' && next != '?' && next != '*') sb.append('\\');
            sb.append(next);
          }
          break;
        case '.':
        case '(':
        case ')':
        case '[':
        case ']':
        case '{':
        case '}':
        case '+':
        case '|':
        case '^':
        case '$':
          // Escape regex active characters
          sb.append('\\').append(ch);
          break;
        default:
          sb.append(ch);
      }
    }
    return sb.toString();
  }
}
