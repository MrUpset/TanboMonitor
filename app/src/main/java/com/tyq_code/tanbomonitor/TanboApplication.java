package com.tyq_code.tanbomonitor;

import android.app.Application;

import com.tyq_code.tanbomonitor.tools.AppInfo;

import java.util.ArrayList;

public class TanboApplication extends Application{
    public ArrayList<AppInfo> appList = new ArrayList<>();
    public ArrayList<String> packageNameList = new ArrayList<>();
    public ArrayList<String> whiteList = new ArrayList<>();
    public boolean serviceON = false;
    public boolean picActivityShowing = false;
    public String TARGET_TIME = "";
}
