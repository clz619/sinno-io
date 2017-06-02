package win.sinno.io.async.dl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.io.async.dl.constant.AsyncDownloadTaskStatusEnum;
import win.sinno.io.async.dl.dto.AsyncDownloadTask;
import win.sinno.io.csv.CsvWriter;

import java.io.IOException;
import java.util.List;

/**
 * csv async downloader
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-06-01 13:45.
 */
public class CsvAsyncDownloader implements AsyncDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(CsvAsyncDownloader.class);

    private AsyncDownloadTask asyncDownloadTask;

    private AsyncDownloadDbHandler asyncDownloadDbHandler;


    public CsvAsyncDownloader(AsyncDownloadTask asyncDownloadTask, AsyncDownloadDbHandler asyncDownloadDbHandler) {

        this.asyncDownloadTask = asyncDownloadTask;

        this.asyncDownloadDbHandler = asyncDownloadDbHandler;
        this.asyncDownloadDbHandler.setAsyncDownloadTask(asyncDownloadTask);


    }

    /**
     * 需要下载
     *
     * @return
     */
    @Override
    public boolean isNeedDownload() {
        return !isCancel() && !isDone();
    }

    /**
     * 是否已经取消
     *
     * @return
     */
    @Override
    public boolean isCancel() {
        return (asyncDownloadTask.getStatus() | AsyncDownloadTaskStatusEnum.CANCEL.getCode()) > 0;
    }

    /**
     * 是否已经完成
     *
     * @return
     */
    @Override
    public boolean isDone() {
        return (asyncDownloadTask.getStatus() | AsyncDownloadTaskStatusEnum.DONE.getCode()) > 0;
    }

    /**
     * 下载
     */
    @Override
    public void download() throws IOException {

        asyncDownloadDbHandler.clear();

        CsvWriter csvWriter = new CsvWriter();
        csvWriter.setFileName(asyncDownloadTask.getFileName());
        csvWriter.setOutPath(asyncDownloadTask.getOutPath());
        csvWriter.setAppendMode(true);

        csvWriter.build();

        String[] header = asyncDownloadDbHandler.getHeader();
        csvWriter.append(header);

        while (isNeedDownload()) {
            List<String[]> dataArrayList = asyncDownloadDbHandler.select();

            for (String[] dataArray : dataArrayList) {
                // 添加数据
                csvWriter.append(dataArray);
                csvWriter.newLine();
            }
            csvWriter.flush();
        }
        csvWriter.close();

        // 下载
        asyncDownloadDbHandler.finish();
    }

    @Override
    public void run() {
        try {

            download();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
