package com.hmammon.familyphoto.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hmammon.familyphoto.R;
import com.hmammon.familyphoto.http.HttpHelper;
import com.hmammon.familyphoto.utils.BaseApp;
import com.hmammon.familyphoto.utils.BaseFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by icyfox on 2014/12/29.
 */
public class SMSFragment extends BaseFragment implements View.OnClickListener{

    private View view;
    private Button btnSend, btnSkip;
    private EditText etPhone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragName = "SMS";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sms, null);
        btnSend = (Button) view.findViewById(R.id.btn_send);
        btnSkip = (Button) view.findViewById(R.id.btn_skip);
        etPhone = (EditText) view.findViewById(R.id.et_phone);

        btnSend.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == btnSend){
            sendSMS();
        }

        if (view == btnSkip){

        }
    }

    private void sendSMS() {
       String phone = etPhone.getText().toString();
       if (!isMobileNO(phone)) {
            showToast("请输入正确的手机号码!");
            etPhone.setText("");
       }else{
           RequestParams params = new RequestParams();
           params.add("via", "phone");
           params.add("account", phone);
           params.add("deviceId", BaseApp.getDeviceId());
           HttpHelper.post(HttpHelper.INVITE, params, handler);
       }

    }

    public boolean isMobileNO(String mobiles) {
        if (mobiles.length() != 11) return false;
        if (!TextUtils.isDigitsOnly(mobiles)) return false;

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(1[7-8][0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    private JsonHttpResponseHandler handler = new JsonHttpResponseHandler(){

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            showToast("发送成功!");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            showToast("网络错误" + statusCode);
        }
    };
}
