package com.hmammon.familyphoto.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
    private View base;
    InputMethodManager imm;

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
        base = view.findViewById(R.id.base);

        btnSend.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        base.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        hideKeyBoard();
        if (view == base){
        }

        if (view == btnSend){
            sendSMS();
        }

        if (view == btnSkip){
           getActivity().getFragmentManager().beginTransaction()
                   .remove(this).commit();
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

    void hideKeyBoard(){
        imm.hideSoftInputFromInputMethod(getView().getWindowToken(), 0);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

}
