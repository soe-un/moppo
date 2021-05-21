package com.example.moppo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SupportActivity extends AppCompatActivity {

    DbHelper helper;
    SQLiteDatabase db;

    static int useridx;



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

        useridx = getIntent().getIntExtra("idx", 0);

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
        UsersTable usersTable = new UsersTable(responseListener, String.valueOf(useridx));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(usersTable);

    }
}