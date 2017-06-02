package win.sinno.io.async.dl;

import java.io.IOException;

/**
 * 异步下载器
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-06-01 13:33.
 */
public interface AsyncDownloader extends Runnable {

    /**
     * 需要下载
     *
     * @return
     */
    boolean isNeedDownload();

    /**
     * 是否已经取消
     *
     * @return
     */
    boolean isCancel();

    /**
     * 是否已经完成
     *
     * @return
     */
    boolean isDone();

    /**
     * 下载
     */
    void download() throws IOException;

}
