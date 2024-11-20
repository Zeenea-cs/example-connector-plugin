package zeenea.connector.example.json;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface JsonForeignKey {

  @NotNull
  String dataset();

  @NotNull
  List<String> sourceFields();

  @NotNull
  List<String> targetFields();

  @Nullable
  String name();
}
