package com.example.moppo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moppo.calendar.DailyPlan;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "moppo.db";
    private static final int DATABASE_VERSION = 5;


    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE plans (idx INTEGER not null PRIMARY KEY AUTOINCREMENT, "+
                "server_idx INTEGER not null, "+
                "plan_name TEXT not null, plan_order INTEGER not null, income INTEGER not null,"+
                "is_complete INTEGER not null, is_updated INTEGER not null, timestamp TEXT not null);");

        //plans: idx, server_idx, plan_name, plan_order, income, is_complete, timestamp
        //------------------------------------------------------각 우선순위가 성공되었으면 1, 아니면 0

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS plans");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS money");
        onCreate(db);
    }

    public int putLocalDB(SQLiteDatabase db, TablePlans pt, int isLocal){

        String q = String.format("INSERT INTO plans (idx, server_idx, plan_name, plan_order, income, is_complete, timestamp, is_updated)"+
                "VALUES (null, %d, '%s', %d, %d, %d, date('%s'), %d);", pt.getServer_idx(), pt.getPlan_name(),
                pt.getPlan_order(), pt.getIncome(), pt.getIs_complete()==1?1:0, pt.getTimestamp(), isLocal);

        db.execSQL(q);

        String qq = String.format("SELECT * FROM plans ORDER BY idx DESC limit 1"); //최근에 추가한 항목을 가져오기
        Cursor c = db.rawQuery(qq, null);

        int localidx = 0;
        while(c.moveToNext()) {
            localidx = c.getInt(c.getColumnIndex("idx"));
        }
        return localidx;
    }

    public void updateLocalDB(SQLiteDatabase db, DailyPlan dp){
        String q = String.format("UPDATE plans SET plan_name = '%s', plan_order = %d, "+
                        "income = %d, is_complete = %d, is_updated = 1 WHERE idx = %d",
                dp.getPlan(), dp.getOrder(), dp.getIncome(), dp.getSelected(), dp.getLocalIdx());

        db.execSQL(q);
    }

    public void deleteLocalDB(SQLiteDatabase db, int idx){
        String q = String.format("UPDATE plans SET is_updated = 2 WHERE idx = %d", idx);

        db.execSQL(q);
    }

    public Cursor readLocalDBPlanlist(SQLiteDatabase db, String selectedDate){
        String q = String.format("SELECT * from plans WHERE date(timestamp) = date('%s') AND is_updated != 2;", selectedDate);
        Cursor cursor = db.rawQuery(q, null);

        return cursor;
    }

    public HashSet<CalendarDay> readisPlan(SQLiteDatabase db){
        HashSet<CalendarDay> hashSet = new HashSet<CalendarDay>();
        String q = String.format("SELECT * from plans WHERE is_updated != 2;");
        Cursor cursor = db.rawQuery(q, null);

        CalendarDay result = null;

        while(cursor.moveToNext()){
            String[] tmp = cursor.getString(cursor.getColumnIndex("timestamp")).split("-");
            result = new CalendarDay(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]) -1, Integer.parseInt(tmp[2]));
            hashSet.add(result);
        }
        return hashSet;
    }

    public void cleanLocalDB(SQLiteDatabase db){
        String q = String.format("DELETE from plans;");
        db.execSQL(q);
    }

    public Cursor readRecentRecord(SQLiteDatabase db){
        String q = String.format("SELECT * from plans order by idx desc limit 1;");
        Cursor cursor = db.rawQuery(q, null);
        return cursor;
    }

    public Cursor goToServerToInsert(SQLiteDatabase db){
        String q = String.format("SELECT * from plans WHERE server_idx = -1 AND is_updated = 1;");
        Cursor cursor = db.rawQuery(q, null);
        return cursor;
    }

    public Cursor goToServerToDelete(SQLiteDatabase db){
        String q = String.format("SELECT * from plans WHERE server_idx != -1 AND is_updated = 2;");
        Cursor cursor = db.rawQuery(q, null);
        return cursor;
    }

    public Cursor goToServerToUpdate(SQLiteDatabase db){
        String q = String.format("SELECT * from plans WHERE server_idx != -1 AND is_updated = 1;");
        Cursor cursor = db.rawQuery(q, null);
        return cursor;
    }

}
