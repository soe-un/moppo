package com.example.moppo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kakao.usermgmt.response.model.User;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "moppo.db";
    private static final int DATABASE_VERSION = 4;


    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE plans (idx INTEGER not null PRIMARY KEY AUTOINCREMENT, "+
                "server_idx INTEGER not null, "+
                "plan_name TEXT not null, plan_order INTEGER not null, income INTEGER not null,"+
                "is_complete BOOLEAN not null, is_updated INTEGER not null, timestamp TEXT not null);");

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
                "VALUES (null, %d, '%s', %d, %d, %s, date('%s'), %d);", pt.getServer_idx(), pt.getPlan_name(),
                pt.getPlan_order(), pt.getIncome(), pt.getIs_complete()==true?"true":"false", pt.getTimestamp(), isLocal);

        db.execSQL(q);
    }

    public void updateLocalDB(SQLiteDatabase db, DailyPlan dp){
        String q = String.format("UPDATE plans SET plan_name = '%s', plan_order = %d, "+
                        "income = %d, is_complete = %s, is_updated = 1 WHERE idx = %d",
                dp.getPlan(), dp.getOrder(), dp.getIncome(), dp.getSelected()==true?"true":"false", dp.getLocalIdx());

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


    //돈 추가 - 입금
    //money: idx, userNo, timestamp, typeFlag, typeMoney, typeNo
    //-------------------------------입금: 0
    //-------------------------------출금: 1
    //---------------------------------------------------자기자신일 경우 우선순위를 통해 입금된 것
    public void insertMoney(SQLiteDatabase db, MoneyTable mt){
        int tmpusermoney = getTotalMoneybyIdx(db, mt.getUserNo());
        int tmptypemoney = getTotalMoneybyIdx(db, mt.getTypeNo());

        if (mt.getUserNo() == mt.getTypeNo()){ //add just one record

            db.execSQL("INSERT INTO money VALUES(null, " + mt.getUserNo() + ", datetime('now', 'localtime'), "
                    + mt.getTypeFlag() + ","+ mt.getTypeMoney() +"," + mt.getTypeNo() + ");");

            tmpusermoney += mt.getTypeMoney();

            db.execSQL("UPDATE users SET totalMoney = "+ tmpusermoney +", updatedTime = datetime('now', 'localtime') where idx = "+mt.getUserNo()+";");
        }else{ //add two record
            int tmptypeflag;

            if(mt.getTypeFlag() == 0){
                tmpusermoney += mt.getTypeMoney();
                tmptypemoney -= mt.getTypeMoney();
                tmptypeflag = 1;
            }else{
                tmpusermoney -= mt.getTypeMoney();
                tmptypemoney += mt.getTypeMoney();
                tmptypeflag = 0;
            }

            //주체
            db.execSQL("INSERT INTO money VALUES(null, " + mt.getUserNo() + ", datetime('now', 'localtime'), "
                    + mt.getTypeFlag() + ","+ mt.getTypeMoney() +"," + mt.getTypeNo() + ");");
            //상대
            db.execSQL("INSERT INTO money VALUES(null, " + mt.getTypeMoney() + ", datetime('now', 'localtime'), "
                    + tmptypeflag + ","+ mt.getUserNo() +"," + mt.getTypeNo() + ");");

            db.execSQL("UPDATE users SET totalMoney = "+ tmpusermoney +", updatedTime = datetime('now', 'localtime') where idx = "+mt.getUserNo()+";");
            db.execSQL("UPDATE users SET totalMoney = "+ tmptypemoney +", updatedTime = datetime('now', 'localtime') where idx = "+mt.getTypeNo()+";");

        }

    }


    //plan list 반환
    public Cursor getPlanlist(SQLiteDatabase db, int userNo, String todayTime){
        Cursor cursor = db.rawQuery("SELECT * FROM plans WHERE date(timestamp) = date('"+todayTime+"') AND userNo = " + userNo + ";", null);
        return cursor;
    }

    //users 에서 ID (string)로 IDX(int) 찾기
    public int getIdxbyID(SQLiteDatabase db, String id){
        int result;
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE userID = '"+id+"';", null);
        cursor.moveToFirst();
        result = cursor.getInt(cursor.getColumnIndex("idx"));
        cursor.close();
        return result;
    }

    //users 에서 idx(int)로 totalMoeny(int) 찾기
    public int getTotalMoneybyIdx(SQLiteDatabase db, int idx){
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE idx = "+idx+";", null);
        if(cursor.moveToFirst()){
            result = cursor.getInt(cursor.getColumnIndex("totalMoney"));
        }
        cursor.close();
        return result;
    }

    //plans 에서 idx(int)로 timestamp 찾기
    public String getTimestampbyIdx(SQLiteDatabase db, int idx){
        String result;
        Cursor cursor = db.rawQuery("SELECT timestamp FROM plans WHERE idx = "+idx+";", null);
        cursor.moveToFirst();
        result = cursor.getString(cursor.getColumnIndex("timestamp"));
        cursor.close();
        return result;
    }


}
