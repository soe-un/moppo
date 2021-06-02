package com.example.moppo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.moppo.calendar.FragmentCalendar;
import com.example.moppo.login.LoginActivity;
import com.example.moppo.ranking.FragmentRanking;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNV;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentCalendar fragmentCalendar = new FragmentCalendar();
    private FragmentRanking fragmentRanking = new FragmentRanking();
    private FragmentStatistic fragmentStatistic = new FragmentStatistic();

    DbHelper helper;
    SQLiteDatabase db;

    public Context context_main;
    String userID;
    int idx;
    String userNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (SaveSharedPreference.getPrefUserName(MainActivity.this).length() == 0
                || SaveSharedPreference.getPrefUserId(MainActivity.this).length() == 0
                || SaveSharedPreference.getPrefUserIdx(MainActivity.this) == -10) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context_main = MainActivity.this;

        helper = new DbHelper(this);

        try{ //get database
            db = helper.getWritableDatabase();
        }catch (SQLException ex){
            db = helper.getReadableDatabase();
        }

        userID = SaveSharedPreference.getPrefUserId(context_main);
        idx = SaveSharedPreference.getPrefUserIdx(context_main);
        userNick = SaveSharedPreference.getPrefUserName(context_main);

        getPlansfromServer(); //Server DB -> Local DB

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container,fragmentCalendar).commitAllowingStateLoss();

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
        bundle.putString("nickname", userNick);

        fragmentRanking.setArguments(bundle);
        fragmentCalendar.setArguments(bundle);
        fragmentStatistic.setArguments(bundle);
    }

    private void BottomNavigate(int id){
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch(id){
            case R.id.calendar:
                transaction.replace(R.id.fragment_container, fragmentCalendar).commitAllowingStateLoss();
                break;
            case R.id.statistic:
                transaction.replace(R.id.fragment_container, fragmentStatistic).commit();
                break;
            case R.id.ranking:
                transaction.replace(R.id.fragment_container, fragmentRanking).commitAllowingStateLoss();
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
                Intent intent = new Intent(this, SubActivity.class);
                startActivity(intent);
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
        Log.d("life", "server database read");
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

                        TablePlans tablePlans = new TablePlans(server_idx, plan_name, plan_order, income, is_complete, timestamp);
                        helper.putLocalDB(db, tablePlans, 0);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        TableUsers tableUsers = new TableUsers(responseListener, String.valueOf(idx));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(tableUsers);

    }
}