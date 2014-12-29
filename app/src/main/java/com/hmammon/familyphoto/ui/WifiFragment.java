package com.hmammon.familyphoto.ui;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hmammon.familyphoto.R;
import com.hmammon.familyphoto.utils.BaseFragment;
import com.hmammon.familyphoto.utils.WifiComparator;
import com.hmammon.familyphoto.utils.WifiHelper;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WifiFragment extends BaseFragment implements View.OnClickListener
                                            , AdapterView.OnItemClickListener{

    private View view;
    private Button btnConnect, btnExit, btnConn;
    private View llDetail;
    private List<ScanResult> wifiScanList;
    private MainActivity activity;
    private WifiReceiver wifiReceiver;
    private final int SECURITY_NONE = 0, SECURITY_WEP = 1, SECURITY_PSK = 2, SECURITY_EAP = 3;
    private WifiManager wifiMana;
    private WifiAdapter adapter;
    private ListView lv;
    private TextView tvWifi;
    private EditText etPass;
    private ScanResult choose;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        fragName = "WifiSetting";

        wifiReceiver = new WifiReceiver();

        wifiMana = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        if (!wifiMana.isWifiEnabled())
            wifiMana.setWifiEnabled(true);

        dialog = new ProgressDialog(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wifi, container, false);
        view.setOnClickListener(this);
        btnConnect = (Button)view.findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(this);
        btnConn = (Button)view.findViewById(R.id.btn_conn);
        btnConn.setOnClickListener(this);
        btnExit = (Button)view.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(this);
        etPass = (EditText)view.findViewById(R.id.et);

        llDetail = view.findViewById(R.id.ll_detail);
        lv = (ListView) view.findViewById(R.id.lv);
        lv.setOnItemClickListener(this);
        tvWifi = (TextView) view.findViewById(R.id.tv_wifi);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.unregisterReceiver(wifiReceiver);
    }

    @Override
    public void onClick(View view) {
        if (view == btnConnect){
            llDetail.setVisibility(View.VISIBLE);
            btnConnect.setVisibility(View.GONE);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            activity.registerReceiver(wifiReceiver, intentFilter);
            wifiMana.startScan();
        }

        else if (view == btnExit){
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        }

        else if (view == btnConn){
            String pass = etPass.getText().toString();
            if (pass.length() == 0) {
                showToast("请输入密码");
                return;
            }
            connect(choose, pass);
            dialog.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        choose = wifiScanList.get(i);
        tvWifi.setText("连接到:" + choose.SSID);

        if (getSecurity(choose) == SECURITY_NONE){
            dialog.setMessage("连接中...");
            dialog.show();
            connect(choose, "");
        }
    }

    private void list(){
        wifiScanList = wifiMana.getScanResults();

        Collections.sort(wifiScanList, new WifiComparator());

        if (adapter == null) {
            adapter = new WifiAdapter();
            lv.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Wifi列表的适配器
     */
    class WifiAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (wifiScanList == null)
                return 0;
            return wifiScanList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null)
                view = activity.getLayoutInflater().inflate(R.layout.item_wifi, null);

            TextView tvName;
            ImageView ivPass;

            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvName.setText(wifiScanList.get(i).SSID);
            ivPass = (ImageView) view.findViewById(R.id.iv_pass);

            boolean lock = getSecurity(wifiScanList.get(i)) == SECURITY_NONE;

            if (!lock) ivPass.setVisibility(View.VISIBLE);
            else ivPass.setVisibility(View.INVISIBLE);

            return view;
        }
    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.w("wifi", intent  + ".");

            //Wifi列表可用消息
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                list();
            }
            //网络状态改变消息
            else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null){
                    dialog.setMessage(WifiHelper.getInfo(info.getDetailedState()));
                    Log.i("wifi", info.getDetailedState()+"");
                    if (info.isConnected()) {
                        showToast(info.getDetailedState());
                        dialog.dismiss();
                        startSMS();
                    }
                }
            }

            //Wifi状态改变消息
            else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_DISABLED);
                Log.i("wifi", wifistate+"");
                if(wifistate == WifiManager.WIFI_STATE_ENABLED) showToast("Wifi已开启");
            }
        }
    }

    /**
     * 获得网络的安全类型
     * @param result
     * @return
     */
    int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

    /**
     * 连接到相应的网络上
     * @param result
     * @param pass
     */
    private void connect(ScanResult result, String pass){
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + result.SSID + "\"";

        int type = getSecurity(result);

        if (type == SECURITY_WEP){
            conf.wepKeys[0] = "\"" + pass + "\"";
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        }

        if (type == SECURITY_PSK){
            conf.preSharedKey = "\""+ pass +"\"";
        }

        if (type == SECURITY_NONE){
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        wifiMana.addNetwork(conf);

        List<WifiConfiguration> list = wifiMana.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + result.SSID + "\"")) {
                wifiMana.disconnect();
                wifiMana.enableNetwork(i.networkId, true);
                wifiMana.reconnect();

                break;
            }
        }
    }

    private void startSMS(){
        activity.fragSMS = new SMSFragment();
        FragmentTransaction trans = activity.getFragmentManager().beginTransaction();
        trans.remove(activity.fragWifi);
        trans.add(R.id.container, activity.fragSMS).commit();
    }

}
