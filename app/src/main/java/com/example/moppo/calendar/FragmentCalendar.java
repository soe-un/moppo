package com.example.moppo.calendar;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.moppo.DbHelper;
import com.example.moppo.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class FragmentCalendar extends Fragment implements OnDateSelectedListener {
    MaterialCalendarView cal;
    final TodayDecorator todayDecorator = new TodayDecorator();
    String userID;
    int idx;
    ListView listView;
    public static ArrayAdapter adapter;
    ArrayList<String> list = new ArrayList<>();
    String selectedDate = null;

    TextView all;
    TextView incomplete;
    TextView complete;
    TextView more;

    DbHelper helper;
    SQLiteDatabase db;

    Collection<CalendarDay> dates;

    SwipeRefreshLayout refreshLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        userID = bundle.getString("userID");
        idx = bundle.getInt("idx");

        helper = new DbHelper(getContext());

        try { //get database
            db = helper.getWritableDatabase();
        } catch (SQLException ex) {
            db = helper.getReadableDatabase();
        }

        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cal = (MaterialCalendarView) view.findViewById(R.id.calendarView);

        listView = (ListView) view.findViewById(R.id.onlyPlan);
        all = view.findViewById(R.id.all);
        complete = view.findViewById(R.id.complete);
        incomplete = view.findViewById(R.id.not_yet);
        more = view.findViewById(R.id.more);
        refreshLayout = view.findViewById(R.id.refresh);

        cal.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        cal.addDecorators(new SundayDecorator(),
                new SaturdayDecorator(),
                todayDecorator);

        dates = helper.readisPlan(db);
        cal.addDecorator(new EventDecorator(Color.rgb(124, 164, 215), dates));


        cal.setSelectedDate(CalendarDay.today());
        cal.setOnDateChangedListener((OnDateSelectedListener) this);

        //헤더랑 요일 크기 키워주기
        cal.setHeaderTextAppearance(R.style.HEADER);
        cal.setWeekDayTextAppearance(R.style.WEEK);

        //새로고침
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                read();
                refreshLayout.setRefreshing(false);
            }
        });

        //리스트뷰 부분
        if (selectedDate == null) {
            int year = CalendarDay.today().getYear();
            int month = CalendarDay.today().getMonth() + 1;
            int day = CalendarDay.today().getDay();
            selectedDate = String.format("%04d-%02d-%02d", year, month, day);
        }

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all.setTextColor(Color.BLACK);
                complete.setTextColor(-1979711488);
                incomplete.setTextColor(-1979711488);

                list.clear();
                Cursor cursor = helper.readLocalDBPlanlist(db, selectedDate);
                while (cursor.moveToNext()) {
                    String plan = cursor.getString(cursor.getColumnIndex("plan_name"));

                    list.add(plan);
                }
                adapter.notifyDataSetChanged();
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete.setTextColor(Color.BLACK);
                all.setTextColor(-1979711488);
                incomplete.setTextColor(-1979711488);

                list.clear();
                Cursor cursor = helper.readLocalDBPlanlist(db, selectedDate);
                while (cursor.moveToNext()) {
                    String plan = cursor.getString(cursor.getColumnIndex("plan_name"));
                    int isSelected = cursor.getInt(cursor.getColumnIndex("is_complete"));

                    if (isSelected == 1)
                        list.add(plan);
                }
                adapter.notifyDataSetChanged();
            }
        });

        incomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(complete.getCurrentTextColor());

                incomplete.setTextColor(Color.BLACK);
                all.setTextColor(-1979711488);
                complete.setTextColor(-1979711488);

                list.clear();
                Cursor cursor = helper.readLocalDBPlanlist(db, selectedDate);
                while (cursor.moveToNext()) {
                    String plan = cursor.getString(cursor.getColumnIndex("plan_name"));
                    int isSelected = cursor.getInt(cursor.getColumnIndex("is_complete"));

                    if (isSelected == 0)
                        list.add(plan);
                }
                adapter.notifyDataSetChanged();
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DailyActivity.class);
                intent.putExtra("DATE", selectedDate);
                intent.putExtra("idx", idx);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        read();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        int year = date.getYear();
        int month = date.getMonth() + 1;
        int day = date.getDay();
        selectedDate = String.format("%04d-%02d-%02d", year, month, day); //format 통일

        read();
    }

    private void read() { //기본
        list.clear();
        Cursor cursor = helper.readLocalDBPlanlist(db, selectedDate);

        while (cursor.moveToNext()) {
            String plan = cursor.getString(cursor.getColumnIndex("plan_name"));

            list.add(plan);
        }
        adapter.notifyDataSetChanged();
    }
}
