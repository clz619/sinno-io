package win.sinno.io.xslx;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * xlsx 2 csv
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-27 14:09.
 */
public class Xlsx2CsvTest {

    @Test
    public void t() throws OpenXML4JException, SAXException, IOException {
        String filePath = "D:\\data\\ddy_market_xls\\30w.xlsx";
        int minColumns = -1;
        // The package open is instantaneous, as it should be.
        OPCPackage p = OPCPackage.open(filePath, PackageAccess.READ);
        Xlsx2Csv xlsx2csv = new Xlsx2Csv(p, System.out, minColumns);
        xlsx2csv.process();
        p.close();
    }

}
