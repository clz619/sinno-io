package win.sinno.io.xsl;

import org.junit.Test;

import java.io.IOException;

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
        String filename = "D:\\data\\ddy_market_xls\\10w.xls";
        int minColumns = -1;
        Xls2Csv xls2csv = new Xls2Csv(filename, minColumns);
        xls2csv.process();
    }
}
