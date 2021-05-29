package com.example.moppo;

import androidx.appcompat.app.AppCompatActivity;

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

        Intent intent=getIntent();
        strID=intent.getStringExtra("userID");
        stridx = String.valueOf(intent.getIntExtra("idx", 0));
        strNick=intent.getStringExtra("nickname");

        TextView tv_nick=findViewById(R.id.tv_ninkname);
        TextView rv_email=findViewById(R.id.tv_email);
        ImageView iv_profile = findViewById(R.id.iv_profile);

        //닉네임 set
        tv_nick.setText(strNick);
        //이메일 set
        //rv_email.setText(strEmail);

        //Glide.with(this).load(strProfileImg).into(iv_profile);
        findViewById(R.id.alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(SubActivity.this, AlarmActivity.class);
                startActivity(intent3);
                //알람 화면으로 이동.
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
                        Intent intent2 = new Intent(SubActivity.this, LoginActivity.class);
                        startActivity(intent2);
                        //Login 화면으로 이동.
                    }
                });
            }
        });

    }
}