package win.sinno.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * win.sinno.io.FileByteReader
 *
 * @author chenlizhong@qipeng.com
 * @date 2019-05-28
 */
public class FileByteReader implements Closeable {

  private AtomicBoolean isOpen = new AtomicBoolean(false);
  private String filepath;
  private String filename;

  private File file;
  private FileInputStream fis;
  private ByteArrayOutputStream bos;


  public FileByteReader(String filepath, String filename) {
    this.filepath = filepath;
    this.filename = filename;
  }

  public boolean isOpen() {
    return isOpen.get();
  }

  public void open() throws IOException {
    if (isOpen.compareAndSet(false, true)) {
      file = new File(filepath + filename);
      try {
        fis = new FileInputStream(file);

        bos = new ByteArrayOutputStream();

      } catch (FileNotFoundException e) {
        isOpen.set(false);
        throw e;
      }
    }
  }

  public byte[] read() throws IOException {
    byte[] bytes = new byte[1024];
    int n;
    while ((n = fis.read(bytes)) != -1) {
      bos.write(bytes, 0, n);
    }

    return bos.toByteArray();
  }

  @Override
  public void close() throws IOException {
    if (isOpen.compareAndSet(true, false)) {
      // close
      if (bos == null) {
        bos.close();
      }

      if (fis == null) {
        fis.close();
      }
    }
  }
}
