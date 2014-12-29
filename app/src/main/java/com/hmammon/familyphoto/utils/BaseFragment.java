package com.hmammon.familyphoto.utils;

import android.app.Fragment;
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
}
