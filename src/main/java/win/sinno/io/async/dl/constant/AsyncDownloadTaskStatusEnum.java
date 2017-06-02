package win.sinno.io.async.dl.constant;

/**
 * 异步下载任务状态枚举
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-06-01 13:59.
 */
public enum AsyncDownloadTaskStatusEnum {

    /**
     * 0,新建,new
     */
    NEW(0, "新建", "new"),

    /**
     * 1,进行中,doing
     */
    DOING(1, "进行中", "doing"),

    /**
     * 2,完成,done
     */
    DONE(2, "完成", "done"),

    /**
     * 64,取消,cancel
     */
    CANCEL(64, "取消", "cancel"),;

    AsyncDownloadTaskStatusEnum(int code, String valueCn, String valueEn) {
        this.code = code;
        this.valueCn = valueCn;
        this.valueEn = valueEn;
    }

    private int code;

    private String valueCn;

    private String valueEn;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValueCn() {
        return valueCn;
    }

    public void setValueCn(String valueCn) {
        this.valueCn = valueCn;
    }

    public String getValueEn() {
        return valueEn;
    }

    public void setValueEn(String valueEn) {
        this.valueEn = valueEn;
    }
}
