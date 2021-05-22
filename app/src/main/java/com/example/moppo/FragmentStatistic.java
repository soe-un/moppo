package com.example.moppo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import com.example.moppo.MainActivity;

public class FragmentStatistic extends Fragment{

    GetTodayStatistic in;
    Calendar cal = Calendar.getInstance();
    int month = cal.get(cal.MONTH) + 1;
    int date = cal.get(cal.DATE);
    int []daylist = new int[5];
    ArrayList<Entry> values=new ArrayList<>();//Entry 란?
    TextView user;
    static String userID;
    static int idx;
    static String userNick;

    // 날짜 변수들 ex) 달이 바뀌는 거, 28일 등등 -> 더 생각해보기
    public void Get5days(int month,int date){

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
    public void setGraph(View view, ArrayList<Entry> values){
        LineChart chart;

        chart= view.findViewById(R.id.linechart);

        LineDataSet set1;
        set1=new LineDataSet(values,"최근 5일 달성률");

        ArrayList<ILineDataSet> dataSets=new ArrayList<>();

        dataSets.add(set1);//data값 집어 넣음

        LineData data=new LineData(dataSets);

        chart.setData(data);
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        userID = bundle.getString("userID");
        idx = bundle.getInt("idx");
        userNick = bundle.getString("nickname");

        return inflater.inflate(R.layout.fragment_statistic,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = view.findViewById(R.id.statistic_text);
        user.setText(userNick);//user 닉네임 설정

        values.clear();

        in=new GetTodayStatistic(getContext());

        Get5days(month,date);
        setGraph(view,values);

    }
}
