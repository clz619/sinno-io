package win.sinno.io.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;

/**
 * csv write test
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-27 14:51.
 */
public class CsvWriterTest {

  @Test
  public void testFile() throws IOException {
    CsvWriter csvWriter = new CsvWriter("/Users/chenlizhong/Documents/output-mobiles", "test1");
    csvWriter.setAppendMode(true);
    csvWriter.build();

    csvWriter.setBom();
    String[] header = new String[3];
    header[0] = "id";
    header[1] = "msg";
    header[2] = "时间";
    csvWriter.append(header);
    csvWriter.newLine();

    for (int i = 0; i < 10; i++) {
      List<String> data = new ArrayList<>();
      data.add(i + "");
      data.add("hello,world \"你好 世界, !\"");
      data.add(new Date().toString());
      csvWriter.append(data);
      csvWriter.newLine();
    }
    for (int i = 0; i < 10; i++) {
      List<String> data = new ArrayList<>();
      data.add(i + "");
      data.add("的滴滴答答滴滴答答的哈哈哈哈哈哈哈哈哈哈哈");
      data.add(new Date().toString());
      csvWriter.append(data);
      csvWriter.newLine();
    }

    csvWriter.flush();
    csvWriter.close();
  }
}
