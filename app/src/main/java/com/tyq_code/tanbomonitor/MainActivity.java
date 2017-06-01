package com.tyq_code.tanbomonitor;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;
import com.tyq_code.tanbomonitor.tools.UsageHistory;

public class MainActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);

        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                drawerArrow, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        final String[] app_names = new String[]{
                "",
                "记事本",
                "日历",
                "超级课程表",
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.brief_textview, R.id.brief_text, app_names);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String pkg_name = null;
                        switch (position) {
                            case 0:break;
                            case 1:
                                pkg_name = "com.example.android.notepad";break;
                            case 2:
                                pkg_name = "com.android.calendar";break;
                            case 3:
                                pkg_name = "com.xtuone.android.syllabus";break;
                        }
                        if (pkg_name != null){
                            Intent intent = getPackageManager().getLaunchIntentForPackage(pkg_name);
                            if (intent != null)
                                startActivity(intent);
                            else
                                Toast.makeText(MainActivity.this, "找不到"+app_names[position],Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        Button soft = (Button) findViewById(R.id.soft);
        soft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsageHistory history = new UsageHistory(MainActivity.this);
                if (history.query()){
                    Intent intent = new Intent(MainActivity.this, SoftMonitorActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button hard = (Button) findViewById(R.id.hard);
        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsageHistory history = new UsageHistory(MainActivity.this);
                if (history.query()){
                    Intent intent = new Intent(MainActivity.this, HardMonitorActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
