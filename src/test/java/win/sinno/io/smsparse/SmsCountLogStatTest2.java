package win.sinno.io.smsparse;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import win.sinno.io.FileReader;

/**
 * win.sinno.io.smsparse.SmsCountLogStatTest
 *
 * @author chenlizhong@qipeng.com
 * @date 2018/7/24
 */
public class SmsCountLogStatTest2 {

  @Test
  public void sms() {
    SmsCountComplexMap smsCountComplexMap = new SmsCountComplexMap();

    String filePath1 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-1-8-6";
    String filePath2 = "/Users/chenlizhong/logs/smsCountLog/ctrl-log-2-8-6";

    parse(filePath1, smsCountComplexMap);
    parse(filePath2, smsCountComplexMap);

    Set<SmsCountComplexMod> dayModMap = smsCountComplexMap.getDayModMap();

    SmsCountStatAll smsCountStatAll = new SmsCountStatAll();

    for (SmsCountComplexMod mod : dayModMap) {
      smsCountStatAll.addSmsCountMod(mod);
    }

    SmsCountStat statAll = smsCountStatAll.getSmsCountAllStat();

    SmsCountStat statSuc = smsCountStatAll.getSmsCountSucStat();

    SmsCountStat statFail = smsCountStatAll.getSmsCountFailStat();

    // sms count stat all

    int dayCount = 24;
    String[] times = new String[dayCount];
    Integer[] allCount = new Integer[dayCount];
    Integer[] sucCount = new Integer[dayCount];
    Integer[] failCount = new Integer[dayCount];

    for (int i = 0; i < dayCount; i++) {
      times[i] = "2018-6-25 " + i + "";
      allCount[i] = statAll.getDayStat().get(times[i]);
      sucCount[i] = statSuc.getDayStat().get(times[i]);
      failCount[i] = statFail.getDayStat().get(times[i]);

      if (allCount[i] == null) {
        allCount[i] = 0;
      }
      if (sucCount[i] == null) {
        sucCount[i] = 0;
      }
      if (failCount[i] == null) {
        failCount[i] = 0;
      }
    }

    StringBuilder tsb = new StringBuilder();
    for (int i = 0; i < times.length; i++) {
      tsb.append("'");
      tsb.append(times[i]);
      tsb.append("'");
      tsb.append(",");
    }

    tsb.setLength(tsb.length() - 1);
    System.out.println(tsb);

    tsb.setLength(0);
    for (int i = 0; i < allCount.length; i++) {
      tsb.append(allCount[i]);
      tsb.append(",");
    }
    tsb.setLength(tsb.length() - 1);
    System.out.println(tsb);

    tsb.setLength(0);
    for (int i = 0; i < sucCount.length; i++) {
      tsb.append(sucCount[i]);
      tsb.append(",");
    }
    tsb.setLength(tsb.length() - 1);
    System.out.println(tsb);

    tsb.setLength(0);
    for (int i = 0; i < failCount.length; i++) {
      tsb.append(failCount[i]);
      tsb.append(",");
    }
    tsb.setLength(tsb.length() - 1);
    System.out.println(tsb);

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
      return "SmsCountComplexMod{" +
          "selectDT=" + selectDT +
          ", sessionId='" + sessionId + '\'' +
          ", key='" + key + '\'' +
          ", userId=" + userId +
          ", isInter=" + isInter +
          ", count=" + count +
          ", query='" + query + '\'' +
          ", useTs=" + useTs +
          '}';
    }
  }


}
