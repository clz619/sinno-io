package win.sinno.io.async.dl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.io.async.dl.dto.AsyncDownloadTask;
import win.sinno.io.util.FileUtil;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017/6/2 16:58
 */
public abstract class LinuxAsyncDownloadDbHandler implements AsyncDownloadDbHandler {

    private static final Logger LOG = LoggerFactory.getLogger("download");

    public abstract AsyncDownloadTask getAsyncDownloadTask();

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private boolean isContinueDownload;

    private boolean isHasCreateFile;

    private boolean isHasSetHeader;

    @Override
    public void init() {
        int count = count();
        getAsyncDownloadTask().setTotalCount(count);

        String fileName = getAsyncDownloadTask().getFileName();
        String outPath = getAsyncDownloadTask().getOutPath();

//        String filePath = null;
//        if (outPath.endsWith(File.separator)) {
//            filePath = outPath + fileName + ".csv";
//        } else {
//            filePath = outPath + File.separator + fileName + ".csv";
//        }

        String filePath = FileUtil.getFilePath(outPath, fileName, ".csv");

        File file = new File(filePath);

        if (file.exists()) {

            isContinueDownload = true;
            isHasCreateFile = true;

            String[] command = new String[]{"/bin/sh", "-c", "cat " + filePath + " | wc -l"};

            try {
                Process process = Runtime.getRuntime().exec(command);

                OutputProcessor out = new OutputProcessor(process.getInputStream());
                OutputProcessor errOut = new OutputProcessor(process.getErrorStream());

                Future<String> future = executorService.submit(out);
                Future<String> errFuture = executorService.submit(errOut);


                int exitVal = process.waitFor();

                if (exitVal == 0) {
                    String successMsg = future.get();

                    String line = successMsg;

                    if (StringUtils.isNotBlank(line)) {
                        line = line.trim();

                        // 行数
                        Integer lineNum = Integer.parseInt(line);
                        if (lineNum > 0) {
                            isHasSetHeader = true;
                        }
                        lineNum = ((lineNum - 1) > 0) ? (lineNum - 1) : 0;
                        getAsyncDownloadTask().setIndex(lineNum);
                        LOG.info("file:{} has data set index:{}", new Object[]{filePath, lineNum});
                    }
                } else {
                    String errMsg = errFuture.get();
                    LOG.error("cat {} | wc -l err:{}", new Object[]{filePath, errMsg});
                }

                process.destroy();

            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }

    }

    @Override
    public boolean isContinueDownload() {
        return isContinueDownload;
    }

    @Override
    public boolean isHasCreateFile() {
        return isHasCreateFile;
    }

    @Override
    public boolean isHasSetHeader() {
        return isHasSetHeader;
    }
}
