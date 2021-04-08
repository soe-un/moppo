package com.example.moppo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class SubActivity extends AppCompatActivity {

    private String strNick, strProfileImg, strEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Intent intent=getIntent();
        strNick=intent.getStringExtra("name");
        strEmail=intent.getStringExtra("email");
        strProfileImg=intent.getStringExtra("profileImg");

        TextView tv_nick=findViewById(R.id.tv_ninkname);
        TextView rv_email=findViewById(R.id.tv_email);
        ImageView iv_profile = findViewById(R.id.iv_profile);

        //닉네임 set
        tv_nick.setText(strNick);
        //이메일 set
        rv_email.setText(strEmail);

        Glide.with(this).load(strProfileImg).into(iv_profile);
    }
}