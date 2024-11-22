package zeenea.connector.example.file;

public final class FileItem<T> {
  private final T item;
  private final FileRef fileRef;

  public FileItem(T item, FileRef fileRef) {
    this.item = item;
    this.fileRef = fileRef;
  }

  public T getItem() {
    return item;
  }

  public FileRef getFileRef() {
    return fileRef;
  }
}
