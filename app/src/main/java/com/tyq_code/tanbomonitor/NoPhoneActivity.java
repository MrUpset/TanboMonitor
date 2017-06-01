package com.tyq_code.tanbomonitor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NoPhoneActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_phone);

        Button button = (Button) findViewById(R.id.back_to_home);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TanboApplication)getApplication()).picActivityShowing = false;
                GoToHome();
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((TanboApplication)getApplication()).picActivityShowing = false;
        GoToHome();
        finish();
    }

    /**
     * 相当于是按下Home键
     */
    public void GoToHome(){
        Intent intent= new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
