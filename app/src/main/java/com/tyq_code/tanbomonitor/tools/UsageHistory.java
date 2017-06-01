package com.tyq_code.tanbomonitor.tools;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.tyq_code.tanbomonitor.Constant;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UsageHistory {
    Context context;

    public UsageHistory(Context context) {
        this.context = context;
    }

    public boolean query(){
        return getSystemUsageHistory();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean getSystemUsageHistory() {
        if (hasModule()) {
            boolean canWork = getUsageStats();
            if (!canWork) {
                Log.e("getSystemUsageHistory", "没有打开安全权限");
                Toast.makeText(context, "请打开TanboMonitor的安全权限", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //非Activity不加flag会报错
                context.startActivity(intent);
                return false;
            }
            return true;
        } else {
            Log.e("test", "小于5.0，没这个功能");
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean hasModule() {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean getUsageStats() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        long end_time = calendar.getTimeInMillis();
        long start_time = end_time - 200000;

        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getApplicationContext().getSystemService(Constant.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start_time, end_time);
        return !(queryUsageStats == null || queryUsageStats.isEmpty());
    }
}
