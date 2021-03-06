package com.example.moppo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SupportActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentStatistic fragmentStatistic;

    DbHelper helper;
    SQLiteDatabase db;

    int useridx;
    String userNick;
    String userID;
    int userNo;
    int inMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        helper = new DbHelper(this);

        try{ //get database
            db = helper.getWritableDatabase();
        }catch (SQLException ex){
            db = helper.getReadableDatabase();
        }

        useridx = getIntent().getIntExtra("idx", 0);//액티비티로부터 알고싶은 useridx를 알아냄
        userID = getIntent().getStringExtra("userID");
        userNick = getIntent().getStringExtra("nickname");
        userNo = getIntent().getIntExtra("userNo", 0);
        inMoney = getIntent().getIntExtra("inMoney", 0);

        Bundle bundle = new Bundle();
        bundle.putString("userID", userID);
        bundle.putInt("idx", useridx);
        bundle.putString("nickname", userNick);
        bundle.putInt("inMoney", inMoney);

        //getPlansfromServer();

        //통계 프래그먼트 화면
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragmentStatistic = new FragmentStatistic();
        transaction.add(R.id.frameLayout2, fragmentStatistic).commit();

        fragmentStatistic.setArguments(bundle);

        //후원 금액 text
        EditText support_money=(EditText)findViewById(R.id.support_money);

        Button supbtn = findViewById(R.id.support_btn);

        supbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String typeMoney = support_money.getText().toString();

                Response.Listener<String> responseListener;
                responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            System.out.println(success);
                            String message = jsonObject.getString("message");

                            if (success) {
                                Toast.makeText(SupportActivity.this, "후원 성공!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SupportActivity.this, "후원 실패(금액 초과)" + message, Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };
                TableUsers tableUsers = new TableUsers(responseListener, String.valueOf(userNo),typeMoney, String.valueOf(useridx));
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(tableUsers);
            }
        });

    }


}