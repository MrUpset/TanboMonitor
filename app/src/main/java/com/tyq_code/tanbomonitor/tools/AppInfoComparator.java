package com.tyq_code.tanbomonitor.tools;

import java.util.Comparator;

public class AppInfoComparator implements Comparator<AppInfo> {

    @Override
    public int compare(AppInfo leftAppInfo, AppInfo rightAppInfo) {
        float leftTime, rightTime;
        leftTime = leftAppInfo.time;
        rightTime = rightAppInfo.time;
        if (leftTime < rightTime)
            return 1;
        else if (leftTime > rightTime)
            return -1;
        else
            return 0;
    }

}
