package com.example.moppo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ComponentActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.moppo.DbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class GetTodayStatistic {
    public Context context;

    DbHelper helper;
    SQLiteDatabase db;

    int idx;

    public int result = 0;

    public GetTodayStatistic(Context context, int idx){
        this.context = context;
        this.idx = idx;

        helper = new DbHelper(context);

        try{ //get database
            db = helper.getWritableDatabase();
        }catch (SQLException ex){
            db = helper.getReadableDatabase();
        }
    }

    public void readToday(String selectedDate){

        result = 0;

        String q = String.format("SELECT * from plans WHERE date(timestamp) = date('%s') AND is_updated != 2;", selectedDate);
        Cursor c = db.rawQuery(q, null);

        int sum = 0; //우선순위 총합

        Log.d("readc", String.valueOf(c.getCount()));

        for(int i=1; i <= c.getCount(); i++) {
            sum += i;
        }

        Log.d("reads", String.valueOf(sum));
        //플랜이 저장 안 되어 있으면
        if(sum == 0) {
            //Toast.makeText(context, "저장된 플랜이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        result = 0;
        while (c.moveToNext()){ //c.getCount() 가 전체 order 길이
            int tmpflag = c.getInt(c.getColumnIndex("is_complete"));
            int tmporder = c.getInt(c.getColumnIndex("plan_order"));
            System.out.println("tmpflag: "+tmpflag+" tmporder: "+tmporder);
            //result += (tmpflag)*(5-tmporder);
            if(tmpflag == 1){
                float tmpcnt = (float)c.getCount();
                float tmp = ( (tmpcnt - ((float)tmporder - 1)) / (float)sum ) * 100 ;


                Log.d("MATH", String.valueOf(tmp) );
                Log.d("MATHround", String.valueOf(Math.round(tmp)) );
                result += (int)(Math.round(tmp));
                if(result == 99){ //100% 계산 ...
                    result ++;
                }
            }
        }
        c.close();

    }

    public int getAchievement(int m,int d) {//읽어온 파일의 달성률 반환 함수
        String selDate = String.format("2021-%02d-%02d", m , d);
        readToday(selDate);
        System.out.println(result);

        return result;
    }

}