package com.tyq_code.tanbomonitor.tools;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public class DBMS {
    private Context context;
    private ArrayList<AppInfo> appList = new ArrayList<>();
    private ArrayList<String> packageNameList = new ArrayList<>();
    private UsageInfoTable usageInfoTable;

    public DBMS(Context context) {
        this.context = context;
        createDatabase();
        readInfoFromDB();
    }

    public void store(ArrayList<String> _packageNameList, ArrayList<AppInfo> _appList) {
        AppInfo tempAppInfo;
        for (AppInfo appInfo : _appList) {
            if (packageNameList.contains(appInfo.packageName)){
                int index = _packageNameList.indexOf(appInfo.packageName);
                tempAppInfo = _appList.get(index);
                appInfo.time = tempAppInfo.time;
                appInfo.frequency = tempAppInfo.frequency;
                usageInfoTable.update(appInfo.packageName, appInfo.time, appInfo.frequency);
            } else {
                appList.add(appInfo);
                packageNameList.add(appInfo.packageName);
                usageInfoTable.insert(appInfo.packageName, appInfo.time, appInfo.frequency);
            }
        }
    }

    private void createDatabase() {
        usageInfoTable = new UsageInfoTable(context);
    }

    private void readInfoFromDB() {
        Cursor cursor = usageInfoTable.getPackageInfo();
        AppInfo info;
        while (cursor.moveToNext()) {
            info = new AppInfo(null, cursor.getString(1), cursor.getLong(2), cursor.getInt(3), null);
            appList.add(info);
            packageNameList.add(info.packageName);
//            Log.e("readInfoFromDB", "" + info.packageName);
        }
    }

    public ArrayList<AppInfo> getAppList() {
        return appList;
    }

    public ArrayList<String> getPackageNameList() {
        return packageNameList;
    }
}
