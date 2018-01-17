package win.sinno.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.io.util.FileUtil;

/**
 * csv
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-27 14:23.
 */
public class FileWriter {

  private static final Logger LOG = LoggerFactory.getLogger(FileWriter.class);

  private static final String DEFAULT_CHARSET = "UTF-8";

  public static final String UTF_8 = "UTF-8";

  public static final String GB2312 = "GB2312";

  private boolean appendMode;
  private String outPath;
  private String fileName;
  private String charset;

  private File file = null;
  private FileOutputStream fos = null;
  private OutputStreamWriter osw = null;
  private BufferedWriter bw = null;

  public FileWriter() {
  }

  public FileWriter(String outPath, String fileName) {
    this.outPath = outPath;
    this.fileName = fileName;
  }

  public FileWriter(String outPath, String fileName, boolean appendMode) {
    this.outPath = outPath;
    this.fileName = fileName;
    this.appendMode = appendMode;
  }

  public FileWriter setOutPath(String outPath) {
    this.outPath = outPath;
    return this;
  }

  public FileWriter setFileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  public FileWriter setAppendMode(boolean appendMode) {
    this.appendMode = appendMode;
    return this;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public File build() throws IOException {

    String filePath = FileUtil.getFilePath(outPath, fileName);

    file = new File(filePath);
    if (!file.exists()) {
      File pathFile = file.getParentFile();
      if (!pathFile.exists()) {
        pathFile.mkdirs();
      }
      file.createNewFile();
    }

    fos = new FileOutputStream(file, true);
    osw = new OutputStreamWriter(fos, charset == null ? DEFAULT_CHARSET : charset);
    bw = new BufferedWriter(osw, 1024);

    return file;
  }

  public void write(List<String> list) throws IOException {
    if (CollectionUtils.isEmpty(list)) {
      return;
    }
    for (String str : list) {
      write(str);
      newLine();
    }
  }

  public void write(String str) throws IOException {
    bw.write(str);
  }

  public void newLine() throws IOException {
    bw.newLine();
  }

  public void flush() throws IOException {
    bw.flush();
  }

  public void close() throws IOException {
    if (bw != null) {
      bw.close();
    }
    if (osw != null) {
      osw.close();
    }
    if (fos != null) {
      fos.close();
    }
  }

}
