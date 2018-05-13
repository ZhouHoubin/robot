package z.houbin.robot;

import java.util.ArrayList;
import java.util.List;

public class Data {
    public static final int SUCCESS = 0;
    public static final int FAILED = -1;

    private int code;
    private String msg;
    private List<Object> datas = new ArrayList<>();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Object> getDatas() {
        return datas;
    }

    public void setDatas(List<Object> datas) {
        this.datas = datas;
    }
}
