/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

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
