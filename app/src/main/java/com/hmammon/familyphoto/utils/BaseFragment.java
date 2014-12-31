package com.hmammon.familyphoto.utils;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by icyfox on 2014/12/29.
 */
public class BaseFragment extends Fragment {

    protected String fragName;

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(fragName);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(fragName);
    }

    protected void showToast(Object msg){
        Toast.makeText(getActivity(), msg + "", Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * 显示提示对话框
     * @param title
     * @param msg
     * @param listener
     */
    protected void showDialog(String title, String msg, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确定", listener)
                .show();
    }
}
