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
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNV;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentCalendar fragmentCalendar = new FragmentCalendar();
    private FragmentRanking fragmentRanking = new FragmentRanking();
    private FragmentStatistic fragmentStatistic = new FragmentStatistic();

    DbHelper helper;
    SQLiteDatabase db;

    static String userID;
    static int idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new DbHelper(this);

        try{ //get database
            db = helper.getWritableDatabase();
        }catch (SQLException ex){
            db = helper.getReadableDatabase();
        }

        userID = getIntent().getStringExtra("userID");
        idx = getIntent().getIntExtra("idx", 0);

        getPlansfromServer(); //Server DB -> Local DB

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

        Bundle bundle = new Bundle();
        bundle.putString("userID", userID);
        bundle.putInt("idx", idx);

        fragmentCalendar.setArguments(bundle);
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


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("life", "onResume Main");
        getPlansfromServer(); //local db 갱신
    }

    private void getPlansfromServer() { //DB 읽어오기

        Response.Listener<String> responseListener;
        responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    helper.cleanLocalDB(db);

                    for(int i = 0 ; i<jsonArray.length() ; i++){
                        JSONObject tmpjsonobj = (JSONObject) jsonArray.get(i);

                        int server_idx = tmpjsonobj.getInt("server_idx");
                        String plan_name = tmpjsonobj.getString("plan_name");
                        int plan_order = tmpjsonobj.getInt("plan_order");
                        int income = tmpjsonobj.getInt("income");
                        int is_complete = tmpjsonobj.getInt("is_complete");
                        String timestamp = tmpjsonobj.getString("timestamp");

                        PlansTable plansTable = new PlansTable(server_idx, plan_name, plan_order, income, is_complete, timestamp);
                        helper.putLocalDB(db, plansTable, 0);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        UsersTable usersTable = new UsersTable(responseListener, String.valueOf(idx));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(usersTable);

    }
}