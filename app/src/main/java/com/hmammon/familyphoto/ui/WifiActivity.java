package com.hmammon.familyphoto.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hmammon.familyphoto.R;
import com.hmammon.familyphoto.utils.BaseActivity;

import java.util.List;

public class WifiActivity extends BaseActivity {

    private WifiManager wifiMana;
    private WifiReceiver wifiReceiver;
    private ListView lv;
    private WifiAdapter adapter;
    private List<ScanResult> wifiScanList;
    private TextView tvName;
    private final int SECURITY_NONE = 0, SECURITY_WEP = 1, SECURITY_PSK = 2, SECURITY_EAP = 3;
    private ScanResult sr;
    private Button btn;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        wifiMana = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiMana.setWifiEnabled(true);

        wifiReceiver = new WifiReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiReceiver, intentFilter);

        lv = (ListView)findViewById(R.id.lv);
        lv.setOnItemClickListener(itemClick);
        tvName = (TextView) findViewById(R.id.tv_name);
        btn = (Button)findViewById(R.id.btn);
        et = (EditText)findViewById(R.id.editText);
        btn.setOnClickListener(click);

        wifiMana.startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

    private void list(){
        wifiScanList = wifiMana.getScanResults();
        adapter = new WifiAdapter();
        lv.setAdapter(adapter);
    }

    class WifiReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                list();
                showToast("wifi出结果");
            }
            else {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                showToast("aaaaaaaaa" + info);
                if (info != null && info.isConnected()){
                    showToast("wifi连接成功");
                    finish();
                }
            }
        }
    }

    class WifiAdapter extends BaseAdapter{

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
                view = getLayoutInflater().inflate(R.layout.item_wifi, null);

            TextView tvName, tvLevel;
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvLevel = (TextView) view.findViewById(R.id.tv_mac);

            tvName.setText(wifiScanList.get(i).SSID);
            tvLevel.setText(wifiScanList.get(i).level);

            return view;
        }
    }

    private AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            tvName.setText("连接Wifi：" + wifiScanList.get(i).SSID );
            sr = wifiScanList.get(i);
        }
    };

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (sr == null) return;
            String pass = et.getText().toString();
            connect(sr, pass);
        }
    };

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
