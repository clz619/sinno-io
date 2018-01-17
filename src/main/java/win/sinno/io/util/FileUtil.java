package win.sinno.io.util;

import java.io.File;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/5 16:24
 */
public class FileUtil {

  /**
   * @param suffix ex .csv
   */
  public static String getFilePath(String path, String fileName, String suffix) {

    String filePath = getFilePath(path, fileName);

    if (StringUtils.isNotBlank(suffix)) {
      filePath += suffix;
    }

    return filePath;
  }

  public static String getFilePath(String path, String fileName) {
    String filePath = null;
    if (path.endsWith(File.separator)) {
      filePath = path + fileName;
    } else {
      filePath = path + File.separator + fileName;
    }

    return filePath;
  }
}
