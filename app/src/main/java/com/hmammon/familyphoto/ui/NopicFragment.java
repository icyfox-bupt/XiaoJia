package com.hmammon.familyphoto.ui;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hmammon.familyphoto.R;
        import com.hmammon.familyphoto.utils.BaseFragment;

/**
 * Created by icyfox on 2014/12/29.
 */
public class NopicFragment extends BaseFragment implements View.OnClickListener{

    private View base;
    private View btnWifi, btnExit;
    private MainActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragName = "nopic";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nopic, null);

        activity = (MainActivity) getActivity();

        btnWifi = view.findViewById(R.id.btn_wifi);
        btnExit = view.findViewById(R.id.btn_exit);
        base = view.findViewById(R.id.base);
        btnWifi.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        base.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == base);
        if (view == btnWifi){
            FragmentTransaction trans = getActivity().getFragmentManager().beginTransaction();
            trans.add(R.id.container, activity.fragWifi).commit();
        }
        if (view == btnExit){
            FragmentTransaction trans = getActivity().getFragmentManager().beginTransaction();
            trans.remove(activity.fragNopic).commit();
        }
    }
}
