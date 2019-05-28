package win.sinno.io;

import java.io.UnsupportedEncodingException;
import org.junit.Test;

/**
 * win.sinno.io.EncodeByteTest
 *
 * @author chenlizhong@qipeng.com
 * @date 2019-05-24
 */

public class EncodeByteTest {

  @Test
  public void test() throws UnsupportedEncodingException {
    String str = "璟";
    byte[] bytes = str.getBytes("GB2312");
    System.out.println(new String(bytes, "GBK"));
  }

}
