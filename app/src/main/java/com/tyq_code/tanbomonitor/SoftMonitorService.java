package com.tyq_code.tanbomonitor;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.tyq_code.tanbomonitor.tools.AppInfo;
import com.tyq_code.tanbomonitor.tools.DBMS;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SoftMonitorService extends Service {

    private ActivityManager activityManager;
    private ComponentName cn;
    private ArrayList<AppInfo> appList;
    private ArrayList<String> packageNameList;
    private AppInfo appInfo;
    private String lastActivePackageName;
    private DBMS dbms;
    private TanboApplication application;

    public SoftMonitorService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("service", "onCreate");
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        application = (TanboApplication) getApplication();
        dbms = new DBMS(this);
        appList = application.appList = dbms.getAppList();
        packageNameList = application.packageNameList = dbms.getPackageNameList();
        Timer monitorTimer = new Timer();
        MonitorTimerTask monitorTimerTask = new MonitorTimerTask();
        monitorTimer.schedule(monitorTimerTask, 0, Constant.CHECK_TIME);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class MonitorTimerTask extends TimerTask{
        @Override
        public void run() {
            cn = activityManager.getRunningTasks(1).get(0).topActivity;
            String packageName = cn.getPackageName();
            if(packageNameList.contains(packageName)){
                appInfo = appList.get(packageNameList.indexOf(packageName));
                appInfo.time += Constant.CHECK_TIME / 1000;
                if (!packageName.equals(lastActivePackageName))
                    appInfo.frequency++;
            } else {
                packageNameList.add(packageName);
                appInfo = new AppInfo(null, packageName, 0, 0, null);
                appList.add(appInfo);
            }
            lastActivePackageName = packageName;
            Log.e("monitor", "Name:"+appInfo.packageName+" time:"+appInfo.time/Constant.CHECK_TIME+" frequency:"+appInfo.frequency);
            dbms.store(packageNameList, appList);
        }
    }
}