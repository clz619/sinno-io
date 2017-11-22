package win.sinno.io.async.dl;

import org.apache.commons.collections4.CollectionUtils;
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

    private static final Logger LOG = LoggerFactory.getLogger("download");

    private AsyncDownloadTask asyncDownloadTask;

    private AsyncDownloadDbHandler asyncDownloadDbHandler;

    private static final int MAX_NUM_COUNT = 3;

    //防止 数据 异常 导致死循环，如count后删除一些数据，导致select永远比count小
    private int nullCount = 0;

    public CsvAsyncDownloader(AsyncDownloadTask asyncDownloadTask, AsyncDownloadDbHandler asyncDownloadDbHandler) {

        this.asyncDownloadTask = asyncDownloadTask;

        //设置下载任务
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
        return !isCancel()
                && !isDone()
                && (asyncDownloadTask.getTotalCount() > asyncDownloadTask.getIndex() && nullCount < MAX_NUM_COUNT);
    }

    /**
     * 是否已经取消
     *
     * @return
     */
    @Override
    public boolean isCancel() {
        return (asyncDownloadTask.getStatus() & AsyncDownloadTaskStatusEnum.CANCEL.getCode()) > 0;
    }

    /**
     * 是否已经完成
     *
     * @return
     */
    @Override
    public boolean isDone() {
        return (asyncDownloadTask.getStatus() & AsyncDownloadTaskStatusEnum.DONE.getCode()) > 0;
    }

    /**
     * 下载
     */
    @Override
    public void download() throws IOException {

        asyncDownloadDbHandler.init();

        CsvWriter csvWriter = new CsvWriter();
        csvWriter.setFileName(asyncDownloadTask.getFileName());
        csvWriter.setOutPath(asyncDownloadTask.getOutPath());
        csvWriter.setAppendMode(true);
        csvWriter.build();

        if (!asyncDownloadDbHandler.isHasSetHeader()) {
            String[] header = asyncDownloadDbHandler.getHeader();
            csvWriter.setBom();
            csvWriter.append(header);
            csvWriter.newLine();
            csvWriter.flush();
        }

        while (isNeedDownload()) {
            List<String[]> dataArrayList = asyncDownloadDbHandler.select();

            if (CollectionUtils.isNotEmpty(dataArrayList)) {

                asyncDownloadTask.setIndex(asyncDownloadTask.getIndex() + dataArrayList.size());

                for (String[] dataArray : dataArrayList) {
                    // 添加数据
                    csvWriter.append(dataArray);
                    csvWriter.newLine();
                }

                csvWriter.flush();

            } else {
                nullCount++;
            }
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
