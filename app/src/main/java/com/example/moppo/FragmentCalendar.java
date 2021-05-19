package com.example.moppo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;

public class FragmentCalendar extends Fragment implements OnDateSelectedListener{
    MaterialCalendarView cal;
    final TodayDecorator todayDecorator = new TodayDecorator();
    static String userID;
    static int idx;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        userID = bundle.getString("userID");
        idx = bundle.getInt("idx");

        return inflater.inflate(R.layout.fragment_calendar,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cal = (MaterialCalendarView)view.findViewById(R.id.calendarView);

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
        cal.setOnDateChangedListener((OnDateSelectedListener) this);

        //헤더랑 요일 크기 키워주기
        cal.setHeaderTextAppearance(R.style.HEADER);
        cal.setWeekDayTextAppearance(R.style.WEEK);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        int year = date.getYear();
        int month = date.getMonth() + 1;
        int day = date.getDay();
        String selectedDate = String.format("%04d-%02d-%02d", year, month, day); //format 통일

        Intent intent = new Intent(getActivity(), DailyActivity.class);
        intent.putExtra("DATE",selectedDate);
        intent.putExtra("idx", idx);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }
}
