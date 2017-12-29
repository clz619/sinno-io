package win.sinno.io.xsl;

import java.io.IOException;
import org.junit.Test;
import win.sinno.io.csv.CsvWriter;

/**
 * xls 2 csv
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-27 15:53.
 */
public class Xls2CsvTest {

  @Test
  public void testXls2Csv() throws IOException {
    Long bt = System.currentTimeMillis();
    CsvWriter csvWriter = new CsvWriter();
    csvWriter.setCharset("utf-8");
    csvWriter.setAppendMode(true);
    csvWriter.setFileName("1111");
    csvWriter.setOutPath("/Users/chenlizhong/Documents/");
    csvWriter.build();

    String[] header = {"手机号", "昵称"};
    csvWriter.append(header);
    csvWriter.newLine();
    csvWriter.flush();

    String filename = "/Users/chenlizhong/Documents/seller_send_coupon_0_52780.xls";
    int minColumns = -1;

    Xls2Csv xls2csv = new Xls2Csv(filename, csvWriter, minColumns);

    xls2csv.process();

    csvWriter.flush();
    csvWriter.close();
    Long et = System.currentTimeMillis();

    System.out.println(et - bt);
  }
}
