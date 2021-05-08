package com.example.moppo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNV;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentCalendar fragmentCalendar = new FragmentCalendar();
    private FragmentRanking fragmentRanking = new FragmentRanking();
    private FragmentStatistic fragmentStatistic = new FragmentStatistic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout,fragmentCalendar).commitAllowingStateLoss();

        mBottomNV = findViewById(R.id.bottomNavBar);
        mBottomNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                BottomNavigate(item.getItemId());
                return true;
            }
        });
        mBottomNV.setSelectedItemId(R.id.calendar);// 캘린더 프래그먼트 선택한 채로 시작
    }

    private void BottomNavigate(int id){
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch(id){
            case R.id.calendar:
                transaction.replace(R.id.frameLayout, fragmentCalendar).commitAllowingStateLoss();
                break;
            case R.id.statistic:
                transaction.replace(R.id.frameLayout, fragmentStatistic).commitAllowingStateLoss();
                break;
            case R.id.ranking:
                transaction.replace(R.id.frameLayout, fragmentRanking).commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.option:
                //옵션 누르면 할 것
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}