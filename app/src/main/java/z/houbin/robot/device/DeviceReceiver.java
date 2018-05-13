package z.houbin.robot.device;

import android.app.admin.DeviceAdminReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 设备管理器
 */
public class DeviceReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        // 设备管理：可用
        Toast.makeText(context, "设备管理：可用", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(final Context context, Intent intent) {
        // 设备管理：不可用
        Toast.makeText(context, "设备管理：不可用", Toast.LENGTH_SHORT).show();
        //如果取消了激活就再次提示激活
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
           /* // 这里处理 不可编辑设备。这里可以造成死机状态
            Intent intent2 = new Intent(context, NoticeSetting.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
            context.stopService(intent);// 是否可以停止*/
        return "禁用后不能自动锁屏了哦";
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        // 设备管理：密码己经改变
        Toast.makeText(context, "设备管理：密码己经改变", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        // 设备管理：改变密码失败
        Toast.makeText(context, "设备管理：改变密码失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        // 设备管理：改变密码成功
        Toast.makeText(context, "设备管理：改变密码成功", Toast.LENGTH_SHORT).show();
    }
}
