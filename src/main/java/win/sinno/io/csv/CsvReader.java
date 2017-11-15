package win.sinno.io.csv;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * win.sinno.io.csv.CsvReader
 *
 * @author : admin@chenlizhong.cn
 * @date 2017/11/9
 */
public class CsvReader {

  private static final String DEFAULT_CHARSET = "UTF-8";

  private String filepath;
  private String filename;
  private String charset;

  private File file;
  private FileInputStream fis;
  private DataInputStream dis;
  private InputStreamReader isr;
  private BufferedReader br;

  private AtomicBoolean isOpen = new AtomicBoolean(false);

  public CsvReader() {
  }

  public CsvReader(String filepath, String filename, String charset) {
    this.filepath = filepath;
    this.filename = filename;
    this.charset = charset;
  }


  public void setFilepath(String filepath) {
    this.filepath = filepath;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public void open() {
    if (isOpen.compareAndSet(false, true)) {
      try {
        file = new File(filepath + "/" + filename + ".csv");  // CSV文件路径

        fis = new FileInputStream(file);

        dis = new DataInputStream(fis);
        isr = new InputStreamReader(dis, charset == null ? DEFAULT_CHARSET : charset);
        br = new BufferedReader(isr);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
  }


  public String readLine() throws IOException {
    if (isOpen.get() == false) {
      open();
    }
    return br.readLine();
  }

  public void close() {
    if (br != null) {
      try {
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    if (isr != null) {
      try {
        isr.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (dis != null) {
      try {
        dis.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (fis != null) {
      try {
        fis.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
