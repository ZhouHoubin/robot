package z.houbin.robot.device;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import z.houbin.robot.handler.BaseHandler;

public class DeviceManagerHandler extends BaseHandler {
    private DevicePolicyManager manager;
    private final ComponentName componentName;

    public DeviceManagerHandler(Context mContext) {
        super(mContext);
        //获取设备管理服务
        manager = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
        //DeviceReceiver 继承自 DeviceAdminReceiver
        componentName = new ComponentName(mContext, DeviceReceiver.class);
    }

    private boolean isActive() {
        //判断是否激活  如果没有就启动激活设备
        if (!manager.isAdminActive(componentName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "锁屏功能需要设备管理器");
            mContext.startActivity(intent);
            return false;
        }
        return true;
    }

    public void lock() {
        if (isActive()) {
            manager.lockNow();
        }
    }

    public void unLock() {
        //屏幕
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(1000);
        }

        //键盘
        KeyguardManager km = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        if (km != null) {
            KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
            kl.disableKeyguard();
        }
    }
}
