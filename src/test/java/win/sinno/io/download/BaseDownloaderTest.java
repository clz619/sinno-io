package win.sinno.io.download;

import org.junit.Test;

/**
 * win.sinno.io.download.BaseDownloaderTest
 *
 * @author chenlizhong@qipeng.com
 * @date 2017/12/29
 */
public class BaseDownloaderTest {

  private IDownloader downloader = new BufferedDownloader();

  @Test
  public void testDownloader() {

//    String url = "http://commons.apache.org/images/commons-logo.png";
//    String path = "/Users/chenlizhong/Pictures/commons-logo.png";

    String url = "http://7xk5y4.com1.z0.glb.clouddn.com/pdc-puwei.png";
    String path = "/Users/chenlizhong/Pictures/pdc-puwei.png";
    downloader.download(url, path);
  }

}
