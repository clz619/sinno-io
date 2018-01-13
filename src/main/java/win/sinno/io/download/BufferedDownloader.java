package win.sinno.io.download;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.io.util.FileUtil;

/**
 * win.sinno.io.download.BufferedDownloader
 *
 * @author chenlizhong@qipeng.com
 * @date 2017/12/29
 */
public class BufferedDownloader implements IDownloader {

  private static final Logger LOG = LoggerFactory.getLogger(BufferedDownloader.class);

  private int byteSize = 4096;

  public static final Long DEFAULT_TIMEOUT = 10000l;

  @Override
  public void download(String url, String path) {

    download(url, path, DEFAULT_TIMEOUT);

  }

  @Override
  public void download(String url, String path, Long timeout) {
    URL u = null;
    BufferedInputStream bis = null;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;

    try {
      u = new URL(url);

      URLConnection conn = u.openConnection();

      conn.setConnectTimeout(timeout != null ? timeout.intValue() : DEFAULT_TIMEOUT.intValue());

      bis = new BufferedInputStream(conn.getInputStream());

      File file = new File(path);

      File parent = file.getParentFile();
      if (!parent.exists()) {
        parent.mkdirs();
      }

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
