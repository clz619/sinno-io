package win.sinno.io.csv;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

/**
 * win.sinno.io.csv.CsvFormatTest
 *
 * @author admin@chenlizhong.cn
 * @date 2017/11/9
 */
public class CsvFormatTest1 {


  String OUT_PATH = "/Users/chenlizhong/Documents/output3900w";

  CsvWriter csvWriter;

  int idx = 0;
  int pos = 0;

  @Test
  public void scanFile() {
    String sourcePath = "/Users/chenlizhong/Documents/2000w";

    File file = new File(sourcePath);
    File[] childFiles = file.listFiles();
    for (File cfile : childFiles) {
      if (cfile.isDirectory()) {
        File[] csvs = cfile.listFiles(new FileFilter() {
          @Override
          public boolean accept(File pathname) {
            return pathname.getName().endsWith(".csv");
          }
        });

        for (File csv : csvs) {
          System.out.println(csv.getAbsolutePath());


        }

      }
    }
  }

  @Test
  public void format() throws IOException {

    ListArray lines = new ListArray();
    SetArray mobiles = new SetArray();
    long st = System.currentTimeMillis();

    String sourcePath = "/Users/chenlizhong/Documents/mobiles";

    Set<Integer> uft8Idx = new HashSet<>();
    uft8Idx.add(22);
    uft8Idx.add(19);
    uft8Idx.add(18);
    uft8Idx.add(17);
    uft8Idx.add(16);
    uft8Idx.add(15);
    uft8Idx.add(2);
    uft8Idx.add(20);
    uft8Idx.add(14);

    for (int i = 1; i <= 37; i++) {
      int count = 0;
      long ist = System.currentTimeMillis();
      System.out.println("begin " + i);
      String fileName = "100W-" + i;

      CsvReader csvReader = new CsvReader(sourcePath, fileName, "GB18030");
      csvReader.setCharset("GB18030");
      if (uft8Idx.contains(i)) {
        csvReader.setCharset("UTF-8");
      }
      csvReader.open();

      String[] lineArray = null;
      String nick = null;
      String mobile = null;

      String line = null;
      int j = 0;
      while ((line = csvReader.readLine()) != null
//          && j < 100
          ) {
        lineArray = line.split(",");
        if (lineArray.length < 2) {
          continue;
        }

        nick = lineArray[0].trim();
        mobile = lineArray[1].replaceAll("'", "").replaceAll("\"", "").trim();

        lineArray = new String[2];

        lineArray[0] = mobile;
        lineArray[1] = nick;

        if (isMobile(mobile)
            && !mobiles.isContains(mobile)
            ) {
          mobiles.add(mobile);
          lines.add(lineArray);
          count++;
        }

        j++;
      }
      csvReader.close();
      long iet = System.currentTimeMillis();

      System.out.println(i + ".csv count:" + count + " useTs:" + (iet - ist) + "ms");
    }

    String sourcePath2 = "/Users/chenlizhong/Documents/2000w";

    File file = new File(sourcePath2);
    File[] childFiles = file.listFiles();
    for (File cfile : childFiles) {
      if (cfile.isDirectory()) {
        File[] csvs = cfile.listFiles(new FileFilter() {
          @Override
          public boolean accept(File pathname) {
            return pathname.getName().endsWith(".csv");
          }
        });

        for (File csv : csvs) {
          System.out.println(csv.getAbsolutePath());

          int count = 0;
          long ist = System.currentTimeMillis();

          String fileName = csv.getName().substring(0, csv.getName().length() - 4);

          CsvReader csvReader = new CsvReader(csv.getParentFile().getAbsolutePath(), fileName,
              "UTF-8");

          csvReader.open();

          String[] lineArray = null;
          String nick = null;
          String mobile = null;

          String line = null;
          int j = 0;
          while ((line = csvReader.readLine()) != null
//          && j < 100
              ) {
            lineArray = line.split(",");
            if (lineArray.length < 2) {
              continue;
            }

            nick = lineArray[1].trim();
            mobile = lineArray[0].replaceAll("'", "").replaceAll("\"", "").trim();

            lineArray = new String[2];

            lineArray[0] = mobile;
            lineArray[1] = nick;

            if (isMobile(mobile)
                && !mobiles.isContains(mobile)
                ) {
              mobiles.add(mobile);
              lines.add(lineArray);
              count++;
            }

            j++;
          }
          csvReader.close();
          long iet = System.currentTimeMillis();

          System.out.println(".csv 2 count:" + count + " useTs:" + (iet - ist) + "ms");
        }

      }
    }

    mobiles = null;

    System.out.println("read map size" + lines.size());

    long et = System.currentTimeMillis();
    System.out.println("useTs:" + (et - st) + "ms");

    writeCsv(lines);

    et = System.currentTimeMillis();
    System.out.println("write csv useTs:" + (et - st) + "ms");

  }

  private void writeCsv(ListArray listArray) throws IOException {

    List<List<String[]>> liness = listArray.getLines();

    for (List<String[]> lines : liness) {

      for (int i = 0; i < lines.size(); i++) {
        CsvWriter csvWriter = getNowCsvWriter();
        csvWriter.append(lines.get(i));
        csvWriter.newLine();
        csvWriter.flush();
        pos++;
      }

    }

  }


  private CsvWriter getNowCsvWriter() throws IOException {

    int step = 960000;
    if (csvWriter != null && (pos % step) != 0) {
      return csvWriter;

    } else {
      if (csvWriter != null) {
        csvWriter.close();
        csvWriter = null;
      }

      csvWriter = new CsvWriter();
      csvWriter.setFileName("" + idx);
      csvWriter.setOutPath(OUT_PATH);
      csvWriter.setAppendMode(true);
      csvWriter.setCharset("GB18030");
      csvWriter.build();
      csvWriter.setBom();

      String[] header = {"手机号", "收货人(可选填)"};
      csvWriter.append(header);
      csvWriter.newLine();
      csvWriter.flush();

      System.out.println("create csv write:" + idx);

      idx++;
    }

    return csvWriter;
  }

  private boolean isMobile(String mobile) {
    if (mobile.length() != 11) {
      return false;
    }

    if (!mobile.startsWith("1")) {
      return false;
    }

    return true;
  }

  @Test
  public void s() {
    System.out.println(11 / 100);
    System.out.println(11 % 100);
  }

  public static class ListArray {

    private List<List<String[]>> lines = new ArrayList<>();

    public List<List<String[]>> getLines() {
      return lines;
    }

    public synchronized void add(String[] strings) {
      int size = lines.size();
      if (size == 0) {
        List<String[]> list = new ArrayList<String[]>();
        lines.add(list);
        list.add(strings);
        System.out.println("add list size:" + lines.size());
      } else {

        List<String[]> list = lines.get(size - 1);
        if (list.size() < 500000) {
          list.add(strings);
        } else {
          List<String[]> list1 = new ArrayList<String[]>();
          lines.add(list1);
          list1.add(strings);
          System.out.println("add list size:" + lines.size());
        }
      }

    }

    public long size() {
      int i = 0;
      if (lines.size() > 0) {

        for (List<String[]> line : lines) {
          i += line.size();
        }
      }

      return i;
    }
  }

  public static class SetArray {

    private List<Set<String>> lines = new ArrayList<>();

    public synchronized void add(String strings) {
      int size = lines.size();
      if (size == 0) {
        Set<String> list = new HashSet<>();
        lines.add(list);
        list.add(strings);

        System.out.println("add set size:" + lines.size());
      } else {

        Set<String> list = lines.get(size - 1);
        if (list.size() < 500000) {
          list.add(strings);
        } else {
          Set<String> list1 = new HashSet<>();
          lines.add(list1);
          list1.add(strings);
          System.out.println("add set size:" + lines.size());
        }
      }

    }

    public boolean isContains(String string) {
      if (lines.size() > 0) {
        for (Set<String> line : lines) {
          if (line.contains(string)) {
            return true;
          }
        }
      }

      return false;
    }
  }

}
