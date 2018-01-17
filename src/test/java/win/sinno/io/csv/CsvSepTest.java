package win.sinno.io.csv;

import java.io.IOException;
import org.junit.Test;

/**
 * win.sinno.io.csv.CsvSepTest
 *
 * @author chenlizhong@qipeng.com
 * @date 2017/12/29
 */
public class CsvSepTest {

  String output = "/Users/chenlizhong/Documents/wk产品销售数据/wz1_5";
  CsvWriter csvWriter;

  int idx = 1;
  int pos = 0;

  @Test
  public void sep() throws IOException {

    String source = "/Users/chenlizhong/Documents/wk产品销售数据";

    String filename = "tmp_fail_code_1_5";

    CsvReader csvReader = new CsvReader(source, filename, "UTF-8");

    csvReader.open();

    String line = null;

    while ((line = csvReader.readLine()) != null) {
//      System.out.println(line);
      pos++;
      CsvWriter csvWriter = getNowCsvWriter();
      csvWriter.write(line);
      csvWriter.newLine();
      csvWriter.flush();

    }

    csvReader.close();

  }

  private CsvWriter getNowCsvWriter() throws IOException {

    int step = 65000;

    if (csvWriter != null && (pos % step) != 0) {
      return csvWriter;

    } else {
      if (csvWriter != null) {
        csvWriter.close();
        csvWriter = null;
      }

      csvWriter = new CsvWriter();
      csvWriter.setFileName("" + idx);
      csvWriter.setOutPath(output);
      csvWriter.setAppendMode(true);
      csvWriter.build();
      csvWriter.setBom();

      csvWriter.newLine();
      csvWriter.flush();

      System.out.println("create csv write:" + idx);

      idx++;
    }

    return csvWriter;
  }

}
