package win.sinno.io.smsparse;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import win.sinno.io.FileReader;

/**
 * win.sinno.io.smsparse.SmsCountLogStatTest
 *
 * @author chenlizhong@qipeng.com
 * @date 2018/7/24
 * @description 慢查询
 */
public class SmsCountLogStatTest_SlowCount {

  @Test
  public void countSlow() {
    SmsCountComplexMap smsCountComplexMap = new SmsCountComplexMap();

    String filePath1 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-1-8-6";
    String filePath2 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-2-8-6";

    parse(filePath1, smsCountComplexMap);
    parse(filePath2, smsCountComplexMap);

    Set<SmsCountComplexMod> dayModMap = smsCountComplexMap.getDayModMap();

    String[] steps = {"[0~1000)", "[1000~3000)", "[3000~5000)", "[5000~10000)", "[10000~30000)",
        "[30000~)"};

    Integer[] counts = {0, 0, 0, 0, 0, 0};

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, Calendar.JUNE);
    calendar.set(Calendar.DAY_OF_MONTH, 25);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    Date beginTs = calendar.getTime();
    calendar.add(Calendar.DAY_OF_MONTH, 1);
    Date endTs = calendar.getTime();

    for (SmsCountComplexMod mod : dayModMap) {

      if (mod.getSelectDT().after(beginTs) && mod.getSelectDT().before(endTs)) {
        Long useTs = mod.getUseTs();

        if (useTs < 1000) {
          counts[0]++;
        } else if (useTs < 3000) {
          counts[1]++;
        } else if (useTs < 5000) {
          counts[2]++;
        } else if (useTs < 10000) {
          counts[3]++;
        } else if (useTs < 30000) {
          counts[4]++;
        } else {
          counts[5]++;
        }

      }

    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < steps.length; i++) {
      sb.append("'");
      sb.append(steps[i]);
      sb.append("'");
      sb.append(",");
    }
    sb.setLength(sb.length() - 1);

    System.out.println(sb.toString());

    sb.setLength(0);

    for (int i = 0; i < counts.length; i++) {
      sb.append(counts[i]);
      sb.append(",");
    }
    sb.setLength(sb.length() - 1);

    System.out.println(sb.toString());
  }


  @Test
  public void countTop100User() {
    SmsCountComplexMap smsCountComplexMap = new SmsCountComplexMap();

    String filePath1 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-1-8-6";
    String filePath2 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-2-8-6";

    parse(filePath1, smsCountComplexMap);
    parse(filePath2, smsCountComplexMap);

    Set<SmsCountComplexMod> dayModMap = smsCountComplexMap.getDayModMap();

    Map<Long, Integer> map = new HashMap<>();

    for (SmsCountComplexMod mod : dayModMap) {
      Long uid = mod.getUserId();
      Integer val = map.get(uid);

      if (val == null) {
        val = 0;
      }
      val++;
      map.put(uid, val);
    }

    Comparator<Map.Entry<Long, Integer>> valueComparator = new Comparator<Map.Entry<Long, Integer>>() {
      @Override
      public int compare(Entry<Long, Integer> o1,
          Entry<Long, Integer> o2) {
        return o2.getValue() - o1.getValue();
      }
    };

    // map转换成list进行排序
    List<Entry<Long, Integer>> list = new ArrayList<Entry<Long, Integer>>(map.entrySet());
    // 排序
    Collections.sort(list, valueComparator);
    // 默认情况下，TreeMap对key进行升序排序
    System.out.println("------------map按照value升序排序--------------------");

    int count = 0;
    for (int i = 0; i < 100; i++) {
      Map.Entry<Long, Integer> entry = list.get(i);
      System.out.println(entry.getKey() + ":" + entry.getValue());
      count++;
    }

  }


  @Test
  public void countUserStep() {
    SmsCountComplexMap smsCountComplexMap = new SmsCountComplexMap();

    String filePath1 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-1-8-6";
    String filePath2 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-2-8-6";

    parse(filePath1, smsCountComplexMap);
    parse(filePath2, smsCountComplexMap);

    Set<SmsCountComplexMod> dayModMap = smsCountComplexMap.getDayModMap();

    String[] steps = {"[0~5)", "[5~10)", "[10~20)", "[20~50)", "[50~100)", "[100~200)",
        "[200~500)", "[500~1000)",
        "[1000~)"};

    Integer[] counts = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    Map<Long, Integer> map = new HashMap<>();

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, Calendar.JUNE);
    calendar.set(Calendar.DAY_OF_MONTH, 25);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    Date beginTs = calendar.getTime();
    calendar.add(Calendar.DAY_OF_MONTH, 1);
    Date endTs = calendar.getTime();

    for (SmsCountComplexMod mod : dayModMap) {

//      if (mod.getSelectDT().after(beginTs) && mod.getSelectDT().before(endTs)) {
      // 20180625
      Long uid = mod.getUserId();
      Integer val = map.get(uid);

      if (val == null) {
        val = 0;
      }
      val++;
      map.put(uid, val);
//      }
    }

    for (Map.Entry<Long, Integer> entry : map.entrySet()) {
      Long key = entry.getKey();
      Integer val = entry.getValue();

      if (val < 5) {
        counts[0]++;
      } else if (val < 10) {
        counts[1]++;
      } else if (val < 20) {
        counts[2]++;
      } else if (val < 50) {
        counts[3]++;
      } else if (val < 100) {
        counts[4]++;
      } else if (val < 200) {
        counts[5]++;
      } else if (val < 500) {
        counts[6]++;
      } else if (val < 1000) {
        counts[7]++;
      } else {
        counts[8]++;
      }

    }

    StringBuilder sb = new StringBuilder();
    for (String step : steps) {
      sb.append("'");
      sb.append(step);
      sb.append("'");
      sb.append(",");
    }
    sb.setLength(sb.length() - 1);
    System.out.println(sb.toString());

    sb.setLength(0);

    for (Integer count : counts) {
      sb.append(count);
      sb.append(",");
    }
    sb.setLength(sb.length() - 1);
    System.out.println(sb.toString());

//    sb.append()

  }


  /**
   * 耗时0~1000毫秒的count量
   */
  @Test
  public void countValDist() {
    SmsCountComplexMap smsCountComplexMap = new SmsCountComplexMap();

    String filePath1 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-1-8-6";
    String filePath2 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-2-8-6";

    parse(filePath1, smsCountComplexMap);
    parse(filePath2, smsCountComplexMap);

    Set<SmsCountComplexMod> dayModMap = smsCountComplexMap.getDayModMap();

    String[] steps = {"[0~2)", "[2~100)", "[100~1000)", "[1000~10000)", "[10000~100000)",
        "[100000~1000000)",
        "[1000000~5000000)", "[5000000~)"};

    Integer[] counts = {0, 0, 0, 0, 0, 0, 0, 0};

    Map<Integer, Integer> map = new HashMap<>();

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, Calendar.JUNE);
    calendar.set(Calendar.DAY_OF_MONTH, 25);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    // 2018-06-25 ~ 2018-06-26
    Date beginTs = calendar.getTime();
    calendar.add(Calendar.DAY_OF_MONTH, 1);
    Date endTs = calendar.getTime();

    for (SmsCountComplexMod mod : dayModMap) {

      if (mod.getSelectDT().after(beginTs) && mod.getSelectDT().before(endTs)) {
        // 20180625
//      Long uid = mod.getUserId();
        Integer c = mod.getCount();
        Integer val = map.get(c);

        if (val == null) {
          val = 0;
        }

        val++;
        map.put(c, val);
      }
    }

    for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
      Integer key = entry.getKey();
      Integer val = entry.getValue();

      // 设置区间段
      if (key < 2) {
        counts[0] += val;
      } else if (key < 100) {
        counts[1] += val;
      } else if (key < 1000) {
        counts[2] += val;
      } else if (key < 10000) {
        counts[3] += val;
      } else if (key < 100000) {
        counts[4] += val;
      } else if (key < 1000000) {
        counts[5] += val;
      } else if (key < 5000000) {
        counts[6] += val;
      } else {
        counts[7] += val;
      }

    }

    StringBuilder sb = new StringBuilder();
    for (String step : steps) {
      sb.append("'");
      sb.append(step);
      sb.append("'");
      sb.append(",");
    }
    sb.setLength(sb.length() - 1);
    System.out.println(sb.toString());

    sb.setLength(0);

    for (Integer count : counts) {
      sb.append(count);
      sb.append(",");
    }
    sb.setLength(sb.length() - 1);
    System.out.println(sb.toString());


  }


  /**
   * 耗时[0~2)次的分布
   */
  @Test
  public void countVal0_2UseTs() {
    SmsCountComplexMap smsCountComplexMap = new SmsCountComplexMap();

    String filePath1 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-1-8-6";
    String filePath2 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-2-8-6";

    parse(filePath1, smsCountComplexMap);
    parse(filePath2, smsCountComplexMap);

    Set<SmsCountComplexMod> dayModMap = smsCountComplexMap.getDayModMap();

    String[] steps = {"[0~1000)", "[1000~3000)", "[3000~5000)", "[5000~10000)", "[10000~30000)",
        "[30000~)"};
    Integer[] counts = {0, 0, 0, 0, 0, 0};

    Map<Long, Integer> map = new HashMap<>();

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, Calendar.JUNE);
    calendar.set(Calendar.DAY_OF_MONTH, 25);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    // 2018-06-25 ~ 2018-06-26
    Date beginTs = calendar.getTime();
    calendar.add(Calendar.DAY_OF_MONTH, 1);
    Date endTs = calendar.getTime();

    for (SmsCountComplexMod mod : dayModMap) {

//      if (mod.getSelectDT().after(beginTs) && mod.getSelectDT().before(endTs)) {
//      20180625
//      Long uid = mod.getUserId();

      Integer c = mod.getCount();

//      String[] steps = {"[0~2)", "[2~100)", "[100~1000)", "[1000~10000)", "[10000~100000)",
//          "[100000~1000000)",
//          "[1000000~5000000)", "[5000000~)"};
      if (c < 2) {
//      if (c >= 2 && c < 100) {
//      if (c >= 100 && c < 1000) {
//      if (c >= 1000 && c < 10000) {
//      if (c >= 10000 && c < 100000) {
//      if (c >= 100000 && c < 1000000) {
//      if (c >= 1000000 && c < 5000000) {
//      if (c >= 5000000) {

        Long useTs = mod.getUseTs();

        Integer val = map.get(useTs);

        if (val == null) {
          val = 0;
        }

        val++;
        map.put(useTs, val);
      }
//      }

    }

    for (Map.Entry<Long, Integer> entry : map.entrySet()) {
      Long key = entry.getKey();
      Integer val = entry.getValue();

      // 设置区间段
      if (key < 1000) {
        counts[0] += val;
      } else if (key < 3000) {
        counts[1] += val;
      } else if (key < 5000) {
        counts[2] += val;
      } else if (key < 10000) {
        counts[3] += val;
      } else if (key < 30000) {
        counts[4] += val;
      } else {
        counts[5] += val;
      }

    }

    StringBuilder sb = new StringBuilder();
    for (String step : steps) {
      sb.append("'");
      sb.append(step);
      sb.append("'");
      sb.append(",");
    }
    sb.setLength(sb.length() - 1);
    System.out.println(sb.toString());

    sb.setLength(0);

    for (Integer count : counts) {
      sb.append(count);
      sb.append(",");
    }
    sb.setLength(sb.length() - 1);
    System.out.println(sb.toString());


  }


  /**
   * 耗时[0~2)次的分布
   */
  @Test
  public void countSlowLog() {
    SmsCountComplexMap smsCountComplexMap = new SmsCountComplexMap();

    String filePath1 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-1-8-6";
    String filePath2 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-2-8-6";

    parse(filePath1, smsCountComplexMap);
    parse(filePath2, smsCountComplexMap);

    Set<SmsCountComplexMod> dayModMap = smsCountComplexMap.getDayModMap();

    String[] steps = {"[0~1000)", "[1000~3000)", "[3000~5000)", "[5000~10000)", "[10000~30000)",
        "[30000~)"};
    Integer[] counts = {0, 0, 0, 0, 0, 0};

    Map<Long, Integer> map = new HashMap<>();

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.MONTH, Calendar.JUNE);
    calendar.set(Calendar.DAY_OF_MONTH, 25);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    // 2018-06-25 ~ 2018-06-26
    Date beginTs = calendar.getTime();
    calendar.add(Calendar.DAY_OF_MONTH, 1);
    Date endTs = calendar.getTime();

    int count = 0;

    for (SmsCountComplexMod mod : dayModMap) {

//      if (mod.getSelectDT().after(beginTs) && mod.getSelectDT().before(endTs)) {
//      20180625
//      Long uid = mod.getUserId();

      Integer c = mod.getCount();

//      String[] steps = {"[0~2)", "[2~100)", "[100~1000)", "[1000~10000)", "[10000~100000)",
//          "[100000~1000000)",
//          "[1000000~5000000)", "[5000000~)"};
//      if (c < 2) {
//      if (c >= 2 && c < 100) {
//      if (c >= 100 && c < 1000) {
//      if (c >= 1000 && c < 10000) {
      if (c >= 10000 && c < 100000) {
//      if (c >= 100000 && c < 1000000) {
//      if (c >= 1000000 && c < 5000000) {
//      if (c >= 5000000) {

        Long useTs = mod.getUseTs();
//        String[] steps = {"[0~1000)", "[1000~3000)", "[3000~5000)", "[5000~10000)", "[10000~30000)",
//            "[30000~)"};

//        if (useTs < 1000) {
//        if (useTs >= 1000 && useTs < 3000) {
//        if (useTs >= 3000 && useTs < 5000) {
//        if (useTs >= 5000 && useTs < 10000) {
        if (useTs >= 10000 && useTs < 30000) {
//        if (useTs >= 30000) {
          System.out.println(mod);

          count++;
        }
      }

//      }

    }

  }


  public void parse(String filePath, SmsCountComplexMap smsCountComplexMap) {
    File ctrlDir = new File(filePath);
    File[] logFiles = ctrlDir.listFiles();

    int count = 0;

    for (File logFile : logFiles) {
      FileReader fileReader = new FileReader(logFile.getParent(), logFile.getName(), "utf-8");

      try {

        fileReader.open();

        String line = null;
        while ((line = fileReader.readLine()) != null
//            && count < 10
            ) {

          if (line.contains("sms-count")) {
            int spNowCount = 0;
            int spMaxCount = 7;
            int idx = 0;

            // sms-count
            // sms-countComplex-suc
            // sms-countFailed-suc
            // sms-countSuccess-suc

            SmsCountComplexMod smsCountComplexMod = new SmsCountComplexMod();
            while (spNowCount < spMaxCount
                && (idx = line.indexOf(",")) >= 0) {
              while ((idx = line.indexOf(",")) == 0) {
                idx++;
                line = line.substring(idx);
              }
              String str = (line.substring(0, idx));

              line = line.substring(idx);
              spNowCount++;

              if (spNowCount == 1) {
                smsCountComplexMod.setSelectDT(DateUtils.parseDate(str, "yyyy-MM-dd HH:mm:ss"));
              } else if (spNowCount == 4) {
                smsCountComplexMod.setSessionId(str);
              } else if (spNowCount == 5) {
                smsCountComplexMod.setKey(str);
              } else if (spNowCount == 6) {

                smsCountComplexMod.setUserId(Long.valueOf(str.split("=")[1]));
              } else if (spNowCount == 7) {
                if (smsCountComplexMod.getKey().equals("sms-count")) {
                  smsCountComplexMod.setInter(str.contains("true"));
                } else {
                  String[] carr = str.split(":");
                  smsCountComplexMod.setCount(Integer.valueOf(carr[1]));
                }
              }
            }

            smsCountComplexMod.setQuery(line.substring(1));

            smsCountComplexMap.addSmsCount(smsCountComplexMod);

            count++;

          }
        }


      } catch (Exception e) {

        e.printStackTrace();

      } finally {
        fileReader.close();
      }
    }
  }

  public static class SmsCountStatAll {

    private SmsCountStat smsCountAllStat = new SmsCountStat();
    private SmsCountStat smsCountSucStat = new SmsCountStat();
    private SmsCountStat smsCountFailStat = new SmsCountStat();

    public void addSmsCountMod(SmsCountComplexMod smsCountComplexMod) {
      String key = smsCountComplexMod.getKey();

      // sms-count
      // sms-countComplex-suc
      // sms-countFailed-suc
      // sms-countSuccess-suc

      if (key.equals("sms-countComplex-suc")) {
        smsCountAllStat.addSmsCountMod(smsCountComplexMod);
      } else if (key.equals("sms-countSuccess-suc")) {
        smsCountSucStat.addSmsCountMod(smsCountComplexMod);
      } else if (key.equals("sms-countFailed-suc")) {
        smsCountFailStat.addSmsCountMod(smsCountComplexMod);
      }

    }

    public SmsCountStat getSmsCountAllStat() {
      return smsCountAllStat;
    }

    public SmsCountStat getSmsCountSucStat() {
      return smsCountSucStat;
    }

    public SmsCountStat getSmsCountFailStat() {
      return smsCountFailStat;
    }

    @Override
    public String toString() {
      return "SmsCountStatAll{" +
          "smsCountAllStat=" + smsCountAllStat +
          ", smsCountSucStat=" + smsCountSucStat +
          ", smsCountFailStat=" + smsCountFailStat +
          '}';
    }
  }

  public static class SmsCountStat {

    private int total = 0;

    private Map<String, Integer> dayStat = new HashMap<>();

    private Map<Long, Map<String, Integer>> userStat = new HashMap<>();

    // than 3 seconds
    private Set<SmsCountComplexMod> longTimeCountSet = new HashSet<>();

    public void addSmsCountMod(SmsCountComplexMod smsCountComplexMod) {

      Date dt = smsCountComplexMod.getSelectDT();
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(dt);

      String s =
          calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar
              .get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY);

//      String s = calendar.get(Calendar.HOUR_OF_DAY) + "";

      total++;

      addDayStat(s);

      addUserDayStat(smsCountComplexMod.getUserId(), s);

      if (smsCountComplexMod.getUseTs() > 3000) {
        longTimeCountSet.add(smsCountComplexMod);
      }

    }

    public void addDayStat(String day) {
      Integer dayStatVal = dayStat.get(day);

      if (dayStatVal == null) {
        dayStatVal = 1;

      } else {
        dayStatVal += 1;
      }
      dayStat.put(day, dayStatVal);
    }

    public void addUserDayStat(Long userId, String day) {
      Map<String, Integer> userStatVal = userStat.get(day);

      if (userStatVal == null) {
        userStatVal = new HashMap<>();
        userStatVal.put(day, 1);

        userStat.put(userId, userStatVal);
      } else {

        Integer countVal = userStatVal.get(day);
        if (countVal == null) {
          countVal = 1;
        } else {
          countVal += 1;
        }

        userStatVal.put(day, countVal);
      }

    }

    public int getTotal() {
      return total;
    }

    public Map<String, Integer> getDayStat() {
      return dayStat;
    }

    public Map<Long, Map<String, Integer>> getUserStat() {
      return userStat;
    }

    public Set<SmsCountComplexMod> getLongTimeCountSet() {
      return longTimeCountSet;
    }

    @Override
    public String toString() {
      return "SmsCountStat{" +
          "total=" + total +
          ",dayStat=" + dayStat +
          ", userStat=" + userStat +
          ", longTimeCountSet=" + longTimeCountSet +
          '}';
    }
  }

  public static class SmsCountComplexMap {

    private Map<String, SmsCountComplexMod> smsCountMap = new HashMap<>();

    private Set<SmsCountComplexMod> dayModMap = new HashSet<>();

    public void addSmsCount(SmsCountComplexMod smsCountComplexMod) {

      if ("sms-count".equals(smsCountComplexMod.getKey())) {
        smsCountMap.put(smsCountComplexMod.getSessionId(),
            smsCountComplexMod);

      } else {
        SmsCountComplexMod old = smsCountMap.get(smsCountComplexMod.getSessionId());

        if (old != null) {

          smsCountComplexMod.setInter(old.isInter());
          smsCountComplexMod
              .setUseTs(smsCountComplexMod.getSelectDT().getTime() - old.getSelectDT().getTime());
          smsCountComplexMod.setSelectDT(old.getSelectDT());

          dayModMap.add(smsCountComplexMod);

        } else {

          dayModMap.add(smsCountComplexMod);

        }

      }
    }

    public Map<String, SmsCountComplexMod> getSmsCountMap() {
      return smsCountMap;
    }

    public Set<SmsCountComplexMod> getDayModMap() {
      return dayModMap;
    }
  }

  public static class SmsCountComplexMod {

    private Date selectDT;

    private String sessionId;

    private String key;

    private Long userId;

    private boolean isInter;

    private int count;

    private String query;

    private long useTs;

    public Date getSelectDT() {
      return selectDT;
    }

    public void setSelectDT(Date selectDT) {
      this.selectDT = selectDT;
    }

    public String getSessionId() {
      return sessionId;
    }

    public void setSessionId(String sessionId) {
      this.sessionId = sessionId;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public Long getUserId() {
      return userId;
    }

    public void setUserId(Long userId) {
      this.userId = userId;
    }

    public boolean isInter() {
      return isInter;
    }

    public void setInter(boolean inter) {
      isInter = inter;
    }

    public int getCount() {
      return count;
    }

    public void setCount(int count) {
      this.count = count;
    }

    public String getQuery() {
      return query;
    }

    public void setQuery(String query) {
      this.query = query;
    }

    public long getUseTs() {
      return useTs;
    }

    public void setUseTs(long useTs) {
      this.useTs = useTs;
    }

    @Override
    public String toString() {
      return
          "selectDT=" + selectDT +
              ", sessionId='" + sessionId + '\'' +
              ", key='" + key + '\'' +
              ", userId=" + userId +
              ", isInter=" + isInter +
              ", count=" + count +
              ", query='" + query + '\'' +
              ", useTs=" + useTs;
    }
  }


}
