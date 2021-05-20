package com.example.moppo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kakao.usermgmt.response.model.User;

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

    public void putLocalDB(SQLiteDatabase db, PlansTable pt, int isLocal){

        String q = String.format("INSERT INTO plans (idx, server_idx, plan_name, plan_order, income, is_complete, timestamp, is_updated)"+
                "VALUES (null, %d, '%s', %d, %d, %d, date('%s'), %d);", pt.getServer_idx(), pt.getPlan_name(),
                pt.getPlan_order(), pt.getIncome(), pt.getIs_complete()==1?1:0, pt.getTimestamp(), isLocal);

        db.execSQL(q);
    }

    public void updateLocalDB(SQLiteDatabase db, DailyPlan dp){
        String q = String.format("UPDATE plans SET plan_name = '%s', plan_order = %d, "+
                        "income = %d, is_complete = %d, is_updated = 1 WHERE idx = %d",
                dp.getPlan(), dp.getOrder(), dp.getIncome(), dp.getSelected(), dp.getLocalIdx());

        db.execSQL(q);
    }

    public void reflectionServer(SQLiteDatabase db, int idx){
        String q = String.format("UPDATE plans SET is_updated = 3 WHERE idx = %d", idx);

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
