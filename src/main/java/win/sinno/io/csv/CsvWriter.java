package win.sinno.io.csv;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.io.FileWriter;

/**
 * csv
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-27 14:23.
 */
public class CsvWriter {

  private static final Logger LOG = LoggerFactory.getLogger(CsvWriter.class);

  private static final String DEFAULT_CHARSET = "UTF-8";

  private boolean appendMode;
  private String outPath;
  private String fileName;
  private String charset = DEFAULT_CHARSET;

  private FileWriter fileWriter;

  public CsvWriter() {
  }

  public CsvWriter(String outPath, String fileName) {
    this.outPath = outPath;
    this.fileName = fileName;
  }

  public CsvWriter(String outPath, String fileName, boolean appendMode) {
    this.outPath = outPath;
    this.fileName = fileName;
    this.appendMode = appendMode;
  }

  public CsvWriter setOutPath(String outPath) {
    this.outPath = outPath;
    return this;
  }

  public CsvWriter setFileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  public CsvWriter setAppendMode(boolean appendMode) {
    this.appendMode = appendMode;
    return this;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public File build() throws IOException {
    fileWriter = new FileWriter(outPath, fileName + ".csv", appendMode);
    fileWriter.setCharset(charset);
    return fileWriter.build();
  }

  /**
   * http://baike.baidu.com/item/BOM/2790364
   */
  public void setBom() throws IOException {
    write(new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}));
  }

  public void append(String[] data) throws IOException {

    if (data == null || data.length == 0) {
      return;
    }

    int len = data.length;
    StringBuilder sb = new StringBuilder();
    String record = null;
    for (int i = 0; i < len; ) {
      record = data[i];
      boolean needQuote = false;

      if (record.contains("\"")) {
        record = record.replaceAll("\"", "\"\"");
        needQuote = true;
      }

      if (record.contains(",") || record.contains(" ")) {
        needQuote = true;
      }

      if (needQuote) {
        sb.append('"');
      }

      sb.append(record);

      if (needQuote) {
        sb.append('"');
      }

      if (++i < len) {
        sb.append(",");
      }
    }

    write(sb.toString());
  }

  public void append(List<String> data) throws IOException {

    if (CollectionUtils.isEmpty(data)) {
      return;
    }

    StringBuilder sb = new StringBuilder();
    Iterator<String> dataIterator = data.iterator();
    String record = null;
    while (dataIterator.hasNext()) {
      record = dataIterator.next();
      boolean needQuote = false;

      if (record.contains("\"")) {
        record = record.replaceAll("\"", "\"\"");
        needQuote = true;
      }

      if (record.contains(",") || record.contains(" ")) {
        needQuote = true;
      }

      if (needQuote) {
        sb.append('"');
      }

      sb.append(record);

      if (needQuote) {
        sb.append('"');
      }

      if (dataIterator.hasNext()) {
        sb.append(",");
      }
    }

    write(sb.toString());
  }

  public void write(String str) throws IOException {
    fileWriter.write(str);
  }

  public void newLine() throws IOException {
    fileWriter.newLine();
  }

  public void flush() throws IOException {
    fileWriter.flush();
  }

  public void close() throws IOException {
    fileWriter.close();
  }

}
