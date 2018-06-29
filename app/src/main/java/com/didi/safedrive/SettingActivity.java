package com.didi.safedrive;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.didi.safedrive.util.SettingSharedPrefHelper;
import com.didi.safedrive.util.SmsUtils;

public class SettingActivity extends AppCompatActivity {

    private EditText mManPhone;
    private EditText mCity;
    private EditText mManNane;
    private ImageView mSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mManPhone = findViewById(R.id.et_phone);
        mCity = findViewById(R.id.et_city);
        mManNane = findViewById(R.id.et_name);
        mSet = findViewById(R.id.btn_set);
        mSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mManPhone.getText().toString();
                String name = mManNane.getText().toString();
                String city = mCity.getText().toString();
                SettingSharedPrefHelper helper = new SettingSharedPrefHelper();
                if (!TextUtils.isEmpty(phone)) {
                    helper.putString("phone",phone);
                    if (TextUtils.isEmpty(name)){
                        name = "";
                    }
                    SmsUtils.SendMsg("[CarCar]亲爱的"+name+"，我已将你设置为我的紧急联系人。",phone);
                }
                if (!TextUtils.isEmpty(name)) {
                    helper.putString("name",name);
                }
                if (!TextUtils.isEmpty(city)) {
                    helper.putString("city",city);
                }
                finish();
            }
        });
    }
}
