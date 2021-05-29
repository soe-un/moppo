package com.example.moppo;

import android.content.Context;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import com.example.moppo.MainActivity;

public class FragmentStatistic extends Fragment{

    Calendar cal = Calendar.getInstance();
    int month = cal.get(cal.MONTH) + 1;
    int date = cal.get(cal.DATE);
    int []daylist;
    ArrayList<Entry> values=new ArrayList<>();//Entry 란?
    LineChart chart;

    TextView user, money;
    String userID;
    int idx;
    String userNick;
    int inMoney;

    // 날짜 변수들 ex) 달이 바뀌는 거, 28일 등등 -> 더 생각해보기
    public void Get5days(int month,int date, GetTodayStatistic in){
        daylist = new int[5];
        for(int i = 0 ; i < 5 ; i ++) {
            daylist[i]=in.getAchievement(month,date-4+i);
        }
        for(int i = 0 ; i < 5 ; i ++) {
            float val=daylist[i];
            values.add(new Entry(i+1,val));
        }

        //setGraph(values);

    }
    //그래프 설정
    public void setGraph(ArrayList<Entry> values){

        LineDataSet lineDataSet;
        lineDataSet=new LineDataSet(values,"최근 5일 달성률");
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



    }

}
