package win.sinno.io.download;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * win.sinno.io.download.BufferedDownloader
 *
 * @author chenlizhong@qipeng.com
 * @date 2017/12/29
 */
public class BufferedDownloader implements IDownloader {

  private static final Logger LOG = LoggerFactory.getLogger(BufferedDownloader.class);

  private int byteSize = 4096;

  @Override
  public void download(String url, String path) {

    URL u = null;
    BufferedInputStream bis = null;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;

    try {
      u = new URL(url);
      bis = new BufferedInputStream(u.openStream());

      File file = new File(path);
      fos = new FileOutputStream(file);
      bos = new BufferedOutputStream(fos);

      byte[] buf = new byte[byteSize];
      int len;

      while ((len = bis.read(buf)) != -1) {
        bos.write(buf, 0, len);
      }

    } catch (MalformedURLException e) {
      LOG.error(e.getMessage(), e);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    } finally {

      if (bis != null) {
        try {
          bis.close();
        } catch (IOException e) {
          LOG.error(e.getMessage(), e);
        }
      }

      if (bos != null) {
        try {
          bos.close();
        } catch (IOException e) {
          LOG.error(e.getMessage(), e);
        }
      }

      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          LOG.error(e.getMessage(), e);
        }
      }

    }


  }

}
