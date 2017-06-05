package win.sinno.io.csv;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.sinno.io.util.FileUtil;

import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * csv
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-27 14:23.
 */
public class CsvWriter {

    private static final Logger LOG = LoggerFactory.getLogger(CsvWriter.class);

    private String outPath;
    private String fileName;

    private boolean appendMode;

    private File csvFile = null;
    private FileOutputStream fos = null;
    private OutputStreamWriter osw = null;
    private BufferedWriter bw = null;

    public CsvWriter() {
    }

    public CsvWriter(String outPath, String fileName) {
        this.outPath = outPath;
        this.fileName = fileName;
    }

    public CsvWriter(String outPath, String fileName, boolean appendMode) {
        this.outPath = outPath;
        this.fileName = fileName;
        this.appendMode = appendMode;
    }

    public CsvWriter setOutPath(String outPath) {
        this.outPath = outPath;
        return this;
    }

    public CsvWriter setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public CsvWriter setAppendMode(boolean appendMode) {
        this.appendMode = appendMode;
        return this;
    }

    public File build() throws IOException {

//        String filePath = null;
//        if (outPath.endsWith(File.separator)) {
//            filePath = outPath + fileName + ".csv";
//        } else {
//            filePath = outPath + File.separator + fileName + ".csv";
//        }

        String filePath = FileUtil.getFilePath(outPath, fileName, ".csv");

        csvFile = new File(filePath);
        if (!csvFile.exists()) {
            File pathFile = csvFile.getParentFile();
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            csvFile.createNewFile();
        }

        fos = new FileOutputStream(csvFile, true);
        osw = new OutputStreamWriter(fos, "UTF-8");
        bw = new BufferedWriter(osw, 1024);

        return csvFile;
    }

    public void append(String[] data) throws IOException {

        if (data == null || data.length == 0) {
            return;
        }

        int len = data.length;
        StringBuilder sb = new StringBuilder();
        String record = null;
        for (int i = 0; i < len; ) {
            record = data[i];
            boolean needQuote = false;

            if (record.contains("\"")) {
                record = record.replaceAll("\"", "\"\"");
                needQuote = true;
            }

            if (record.contains(",") || record.contains(" ")) {
                needQuote = true;
            }

            if (needQuote) {
                sb.append('"');
            }

            sb.append(record);

            if (needQuote) {
                sb.append('"');
            }

            if (++i < len) {
                sb.append(",");
            }
        }

        write(sb.toString());
    }

    public void append(List<String> data) throws IOException {

        if (CollectionUtils.isEmpty(data)) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        Iterator<String> dataIterator = data.iterator();
        String record = null;
        while (dataIterator.hasNext()) {
            record = dataIterator.next();
            boolean needQuote = false;

            if (record.contains("\"")) {
                record = record.replaceAll("\"", "\"\"");
                needQuote = true;
            }

            if (record.contains(",") || record.contains(" ")) {
                needQuote = true;
            }

            if (needQuote) {
                sb.append('"');
            }

            sb.append(record);

            if (needQuote) {
                sb.append('"');
            }

            if (dataIterator.hasNext()) {
                sb.append(",");
            }
        }

        write(sb.toString());
    }

    public void write(String str) throws IOException {
        bw.write(str);
    }

    public void newLine() throws IOException {
        bw.newLine();
    }

    public void flush() throws IOException {
        bw.flush();
    }

    public void close() throws IOException {
        if (bw != null) {
            bw.close();
        }
        if (osw != null) {
            osw.close();
        }
        if (fos != null) {
            fos.close();
        }
    }

}
