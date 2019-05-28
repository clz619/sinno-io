package win.sinno.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * win.sinno.io.FileByteReader
 *
 * @author chenlizhong@qipeng.com
 * @date 2019-05-28
 */
public class FileByteWriter implements Closeable {

  private AtomicBoolean isOpen = new AtomicBoolean(false);
  private String filepath;
  private String filename;

  private File file;
  private FileOutputStream fos;

  public FileByteWriter(String filepath, String filename) {
    this.filepath = filepath;
    this.filename = filename;
  }

  public boolean isOpen() {
    return isOpen.get();
  }

  public void open() throws IOException {

    if (isOpen.compareAndSet(false, true)) {
      file = new File(filepath + filename);

      // not exists
      if (!file.exists()) {
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
          parentFile.mkdirs();
        }

        file.createNewFile();
      }

      try {
        fos = new FileOutputStream(file);
      } catch (FileNotFoundException e) {
        isOpen.set(false);
        throw e;
      }
    }
  }

  public void write(byte[] bytes) throws IOException {
    fos.write(bytes);
  }

  public void write(byte[] bytes, int off, int len) throws IOException {
    fos.write(bytes, off, len);
  }

  public void flush() throws IOException {
    fos.flush();
  }

  @Override
  public void close() throws IOException {
    if (isOpen.compareAndSet(true, false)) {
      // close

      if (fos == null) {
        fos.close();
      }
    }
  }
}
