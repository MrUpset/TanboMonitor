package com.tyq_code.tanbomonitor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tyq_code.tanbomonitor.tools.AppInfoUtil;
import com.tyq_code.tanbomonitor.tools.WhiteListTable;
import com.tyq_code.tanbomonitor.view.CheckView;

import java.util.ArrayList;
import java.util.List;

public class WhiteListActivity extends Activity {

    private ArrayList<String> normalPkgNameList;
    private ArrayList<String> whiteList;
    private List<PackageInfo> packages;
    private PackageAdapter packageAdapter;
    private ListView listView;
    private LoadTask loadTask;
    private TanboApplication application;
    private WhiteListTable whiteListTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soft_monitor);

        listView = (ListView) findViewById(R.id.packageListView);
        application = (TanboApplication) getApplication();
        packages = getPackageManager().getInstalledPackages(0);

        packageAdapter = new PackageAdapter(this);
        whiteListTable = new WhiteListTable(this);

        normalPkgNameList = new ArrayList<>();
        whiteList = application.whiteList;

        loadTask = new LoadTask();
        loadTask.execute();
    }

    /**
     * 1.读取database里的white list
     * 2.读取packages
     * 3.将packages里不是白名单中的应用放入normal list
     * 4.首先展示white list，然后展示normal list
     */

    private class LoadTask extends AsyncTask<Void, Integer, String> {

        private ProgressDialog progressDialog;

        @Override
        protected String doInBackground(Void... params) {

            Cursor cursor = whiteListTable.getPackageInfo();
            while (cursor.moveToNext()) {
                if (!whiteList.contains(cursor.getString(1)))
                    whiteList.add(cursor.getString(1));
            }

            PackageInfo packageInfo;
            for (int i = 0; i < packages.size(); i++) {
                packageInfo = packages.get(i);
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
                    continue;
                if (packageInfo.packageName.equals(Constant.PACKAGE_NAME)) continue;
                if (!whiteList.contains(packageInfo.packageName))
                    normalPkgNameList.add(packageInfo.packageName);
            }
            return "finished";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(WhiteListActivity.this, "白名单", "Loading...", true);
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
            return whiteList.size() + normalPkgNameList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater().from(context);
                convertView = inflater.inflate(R.layout.app_info_listview, null);
            }
            ImageView appIconIV = (ImageView) convertView.findViewById(R.id.appInfo_listView_appIcon);
            TextView appNameTV = (TextView) convertView.findViewById(R.id.appInfo_listView_firstTV);
            TextView appTimeTV = (TextView) convertView.findViewById(R.id.appInfo_listView_secondTV);
            final CheckView checkView = (CheckView) convertView.findViewById(R.id.appInfo_listView_checkView);

            AppInfoUtil appInfoUtil = new AppInfoUtil(context);
            final ApplicationInfo appInfo;

            if (position < whiteList.size()) {
                appInfo = appInfoUtil.getAppInfo(whiteList.get(position));
                checkView.isYes = true;
                checkView.invalidate();
            } else {
                appInfo = appInfoUtil.getAppInfo(normalPkgNameList.get(position - whiteList.size()));
                checkView.isYes = false;
                checkView.invalidate();
            }

            appIconIV.setBackground(appInfo.loadIcon(getPackageManager()));
            appNameTV.setText(appInfo.loadLabel(getPackageManager()).toString());
            appTimeTV.setText(appInfo.packageName);
            checkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkView.isYes) {
                        whiteList.add(appInfo.packageName);
                        normalPkgNameList.remove(appInfo.packageName);
                        whiteListTable.insert(appInfo.packageName);
                    } else {
                        normalPkgNameList.add(appInfo.packageName);
                        whiteList.remove(appInfo.packageName);
                        whiteListTable.delete(appInfo.packageName);
                    }
                    checkView.isYes = !checkView.isYes;
                    checkView.isClicked = true;
                    checkView.invalidate();
                }
            });
            return convertView;
        }
    }
}
