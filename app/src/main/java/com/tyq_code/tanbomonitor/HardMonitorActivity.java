package com.tyq_code.tanbomonitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class HardMonitorActivity extends Activity {

    private final Context context = this;
    private boolean serviceON;
    private MsgReceiver msgReceiver;
    private Button monitorBtn;
    private TimePicker timePicker;
    private TextView timeLeftTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_monitor);
        init();
    }

    private void init() {
        final TimePicker killerTimePicker = (TimePicker) findViewById(R.id.hard_time_picker);
        monitorBtn = (Button) findViewById(R.id.killer_monitor_button);
        timePicker = (TimePicker) findViewById(R.id.hard_time_picker);
        timeLeftTV = (TextView) findViewById(R.id.hard_time_left);

        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.STRONG_MONITOR_SERVICE);
        registerReceiver(msgReceiver, intentFilter);

        serviceON = ((TanboApplication)getApplication()).serviceON;

        if (!serviceON) monitorBtn.setText(Constant.START_MONITOR);
        else {
            monitorBtn.setText(Constant.STOP_MONITOR);
            timePicker.setVisibility(View.GONE);
            timeLeftTV.setText("监控结束时间："+((TanboApplication)getApplication()).TARGET_TIME);
            timeLeftTV.setVisibility(View.VISIBLE);
        }

        monitorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!serviceON) {
                    int hour = killerTimePicker.getCurrentHour();
                    int minute = killerTimePicker.getCurrentMinute();
                    Calendar c = Calendar.getInstance();
                    int timeLeft = (hour - c.get(Calendar.HOUR_OF_DAY)) * 60 + minute - c.get(Calendar.MINUTE);
//                    Log.i("TimePicker", hour + ":" + minute + " timeLeft:" + timeLeft);
                    if (timeLeft <= 0) {
//                        Log.i("timeLeft", "<=0");
                        Toast.makeText(context, "请设置正确的时间", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    serviceON = true;
                    Intent intent = new Intent(HardMonitorActivity.this, HardMonitorService.class);
                    intent.putExtra("monitor_time", timeLeft);
                    startService(intent);
                    monitorBtn.setText(Constant.STOP_MONITOR);
                    timePicker.setVisibility(View.GONE);
                    ((TanboApplication)getApplication()).TARGET_TIME = hour + ":" + (minute>9 ? minute : "0"+minute);
                    timeLeftTV.setText("监控结束时间：" + hour + ":" + (minute>9 ? minute : "0"+minute));
                    timeLeftTV.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "在接下来的" + timeLeft + "分钟内请不要玩手机，\n仅可以打开系统应用及白名单应用", Toast.LENGTH_LONG).show();
                } else {
                    serviceON = false;
                    monitorBtn.setText(Constant.START_MONITOR);
                    timePicker.setVisibility(View.VISIBLE);
                    timeLeftTV.setVisibility(View.GONE);
                    stopService(new Intent(HardMonitorActivity.this, HardMonitorService.class));
                }
            }
        });

        Button whiteListBtn = (Button) findViewById(R.id.killer_white_list_button);
        whiteListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HardMonitorActivity.this, WhiteListActivity.class);
                startActivity(intent);
            }
        });
    }

    public class MsgReceiver extends BroadcastReceiver{
        public final static int ID_TIME_UP = 1;

        @Override
        public void onReceive(Context context, Intent intent) {
            int ID = intent.getIntExtra("ID", 0);
            if (ID == ID_TIME_UP) {
                monitorBtn.setText(Constant.START_MONITOR);
                serviceON = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.i("HardMonitorActivity", "onDestroy");
//        Log.i("HardMonitorActivity", "serviceON:" + serviceON);
        unregisterReceiver(msgReceiver);
    }
}