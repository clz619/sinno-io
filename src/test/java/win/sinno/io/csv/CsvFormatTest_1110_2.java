package win.sinno.io.csv;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class CsvFormatTest_1110_2 {

  private String OUT_PATH = "/Users/chenlizhong/Documents/dbg2";
  private CsvWriter csvWriter;

  private int idx = 0;
  private int pos = 0;

  private File file;
  private FileInputStream fis;
  private DataInputStream dis;
  private InputStreamReader isr;
  private BufferedReader br;


  @Test
  public void format() throws IOException {

    ListArray lines = new ListArray();
    SetArray mobiles = new SetArray();
    long st = System.currentTimeMillis();

    String sourcePath = "/Users/chenlizhong/Documents/400Wshuj_4109249条.txt";

    try {
      file = new File(sourcePath);  // CSV文件路径

      fis = new FileInputStream(file);

      dis = new DataInputStream(fis);
      isr = new InputStreamReader(dis);
      br = new BufferedReader(isr);

      String[] lineArray = null;
      String nick = null;
      String mobile = null;

      String line = null;
      int j = 0;
      while ((line = br.readLine()) != null
//          && j < 100
          ) {
        mobile = line.trim();

        lineArray = new String[1];

        lineArray[0] = mobile;

        lines.add(lineArray);

        j++;

      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
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

    int step = 1000000;
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
      csvWriter.setCharset("gb18030");
      csvWriter.setAppendMode(true);
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
