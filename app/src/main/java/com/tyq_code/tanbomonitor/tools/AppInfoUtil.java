package com.tyq_code.tanbomonitor.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class AppInfoUtil {

    private PackageManager pm;

    public AppInfoUtil(Context context) {// 通过包管理器，检索所有的应用程序
        pm = context.getPackageManager();
    }

    /**
     * 通过包名返回一个应用的Application对象
     */
    public ApplicationInfo getAppInfo(String pkgName) {
        if (pkgName == null) {
            return null;
        }
        try {
            PackageInfo packageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_CONFIGURATIONS);
//            Log.i("getPackageInfo","Getting package info now: " + packageInfo.packageName);
            return packageInfo.applicationInfo;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("getPackageInfo","Error");
            e.printStackTrace();
        }
//        Log.i("getPackageInfo","Return null.");
        return null;
    }

}
