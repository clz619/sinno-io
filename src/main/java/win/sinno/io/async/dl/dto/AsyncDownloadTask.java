package win.sinno.io.async.dl.dto;

/**
 * 异步数据库下载任务
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-27 16:23.
 */
public class AsyncDownloadTask {

    /**
     * 任务id
     */
    private long id;

    /**
     *
     */
    private int type;

    /**
     * 参数
     */
    private String params;

    /**
     * 输出路径
     */
    private String outPath;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 执行步骤
     */
    private int status;

    /**
     * 总数
     */
    private int totalCount;

    /**
     * 输出索引
     */
    private int index;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getOutPath() {
        return outPath;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "AsyncDownloadTask{" +
                "id=" + id +
                ", type=" + type +
                ", params='" + params + '\'' +
                ", outPath='" + outPath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", status=" + status +
                ", totalCount=" + totalCount +
                ", index=" + index +
                '}';
    }
}
