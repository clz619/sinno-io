package win.sinno.io.xslx;

import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.junit.Test;
import org.xml.sax.SAXException;
import win.sinno.io.csv.CsvWriter;

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
//    Xlsx2Csv xlsx2csv = new Xlsx2Csv(p, System.out, minColumns);
//    xlsx2csv.process();
//    p.close();
  }


  public static String OUT_PATH = "/Users/chenlizhong/Documents/output-mobiles/";

  // lengmo11csv
  @Test
  public void tr1() throws OpenXML4JException, SAXException, IOException {

    System.out.println("begin");
    long bt = System.currentTimeMillis();

    int[] iarray = {14, 15, 16, 17, 18, 19, 20, 22};

    for (int i : iarray) {

      String filePath = "/Users/chenlizhong/Documents/old/100W-" + i + ".xlsx";

      CsvWriter csvWriter = new CsvWriter();
      csvWriter.setFileName("" + i);
      csvWriter.setOutPath(OUT_PATH);
      csvWriter.setAppendMode(true);
      csvWriter.build();
      csvWriter.setBom();

      String[] header = {"手机号", "昵称"};
      csvWriter.append(header);
      csvWriter.newLine();
      csvWriter.flush();

      int minColumns = -1;

      OPCPackage p = OPCPackage.open(filePath, PackageAccess.READ);
      Xlsx2Csv xlsx2csv = new Xlsx2Csv(p, csvWriter, minColumns);
      xlsx2csv.process();
      p.close();
      csvWriter.close();
    }
    long et = System.currentTimeMillis();

    System.out.println("end." + (et - bt));
  }


  public static String OUT_PATH2 = "/Users/chenlizhong/Documents/lengmo11csv/";

  // lengmo11csv
  @Test
  public void tr2() throws OpenXML4JException, SAXException, IOException {

    System.out.println("begin");
    long bt = System.currentTimeMillis();

    int[] iarray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

    for (int i : iarray) {
      String filePath = "/Users/chenlizhong/Documents/冷漠双十一/双十一" + i + ".xlsx";

      CsvWriter csvWriter = new CsvWriter();
      csvWriter.setFileName("双十一" + i);
      csvWriter.setOutPath(OUT_PATH2);
      csvWriter.setAppendMode(true);
      csvWriter.build();
      csvWriter.setBom();

//      String[] header = {"手机号", "昵称"};
//      csvWriter.append(header);
//      csvWriter.newLine();
//      csvWriter.flush();

      int minColumns = -1;

      OPCPackage p = OPCPackage.open(filePath, PackageAccess.READ);
      Xlsx2Csv xlsx2csv = new Xlsx2Csv(p, csvWriter, minColumns);
      xlsx2csv.process();
      p.close();
      csvWriter.close();
    }
    long et = System.currentTimeMillis();

    System.out.println("end." + (et - bt));
  }

}
