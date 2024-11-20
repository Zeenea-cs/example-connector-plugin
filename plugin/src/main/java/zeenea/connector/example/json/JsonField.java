package zeenea.connector.example.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JsonField {
  private @NotNull String name;
  private @Nullable String description;
  private @Nullable String nativeType;
  private @Nullable String dataType;
  private @Nullable Boolean isNullable;
  private @Nullable Boolean isMultivalued;
  private @Nullable Boolean isPrimaryKey;

  public @NotNull String getName() {
    return name;
  }

  public void setName(@NotNull String name) {
    this.name = name;
  }

  public @Nullable String getDescription() {
    return description;
  }

  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  public @Nullable String getNativeType() {
    return nativeType;
  }

  public void setNativeType(@Nullable String nativeType) {
    this.nativeType = nativeType;
  }

  public @Nullable String getDataType() {
    return dataType;
  }

  public void setDataType(@Nullable String dataType) {
    this.dataType = dataType;
  }

  public @Nullable Boolean getNullable() {
    return isNullable;
  }

  public void setNullable(@Nullable Boolean nullable) {
    isNullable = nullable;
  }

  public @Nullable Boolean getMultivalued() {
    return isMultivalued;
  }

  public void setMultivalued(@Nullable Boolean multivalued) {
    isMultivalued = multivalued;
  }

  public @Nullable Boolean getPrimaryKey() {
    return isPrimaryKey;
  }

  public void setPrimaryKey(@Nullable Boolean primaryKey) {
    isPrimaryKey = primaryKey;
  }
}
