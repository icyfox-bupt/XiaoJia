package com.hmammon.familyphoto;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView)findViewById(R.id.tv);
        tv.setText(Build.VERSION.SDK_INT + " \n" + getWindowManager().getDefaultDisplay().getWidth() + " \n" +
         getWindowManager().getDefaultDisplay().getHeight()
        );

        new GetNewPhoto().start();
    }

}
