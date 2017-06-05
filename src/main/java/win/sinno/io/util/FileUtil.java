package win.sinno.io.util;

import java.io.File;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/5 16:24
 */
public class FileUtil {
    /**
     * @param path
     * @param fileName
     * @param suffix   ex .csv
     * @return
     */
    public static String getFilePath(String path, String fileName, String suffix) {
        String filePath = null;
        if (path.endsWith(File.separator)) {
            filePath = path + fileName + suffix;
        } else {
            filePath = path + File.separator + fileName + suffix;
        }

        return filePath;
    }
}
