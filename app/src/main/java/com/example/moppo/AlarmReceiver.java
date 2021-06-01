package com.example.moppo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SymbolTable;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.moppo.MainActivity;
import com.example.moppo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {

    DbHelper helper;
    SQLiteDatabase db;
    int idx;

    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        idx = intent.getIntExtra("idx", 0);
        this.mContext = context;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");


        //OREO API 26 이상에서는 채널 필요
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남


            String channelName ="매일 알람 채널";
            String description = "매일 정해진 시간에 알람합니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(description);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남


        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                //text 설정하기
                .setTicker("{Time to watch some cool stuff!}")
                .setContentTitle("오늘, 당신의 하루는 얼마인가요?")
                .setContentText("오늘의 계획으로 당신의 값어치를 올려봐요!")
                .setContentInfo("INFO")
                .setContentIntent(pendingI);

        if (notificationManager != null) {

            // 노티피케이션 동작시킴
            notificationManager.notify(1234, builder.build());

            Calendar nextNotifyTime = Calendar.getInstance();

            // 내일 같은 시간으로 알람시간 결정
            nextNotifyTime.add(Calendar.DATE, 1);

            //  Preference에 설정한 값 저장
            SharedPreferences.Editor editor = context.getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
            editor.putLong("nextNotifyTime", nextNotifyTime.getTimeInMillis());
            editor.apply();

            Date currentDateTime = nextNotifyTime.getTime();
            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
            Toast.makeText(context.getApplicationContext(),"다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

            //cashback event !
            Calendar c = Calendar.getInstance();
            Date today = c.getTime();
            String today_text = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today);

            c.add(Calendar.DATE, -1);
            Date yesterday = c.getTime();
            String yesterday_text = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesterday);

            helper = new DbHelper(context);

            try{ //get database
                db = helper.getWritableDatabase();
            }catch (SQLException ex){
                db = helper.getReadableDatabase();
            }

            cashbackEvent(today_text, yesterday_text);


        }
    }

    //cashback
    public void cashbackEvent(String today, String yesterday){ //사용자가 지정한 하루 시작 1분 전에 실행

        Cursor tc = helper.readLocalDBPlanlist(db, today); //오늘의 목록
        Cursor yc = helper.readLocalDBPlanlist(db, yesterday); //어제의 목록

        int today_success = 0;
        int yesterday_success = 0;
        int tc_sum = 0;
        int yc_sum = 0;
        for(int i=1;i<=tc.getCount();i++)
            tc_sum += i;
        for(int i=1;i<=yc.getCount();i++)
            yc_sum += i;


        if(tc_sum == 0 || yc_sum == 0) {
            return;
        }


        while (tc.moveToNext()){ //tc.getCount() 가 전체 order 길이
            int tmpflag = tc.getInt(tc.getColumnIndex("is_complete"));
            int tmporder = tc.getInt(tc.getColumnIndex("plan_order"));
            System.out.println("tmpflag: "+tmpflag+" tmporder: "+tmporder);

            if(tmpflag == 1){
                float tmpcnt = (float)tc.getCount();
                float tmp = ( (tmpcnt - ((float)tmporder - 1)) / (float)tc_sum ) * 100 ;

                Log.d("MATH", String.valueOf(tmp) );
                Log.d("MATHround", String.valueOf(Math.round(tmp)) );
                today_success += (int)(Math.round(tmp));
                if(today_success == 99){ //100% 계산 ...
                    today_success ++;
                }
            }
        }

        while (yc.moveToNext()){ //c.getCount() 가 전체 order 길이
            int tmpflag = yc.getInt(tc.getColumnIndex("is_complete"));
            int tmporder = yc.getInt(tc.getColumnIndex("plan_order"));
            System.out.println("tmpflag: "+tmpflag+" tmporder: "+tmporder);
            if(tmpflag == 1){
                float tmpcnt = (float)yc.getCount();
                float tmp = ( (tmpcnt - ((float)tmporder - 1)) / (float)yc_sum ) * 100 ;

                Log.d("MATH", String.valueOf(tmp) );
                Log.d("MATHround", String.valueOf(Math.round(tmp)) );
                yesterday_success += (int)(Math.round(tmp));
                if(yesterday_success == 99){ //100% 계산 ...
                    yesterday_success ++;
                }
            }
        }

        if(today_success > yesterday_success){
            //후원 금액 * 달성율 ....
            //캐시백 성공 조건
            int typeM = today_success - yesterday_success;

            Response.Listener<String> responseListener;
            responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        System.out.println(success);
                        String message = jsonObject.getString("message");

                        if (success) { //캐시백 성공
                            int cnt = jsonObject.getInt("cnt");
                            System.out.println("캐시백 성공: "+cnt);
                        } else { //캐시백 실패
                            System.out.println("캐시백 실패: "+message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };
            TableUsers tableUsers = new TableUsers(responseListener, String.valueOf(idx), typeM); //캐시백 요청. 인자 타입 주의
            RequestQueue queue = Volley.newRequestQueue(mContext);
            queue.add(tableUsers);
        }
    }
}

