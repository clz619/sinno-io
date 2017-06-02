package win.sinno.io.async.dl;

import win.sinno.io.async.dl.dto.AsyncDownloadTask;

import java.util.List;

/**
 * 异步下载db处理器
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-06-01 14:21.
 */
public interface AsyncDownloadDbHandler {

    /**
     * 设置异步下载任务
     *
     * @param asyncDownloadTask
     */
    void setAsyncDownloadTask(AsyncDownloadTask asyncDownloadTask);

    /**
     * 清空
     */
    void clear();

    /**
     * 获取头部数据
     *
     * @return
     */
    String[] getHeader();

    /**
     * 查询
     *
     * @return
     */
    List<String[]> select();

    /**
     * 刷新
     */
    void finish();

}
