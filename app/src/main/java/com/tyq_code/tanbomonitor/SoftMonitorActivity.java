package com.tyq_code.tanbomonitor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tyq_code.tanbomonitor.tools.AppInfo;
import com.tyq_code.tanbomonitor.tools.AppInfoComparator;
import com.tyq_code.tanbomonitor.view.CheckView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class SoftMonitorActivity extends Activity {

    private ArrayList<AppInfo> appList;
    private ArrayList<String> packageNameList;
    private List<PackageInfo> packages;
    private PackageAdapter packageAdapter;
    private ListView listView;
    private LoadTask loadTask;
    private TanboApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soft_monitor);

        packages = getPackageManager().getInstalledPackages(0);
        packageAdapter = new PackageAdapter(this);
        listView = (ListView) findViewById(R.id.packageListView);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            startService(new Intent(this, SoftMonitorService.class));
            application = (TanboApplication) getApplication();
            appList = application.appList;
            packageNameList = application.packageNameList;
        } else {
            appList = new ArrayList<>();
            packageNameList = new ArrayList<>();
            getSystemUsageHistory();
        }
        loadTask = new LoadTask();
        loadTask.execute();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void getSystemUsageHistory() {
        if (hasModule()) {
            List<UsageStats> queryUsageStats = getUsageStats();
            if (queryUsageStats == null) {
                Log.e("getSystemUsageHistory", "没有打开安全权限");
                Toast.makeText(SoftMonitorActivity.this, "请打开TanboMonitor的安全权限", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //非Activity不加flag会报错
                startActivity(intent);
                finish();
            } else {
                AppInfo appInfo;
                for (UsageStats usageStats : queryUsageStats) {
                    if (usageStats.getTotalTimeInForeground() < 60000) {
//                        Log.i("app", usageStats.getPackageName()+" : "+ usageStats.getTotalTimeInForeground());
                        continue;
                    }//过滤运行时间不超过1分钟的应用
                    appInfo = new AppInfo(null, usageStats.getPackageName(), usageStats.getTotalTimeInForeground() / 1000, 0, null);
                    packageNameList.add(usageStats.getPackageName());
                    appList.add(appInfo);
                }
            }
        } else {
            Log.e("test", "小于5.0，没这个功能");
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean hasModule() {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private List<UsageStats> getUsageStats() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        long end_time = calendar.getTimeInMillis();
//        calendar.add(Calendar.DAY_OF_WEEK, -1);
        long start_time = end_time - 200000;//calendar.getTimeInMillis();

        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Constant.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start_time, end_time);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return null;
        }
        return queryUsageStats;
    }

    private class LoadTask extends AsyncTask<Void, Integer, String> {

        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(Void... params) {
            PackageInfo packageInfo;
            AppInfo appInfo;
            int index;
            for (int i = 0; i < packages.size(); i++) {
                packageInfo = packages.get(i);
                if (packageNameList.contains(packageInfo.packageName)) {
                    index = packageNameList.indexOf(packageInfo.packageName);
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { //非系统应用
                        appInfo = appList.get(index); //因为是引用，所以设置appInfo就设置了appList
                        appInfo.appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                        appInfo.icon = packageInfo.applicationInfo.loadIcon(getPackageManager());
                    } else { //系统应用
                        appList.remove(index);
                        packageNameList.remove(index);
                    }
                }
            }
            AppInfoComparator comparator = new AppInfoComparator();
            Collections.sort(appList, comparator);
            return "finished";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SoftMonitorActivity.this, "TanboMonitor", "Loading...", true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listView.setAdapter(packageAdapter);
            progressDialog.dismiss();
        }
    }

    private class PackageAdapter extends BaseAdapter {

        private Context context;

        public PackageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return appList.size()+1;
        }

        @Override
        public Object getItem(int position) {
//            if (position == 0) return null;
//            return appList.get(position-1);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0){
                if (convertView == null) {
                    LayoutInflater inflater = getLayoutInflater().from(context);
                    convertView = inflater.inflate(R.layout.app_piechart, null);
                    PieChartView pieChartView = (PieChartView) convertView.findViewById(R.id.soft_pie_chart);

                    List<SliceValue> values = new ArrayList<>();
                    for (int i = 0; i < appList.size(); ++i) {
                        SliceValue sliceValue = new SliceValue(appList.get(i).time/60, ChartUtils.nextColor());
                        values.add(sliceValue);
                    }
                    PieChartData pieChartData = new PieChartData(values);
                    pieChartData.setHasLabels(true);
                    pieChartData.setHasLabelsOnlyForSelected(false);
                    pieChartData.setHasLabelsOutside(false);
                    pieChartView.setPieChartData(pieChartData);
                }
            } else {
                if (convertView == null) {
                    LayoutInflater inflater = getLayoutInflater().from(context);
                    convertView = inflater.inflate(R.layout.app_info_listview, null);
                }
                ImageView appIconIV = (ImageView) convertView.findViewById(R.id.appInfo_listView_appIcon);
                TextView appNameTV = (TextView) convertView.findViewById(R.id.appInfo_listView_firstTV);
                TextView appTimeTV = (TextView) convertView.findViewById(R.id.appInfo_listView_secondTV);
                CheckView checkView = (CheckView) convertView.findViewById(R.id.appInfo_listView_checkView);

                AppInfo appInfo = appList.get(position-1);
                appIconIV.setBackground(appInfo.icon);
                appNameTV.setText(appInfo.appName);// + " " + appInfo.packageName);
                appTimeTV.setText("时长：" + (int)appInfo.time / 60+ " min" + (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP?(" 次数：" + appInfo.frequency):""));
                checkView.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        notificationManager.notify(Constant.NOTIFICATION_FLAG, notification);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        packageAdapter.notifyDataSetChanged();
//        notificationManager.cancel(Constant.NOTIFICATION_FLAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Intent intent = new Intent(this, BackgroundListeningService.class);
//        stopService(intent);
    }
}