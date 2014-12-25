package com.hmammon.familyphoto.ui;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hmammon.familyphoto.R;
import com.hmammon.familyphoto.utils.WifiComparator;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WifiFragment extends Fragment implements View.OnClickListener
                                            , AdapterView.OnItemClickListener{

    private View view;
    private Button btnConn, btnExit;
    private View llDetail;
    private List<ScanResult> wifiScanList;
    private Activity activity;
    private WifiReceiver wifiReceiver;
    private final int SECURITY_NONE = 0, SECURITY_WEP = 1, SECURITY_PSK = 2, SECURITY_EAP = 3;
    private WifiManager wifiMana;
    private WifiAdapter adapter;
    private ListView lv;
    private TextView tvWifi;
    private EditText etPass;

    public WifiFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        wifiReceiver = new WifiReceiver();

        wifiMana = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        wifiMana.setWifiEnabled(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wifi, container, false);
        view.setOnClickListener(this);
        btnConn = (Button)view.findViewById(R.id.btn_connect);
        btnConn.setOnClickListener(this);
        btnExit = (Button)view.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(this);

        llDetail = view.findViewById(R.id.ll_detail);
        lv = (ListView) view.findViewById(R.id.lv);
        lv.setOnItemClickListener(this);
        tvWifi = (TextView) view.findViewById(R.id.tv_wifi);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        activity.registerReceiver(wifiReceiver, intentFilter);
        wifiMana.startScan();
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.unregisterReceiver(wifiReceiver);
    }

    @Override
    public void onClick(View view) {
        if (view == btnConn){
            llDetail.setVisibility(View.VISIBLE);
            btnConn.setVisibility(View.GONE);
        }

        else if (view == btnExit){
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ScanResult sr = wifiScanList.get(i);

        tvWifi.setText("连接到:" + sr.SSID);
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
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvName.setText(wifiScanList.get(i).SSID);
            return view;
        }
    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                list();
            }
            else {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
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
}
