package win.sinno.io.csv;

import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * win.sinno.io.csv.GlobalBlackFormatTest
 *
 * @author chenlizhong@qipeng.com
 * @date 2017/12/5
 */
public class GlobalBlackFormatTest {

  @Test
  public void format() throws IOException {

    // /Users/chenlizhong/Downloads/global_black.csv

    String sourcePath = "/Users/chenlizhong/Downloads";
    String fileName = "global_black";

    String targetPath = "/Users/chenlizhong/Documents";
    CsvReader csvReader = new CsvReader(sourcePath, fileName, "UTF-8");

    csvReader.open();

    CsvWriter csvWriter = new CsvWriter();
    csvWriter.setFileName("wk_black_only_mobile1");
    csvWriter.setOutPath(targetPath);
    csvWriter.setCharset("UTF-8");
    csvWriter.setAppendMode(true);
    csvWriter.build();
    csvWriter.setBom();

    String line = null;

    int i = 0;

//    while (StringUtils.isNotBlank(line = csvReader.readLine()) && i < 10) {
    while (StringUtils.isNotBlank(line = csvReader.readLine())) {

//      System.out.println(line);

      String[] lineArray = line.split("\t");

      if (lineArray.length >= 6 && !lineArray[5].contains("89000")) {
//        for (String l : lineArray) {
//          System.out.println(l);
//        }

        i++;

//        csvWriter.append(lineArray);


        csvWriter.append(Arrays.asList(lineArray[0]));
        csvWriter.newLine();

        if (i % 1000 == 0) {

          csvWriter.flush();
        }
//        System.out.println(lineArray[0]);
      }
    }

    System.out.println(i);

    csvWriter.flush();


  }

}
