package z.houbin.robot.handler;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import z.houbin.robot.Data;

public class AppHandler extends BaseHandler {
    private static AppHandler m;

    public static AppHandler getInstance(Context context) {
        if (m == null) {
            m = new AppHandler(context);
        }
        return m;
    }

    private AppHandler(Context mContext) {
        super(mContext);
    }

    public List<UsageStats> getRunningProcess() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -30);
        long ts = calendar.getTimeInMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts, System.currentTimeMillis());
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return null;
        }
        return queryUsageStats;
    }

    public boolean canGetRunningProcess() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
        List queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, System.currentTimeMillis());
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }

    public void openUsageSeetings() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        mContext.startActivity(intent);
    }

    public List<PackageInfo> getInstallPackages() {
        PackageManager pm = mContext.getPackageManager();
        // Return a List of all packages that are installed on the device.
        return pm.getInstalledPackages(0);
    }

    private PackageManager getPackageManager() {
        return mContext.getPackageManager();
    }

    public Data openApp(String name) {
        Data data = new Data();
        List<PackageInfo> packageInfos = getInstallPackages();
        Intent intent = null;

        List<Object> selectPackages = new ArrayList<>();

        for (PackageInfo packageInfo : packageInfos) {
            String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            if (!TextUtils.isEmpty(appName) && appName.contains(name) && packageInfo.applicationInfo.enabled) {
                intent = getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
                selectPackages.add(packageInfo);
            }
        }
        data.setDatas(selectPackages);
        if (intent != null) {
            try {
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                data.setCode(Data.FAILED);
                data.setMsg("打开" + name + "失败了唉");
            }
        }else{
            data.setCode(Data.FAILED);
        }
        return data;
    }


}
