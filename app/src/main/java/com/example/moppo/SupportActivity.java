package com.example.moppo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;

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

        useridx = getIntent().getIntExtra("idx", 0);//액티비티로부터 알고싶은 useridx를 알아냄

        //Cursor cs=db.rawQuery("select * from PlansTable where idx='useridx'",null);//useridx에 해당하는 데이터 가져옴

        //int planorder=cs.getInt(1);//우선순위 정보
        //int iscompleted=cs.getInt(3);//했는지 안했는지 정보 0,1

        //통계 프래그먼트 화면
        FragmentStatistic fragment=new FragmentStatistic();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout2,fragment).commit();

        //후원 금액 text
        EditText support_money=(EditText)findViewById(R.id.support_money);


        //이거로 서버에서 정보를 얻어와서 fragment statistic에 뿌릴 것.
        //editText, button, fragment만 넣으면 될 듯!

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