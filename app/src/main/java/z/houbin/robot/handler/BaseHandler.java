package z.houbin.robot.handler;

import android.content.Context;

public abstract class BaseHandler {
    private static BaseHandler m;
    protected Context mContext;

    protected BaseHandler(Context mContext) {
        this.mContext = mContext;
    }

    public  static BaseHandler getInstance(Context context) {
        if (m == null) {
            m = new BaseHandler(context) {
            };
        }
        return m;
    }
}
