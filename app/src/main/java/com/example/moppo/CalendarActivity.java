package com.example.moppo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.CalendarUtils;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity implements OnDateSelectedListener {

    MaterialCalendarView cal;
    final TodayDecorator todayDecorator = new TodayDecorator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        cal = (MaterialCalendarView)findViewById(R.id.calendarView);

        cal.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0 ,1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        cal.addDecorators(new SundayDecorator(),
                new SaturdayDecorator(),
                todayDecorator);

        cal.setSelectedDate(CalendarDay.today());
        cal.setOnDateChangedListener(this);

        //헤더랑 요일 크기 키워주기
        cal.setHeaderTextAppearance(R.style.HEADER);
        cal.setWeekDayTextAppearance(R.style.WEEK);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        int year = date.getYear();
        int month = date.getMonth() + 1;
        int day = date.getDay();
        String selectedDate = year + "." + month + "." + day;

        Intent intent = new Intent(getApplicationContext(), DailyActivity.class);
        intent.putExtra("DATE",selectedDate);
        startActivity(intent);
    }
}