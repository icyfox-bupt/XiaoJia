package com.hmammon.familyphoto.utils;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by icyfox on 2014/12/18.
 */
public class BaseActivity extends Activity {

    protected void showToast(Object toast){
        Toast.makeText(this, toast + "", Toast.LENGTH_SHORT)
                .show();
    }

    protected void quickStart(Class<?> clazz){
        startActivity(new Intent(this, clazz));
    }
}
