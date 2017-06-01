package com.tyq_code.tanbomonitor.tools;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public String appName;
    public String packageName;
    public long time;
    public int frequency;
    public Drawable icon;

    public AppInfo(String appName, String packageName, long time, int frequency, Drawable icon) {
        this.appName = appName;
        this.packageName = packageName;
        this.time = time;
        this.frequency = frequency;
        this.icon = icon;
    }
}
