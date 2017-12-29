package win.sinno.io.download;

/**
 * win.sinno.io.download.IDownloader
 *
 * @author chenlizhong@qipeng.com
 * @description 下载器
 * @date 2017/12/29
 */
public interface IDownloader {

  void download(String url, String path);
  
}
