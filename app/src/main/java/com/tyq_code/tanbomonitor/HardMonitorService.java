package com.tyq_code.tanbomonitor;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.tyq_code.tanbomonitor.tools.AppInfoUtil;
import com.tyq_code.tanbomonitor.tools.WhiteListTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HardMonitorService extends Service {

    private boolean timeUp = false;
    private final Timer timer = new Timer();
    private ArrayList<String> whiteList;
    private int monitorTime;
    private Intent intent = new Intent(Constant.STRONG_MONITOR_SERVICE);

    public HardMonitorService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i("HardMonitorService", "onStartCommand");
        monitorTime = intent.getIntExtra("monitor_time", 0);
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        ((TanboApplication)getApplication()).serviceON = true;

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!timeUp) {
//                    Log.i("thread", "ON");
                    killForegroundApps();
//                    Log.i("thread", "sleep for 2s");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
//                        Log.e("thread", "sleep dead");
                        e.printStackTrace();
                    }
                }
            }
        });

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timeUp = true;
                intent.putExtra("ID", HardMonitorActivity.MsgReceiver.ID_TIME_UP);
                sendBroadcast(intent);
            }
        };

        WhiteListTable whiteListTable = new WhiteListTable(this);
        TanboApplication tanboApplication = (TanboApplication) getApplication();
        whiteList = tanboApplication.whiteList;
        Cursor cursor = whiteListTable.getPackageInfo();
        while (cursor.moveToNext()){
            if (!whiteList.contains(cursor.getString(1)))
                whiteList.add(cursor.getString(1));
        }

        timer.schedule(timerTask, monitorTime * 60000);
        thread.start();
    }

    private void killForegroundApps() {
        String foregroundApp = getForegroundApp();
        AppInfoUtil appUtil = new AppInfoUtil(this);
        if (foregroundApp == null || foregroundApp.equals(Constant.PACKAGE_NAME)) {
//            Log.i("ForegroundApp=null/this", "" + foregroundApp);
            return; //过滤空应用和本应用
        }
        if ((appUtil.getAppInfo(foregroundApp).flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
//            Log.i("ForegroundApp=sys", "" + foregroundApp);
            return; //过滤系统应用
        }
        if (whiteList.contains(foregroundApp)) {
//            Log.i("ForegroundApp=white", "" + foregroundApp);
            return; //过滤白名单应用
        }
//        Log.i("ForegroundApp", "" + foregroundApp);
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        Log.i("KillApp", "" + foregroundApp);
        if (!((TanboApplication)getApplication()).picActivityShowing) {
            Intent intent = new Intent(getBaseContext(), NoPhoneActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(intent);
            ((TanboApplication)getApplication()).picActivityShowing = true;
        }
//        Toast.makeText(getBaseContext(), "已关闭" + foregroundApp + "应用", Toast.LENGTH_LONG).show();
        am.killBackgroundProcesses(foregroundApp);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String getForegroundApp() {
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Constant.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 60000, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return null;
        }
        UsageStats recentStats = queryUsageStats.get(0);
        for (UsageStats usageStats : queryUsageStats) {
            if (recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                recentStats = usageStats;
            }
        }
        return recentStats.getPackageName();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {

//        Log.i("HardMonitorService", "onDestroy");

        super.onDestroy();
        timer.cancel();
        timeUp = true;
        intent.putExtra("ID", HardMonitorActivity.MsgReceiver.ID_TIME_UP);
        sendBroadcast(intent);
        ((TanboApplication)getApplication()).serviceON = false;
        Toast.makeText(getBaseContext(), "计时已结束", Toast.LENGTH_LONG).show();
    }
}
