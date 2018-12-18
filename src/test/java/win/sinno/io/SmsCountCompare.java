package win.sinno.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * win.sinno.io.SmsCountCompare
 *
 * @author chenlizhong@qipeng.com
 * @date 2018/9/17
 */
public class SmsCountCompare {

  @Test
  public void testSmsCount() throws IOException {
    FileReader fileReader = new FileReader();
    fileReader.setFilepath("/Users/chenlizhong/work/mongo/");
    fileReader.setFilename("new.csv");
    fileReader.open();

    String line = null;

    Long sum = 0l;
    String[] sc = null;
    List<String> b100w = new ArrayList<>();

    while ((line = fileReader.readLine()) != null) {
      sc = line.split(",");
      int c = Integer.valueOf(sc[2]);
      sum += c;

      if (c > 1000000) {
        b100w.add(line);
      }
    }

    fileReader.close();

    System.out.println(sum);
    System.out.println(b100w.size());
    System.out.println(b100w);

  }


}
