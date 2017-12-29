package win.sinno.io.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * csv write test
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-27 14:51.
 */
public class CsvWriterBlackTest {

  @Test
  public void testFile() throws IOException {
    CsvWriter csvWriter = new CsvWriter("/Users/chenlizhong/Documents/", "10w_black");
    csvWriter.setAppendMode(true);
    csvWriter.build();

    csvWriter.setBom();
    String[] header = new String[1];
    header[0] = "手机号";
    csvWriter.append(header);
    csvWriter.newLine();
    long m = 13737100001l;

    for (int i = 0; i < 100000; i++) {
      List<String> data = new ArrayList<>();
      data.add((m + i) + "");
      csvWriter.append(data);
      csvWriter.newLine();
    }

    csvWriter.flush();
    csvWriter.close();
  }
}
