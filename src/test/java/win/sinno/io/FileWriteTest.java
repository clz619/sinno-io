package win.sinno.io;

import java.io.IOException;
import org.junit.Test;

/**
 * win.sinno.io.FileWriteTest
 *
 * @author chenlizhong@qipeng.com
 * @date 2018/9/3
 */
public class FileWriteTest {

  @Test
  public void wrTest() throws IOException {
    FileWriter fw = new FileWriter();
    fw.setOutPath("/Users/chenlizhong/logs/");
    fw.setFileName("m100w.txt");
    fw.setCharset("utf-8");
    fw.setAppendMode(true);

    try {
      fw.build();
      long m = 13738100000l;
      for (int i = 1; i <= 1000; i++) {
        fw.write((m + i) + "");
        fw.newLine();
      }
      fw.flush();
    } finally {
      fw.close();
    }

  }
}
