package win.sinno.io.smsparse;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import win.sinno.io.FileReader;
import win.sinno.io.FileWriter;

/**
 * win.sinno.io.smsparse.YuntunRocketCount
 *
 * @author chenlizhong@qipeng.com
 * @date 2018/8/8
 */
public class YuntunRocketCount {


  @Test
  public void stat0805() {
    // 类型统计 type : statVal
    Map<String, AtomicInteger> typeStat = new HashMap<>();

    Map<String, Map<Integer, AtomicInteger>> typeTodayTsStat = new HashMap<>();

    String filePath = "/Users/chenlizhong/logs/smsCountLog";
    String fileName = "test-rocket.log.2018-08-05.log";

    File file = new File(filePath + "/" + fileName);

    FileReader fileReader = new FileReader(file.getParent()
        , file.getName()
        , "utf-8");

    int readCount = 0;
    try {
      fileReader.open();
      String line = null;

      Calendar c0805 = Calendar.getInstance();
      c0805.set(Calendar.DAY_OF_MONTH, 5);
      c0805.set(Calendar.HOUR_OF_DAY, 0);
      c0805.set(Calendar.MINUTE, 0);
      c0805.set(Calendar.SECOND, 0);
      c0805.set(Calendar.MILLISECOND, 0);

      long ts20180805 = c0805.getTimeInMillis();

      while ((line = fileReader.readLine()) != null
//          && readCount < 10
          ) {
        String[] logArr = line.split("rocket:");

        String opTs = logArr[0];
        String type = logArr[1];
        String tsStr = opTs.substring(0, 19);

        // 创建时间
        Date date = DateUtils.parseDate(tsStr, "yyyy-MM-dd HH:mm:ss");

        Integer todayTs = Math.toIntExact((date.getTime() - ts20180805) / 1000);

        // 今天数据统计
        Map<Integer, AtomicInteger> todayTsStat = typeTodayTsStat.get(type);
        if (todayTsStat == null) {
          todayTsStat = new HashMap<>();
          typeTodayTsStat.put(type, todayTsStat);
        }
        AtomicInteger tsAt = todayTsStat.get(todayTs);
        if (tsAt == null) {
          tsAt = new AtomicInteger();
          todayTsStat.put(todayTs, tsAt);
        }
        tsAt.incrementAndGet();

        // 类型统计数据
        AtomicInteger statCount = typeStat.get(type);
        //
        if (statCount == null) {
          statCount = new AtomicInteger();
          typeStat.put(type, statCount);
        }

        // 加一
        statCount.incrementAndGet();

        readCount++;
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      fileReader.close();
    }

//    System.out.println(typeStat);
//    System.out.println(typeTodayTsStat);

    System.out.println("calu done...");

    Set<String> keys = typeTodayTsStat.keySet();

    for (String key : keys) {
      FileWriter fileWriter = new FileWriter(filePath, "ts-dist-" + key + ".csv", true);

      try {
        fileWriter.build();

        Map<Integer, AtomicInteger> tds = typeTodayTsStat.get(key);
        Set<Entry<Integer, AtomicInteger>> entries = tds.entrySet();

        for (Entry<Integer, AtomicInteger> entry : entries) {
          fileWriter.write(entry.getKey() + "," + entry.getValue());
          fileWriter.newLine();
        }

        fileWriter.flush();

      } catch (IOException e) {
        // 异常抛错
        e.printStackTrace();
      } finally {
        try {
          // 文件关闭
          fileWriter.close();
        } catch (IOException e) {
          e.printStackTrace();
        }

      }

    }
  }

  @Test
  public void calu0805() {
    String filePath = "/Users/chenlizhong/logs/smsCountLog";
//    String fileName = "ts-dist-sms-op-yp.csv";
    String fileName = "ts-dist-rest-failed-yp.csv";

    Map<Integer, AtomicInteger> stat = new HashMap<>();

    FileReader reader = new FileReader(filePath, fileName);

    try {
      reader.open();
      //
      String line = null;

      Map<Integer, Integer> tsCountMap = new HashMap<>();
      while ((line = reader.readLine()) != null) {
        //
        String[] arr = line.split(",");
        Integer ts = Integer.valueOf(arr[0]);
        Integer count = Integer.valueOf(arr[1]);

        tsCountMap.put(ts, count);
      }

      Map<Integer, Integer> hourDistMap = new HashMap<>();

      for (Entry<Integer, Integer> entry : tsCountMap.entrySet()) {
        Integer ts = entry.getKey();
        Integer count = entry.getValue();

        Integer hour = ts / 3600;
        Integer hourCount = hourDistMap.get(hour);

        if (hourCount == null) {
          hourCount = 0;
        }
        hourCount += count;

        hourDistMap.put(hour, hourCount);

      }

      StringBuilder sb = new StringBuilder();
      for (Integer i = 0; i < 24; i++) {
        Integer hourCount = hourDistMap.get(i);
        sb.append(hourCount);
        sb.append(",");
      }
      sb.setLength(sb.length() - 1);

      System.out.println(sb.toString());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      reader.close();
    }

  }


  @Test
  public void calutest() {
    System.out.println(11 / 2); //5
    System.out.println(11 % 2); //1
  }

}
