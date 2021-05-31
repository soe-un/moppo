package com.example.moppo;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import com.example.moppo.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentStatistic extends Fragment{

    Calendar cal = Calendar.getInstance();
    int month = cal.get(cal.MONTH) + 1;
    int date = cal.get(cal.DATE);
    int []dayList = new int[5];
    int []monthList = new int[5];
    int []dateList = new int[5];
    ArrayList<Entry> values=new ArrayList<>();//Entry 란?
    LineChart chart;

    TextView user, money;
    String userID;
    int idx;
    String userNick;
    int inMoney;

    DbHelper helper;
    SQLiteDatabase db;

    // 날짜 변수들 ex) 달이 바뀌는 거, 28일 등등 -> 더 생각해보기
    public void Get5days(int month,int date, GetTodayStatistic in){
        SimpleDateFormat monthF = new SimpleDateFormat("MM");
        SimpleDateFormat dateF = new SimpleDateFormat("dd");
        Calendar calendar = Calendar.getInstance();

        monthList[0] = Integer.parseInt(monthF.format(calendar.getTime()));
        dateList[0] = Integer.parseInt(dateF.format(calendar.getTime()));
        calendar.add(Calendar.DATE, -1);
        monthList[1] = Integer.parseInt(monthF.format(calendar.getTime()));
        dateList[1] = Integer.parseInt(dateF.format(calendar.getTime()));
        calendar.add(Calendar.DATE, -1);
        monthList[2] = Integer.parseInt(monthF.format(calendar.getTime()));
        dateList[2] = Integer.parseInt(dateF.format(calendar.getTime()));
        calendar.add(Calendar.DATE, -1);
        monthList[3] = Integer.parseInt(monthF.format(calendar.getTime()));
        dateList[3] = Integer.parseInt(dateF.format(calendar.getTime()));
        calendar.add(Calendar.DATE, -1);
        monthList[4] = Integer.parseInt(monthF.format(calendar.getTime()));
        dateList[4] = Integer.parseInt(dateF.format(calendar.getTime()));
        calendar.add(Calendar.DATE, -1);

        for(int i = 0 ; i < 5 ; i ++) {
            dayList[i]=in.getAchievement(monthList[4-i],dateList[4-i]);
            float val=dayList[i];
            values.add(new Entry(i+1,val));
        }

        //setGraph(values);

    }
    //그래프 설정
    public void setGraph(ArrayList<Entry> values){

        LineDataSet lineDataSet;
        lineDataSet=new LineDataSet(values," ");
        //UI 개선
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets=new ArrayList<>();
        dataSets.add(lineDataSet);//data값 집어 넣음
        LineData data=new LineData(dataSets);
        chart.setData(data);

        //X축 설정
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);
        xAxis.setLabelCount(5);
        String[] xAxisLables = new String[]{" "};
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLables));

        //Y축 설정
        YAxis yLAxis = chart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = chart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        //
        Description description = new Description();
        description.setText("");

        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDescription(description);
        chart.invalidate();

        //marker 포인트 데이터 값 보여주기
        mymakerView marker = new mymakerView(getContext(),R.layout.activity_my_maker_view);
        marker.setChartView(chart);
        chart.setMarker(marker);
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        userID = bundle.getString("userID");
        idx = bundle.getInt("idx");
        userNick = bundle.getString("nickname");
        inMoney = bundle.getInt("inMoney");

        helper = new DbHelper(getContext());

        try{ //get database
            db = helper.getWritableDatabase();
        }catch (SQLException ex){
            db = helper.getReadableDatabase();
        }

        return inflater.inflate(R.layout.fragment_statistic,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = view.findViewById(R.id.statistic_text);
        user.setText("닉네임: " + userNick);//user 닉네임 설정

        money = view.findViewById(R.id.by_support_money);
        money.setText(String.valueOf(inMoney)+"원을 후원받았어요!");

        chart= view.findViewById(R.id.linechart);

        values.clear();

        GetTodayStatistic in=new GetTodayStatistic(getActivity(), idx);
        Get5days(month,date, in);
        setGraph(values);

        getPlansfromServer();

    }

    private void getPlansfromServer() { //원하는 idx의 DB 읽어오기

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

                    values.clear();

                    GetTodayStatistic in=new GetTodayStatistic(getActivity(), idx);
                    Get5days(month,date, in);
                    setGraph(values);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        TableUsers tableUsers = new TableUsers(responseListener, String.valueOf(idx));
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(tableUsers);

    }

}