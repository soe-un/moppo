package com.example.moppo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moppo.login.LoginActivity;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class SubActivity extends AppCompatActivity {

    private String strNick, strID, stridx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        strID = SaveSharedPreference.getPrefUserId(SubActivity.this);
        stridx = String.valueOf(SaveSharedPreference.getPrefUserIdx(SubActivity.this));
        strNick = SaveSharedPreference.getPrefUserName(SubActivity.this);

        TextView tv_nick=findViewById(R.id.tv_nickname);

        //닉네임 set
        tv_nick.setText(strNick);

        findViewById(R.id.alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //알람 화면으로 이동.
                Intent intent3 = new Intent(SubActivity.this, AlarmActivity.class);
                intent3.putExtra("stridx", stridx);
                startActivity(intent3);
            }
        });



        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout()
                    {
                        //로그아웃 성공시 수행하는 지점
                        //Login 화면으로 이동.
                        SaveSharedPreference.clearPrefUserData(SubActivity.this);
                        Intent intent2 = new Intent(SubActivity.this, MainActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                    }
                });
            }
        });

    }
}