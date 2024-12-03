package zeenea.connector.example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import zeenea.connector.dataset.ForeignKey;

/** This is a temporary solution to fix API errors. */
public class Fix {
  private static final Method BUILD_FK;

  static {
    try {
      BUILD_FK = ForeignKey.Builder.class.getDeclaredMethod("build");
      BUILD_FK.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  /** Temporary fix of zeenea.connector.dataset.ForeignKey.Builder#build visibility issue. */
  public static ForeignKey build(ForeignKey.Builder builder) {
    try {
      return (ForeignKey) BUILD_FK.invoke(builder);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
