package win.sinno.io.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * win.sinno.io.csv.WeikeCsvTest
 *
 * @author admin@chenlizhong.cn
 * @date 2017/11/9
 */
public class WeikeCsvTest {

  //  private String path = "/Users/chenlizhong/Documents/lengmo11csv/双十一1.csv";
  private String path = "/Users/chenlizhong/Documents/phones_1510231861412";

  private static final Pattern PATTERN = Pattern
      .compile("^((14[0-9])|(13[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");

  public static boolean isPhone(String source) {
    return source == null ? false : PATTERN.matcher(source).matches();
  }


  @Test
  public void testWeikeCsv() {
    BufferedReader reader = null;
    int lineNum = 0, errorNum = 0, nickNum = 0, phoneNum = 0;
    long start = System.currentTimeMillis(), end = 0;

    try {
      File file = new File(path);
      long size = file.length(), cursor = 0;
      FileInputStream inputStream = new FileInputStream(path);
      reader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
      // 跳过第一行
      reader.readLine();

      for (String line = reader.readLine(); null != line; line = reader.readLine()) {
        lineNum++;
        cursor += line.length();
        if (StringUtils.isBlank(line)) {
          continue;
        }
        System.out.println(line);
        String[] cols = line.split(",");
        if (cols.length < 1) {
          // 忽略内容只有"，"的行
          continue;
        }
        String phone = cols[0].trim();
        /*
         * 兼容历史订单里面的 '18668042916 格式
				 */
        if (phone.startsWith("'") && phone.length() > 1) {
          phone = phone.substring(1, phone.length());
        }
        if (!isPhone(phone)) {
          errorNum++;
          continue;
          // return "第" + lineNum + "行手机号格式不对，请检查";
        }

        if (cols.length >= 2) {
          String nick = cols[1].trim();
          if (StringUtils.isNotBlank(nick)) {
            nickNum++;
          }
        }
        phoneNum++;


      }

    } catch (Exception e) {
    } finally {
      if (null != reader) {
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    System.out.println("phone number:" + phoneNum);
    System.out.println("error number:" + errorNum);
  }


}
