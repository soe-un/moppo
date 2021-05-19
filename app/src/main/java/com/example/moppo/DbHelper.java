package com.example.moppo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kakao.usermgmt.response.model.User;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "moppo.db";
    private static final int DATABASE_VERSION = 3;


    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE plans (idx INTEGER not null PRIMARY KEY AUTOINCREMENT, "+
                "server_idx INTEGER not null, "+
                "plan_name TEXT not null, plan_order INTEGER not null, income INTEGER not null,"+
                "is_complete BOOLEAN not null, timestamp TEXT not null);");

        //plans: idx, server_idx, plan_name, plan_order, income, is_complete, timestamp
        //------------------------------------------------------각 우선순위가 성공되었으면 1, 아니면 0

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS plans");
        onCreate(db);
    }

    public void putLocalDB(SQLiteDatabase db, PlansTable pt){
        String q = String.format("INSERT INTO plans (idx, server_idx, plan_name, plan_order, income, is_complete, timestamp)"+
                "VALUES (null, %d, '%s', %d, %d, %s, date('%s'));", pt.getServer_idx(), pt.getPlan_name(),
                pt.getPlan_order(), pt.getIncome(), pt.getIs_complete()==true?"true":"false", pt.getTimestamp());

        db.execSQL(q);
    }

    public Cursor readLocalDBPlanlist(SQLiteDatabase db, String selectedDate){
        String q = String.format("SELECT * from plans WHERE date(timestamp) = date('%s');", selectedDate);
        Cursor cursor = db.rawQuery(q, null);

        return cursor;
    }


    //플랜 추가
    public void insertPlan(SQLiteDatabase db, PlansTable pt, String selectedDate){

        /*
        //CHECK THIS IS UPDATE...
        String todaytime = selectedDate;
        Cursor cursor = db.rawQuery("SELECT * from plans WHERE date(timestamp) = date('"+todaytime+"');", null);

        while (cursor.moveToNext()) { //당일 생성 record 존재 시 삭제 후 재생성
            int tmpid = cursor.getInt(cursor.getColumnIndex("userNo"));
            db.execSQL("DELETE FROM plans WHERE date(timestamp) = date('"+todaytime+"') AND userNo ="+tmpid+";");
        }

        cursor.close();

        //생성
        db.execSQL("INSERT INTO plans (idx, userNo, timestamp, first, flagOne) VALUES(null, "+ pt.getUserNo() +", datetime(selectedDate), '" +
                pt.getPlanlist()[0] + "',"+ pt.getFlaglist()[0] +");" );

        //plans: idx, userNo, timestamp, first, second, third, fourth, flagOne, flagTwo, flagThree, flagFour

        for(int i = 1 ; i < 4 ; i ++){
            for(int j = 1 ; j < 4 ; j ++){
                if(i == j && (pt.getPlanlist()[i] != null)){
                    //db.execSQL("UPDATE plans SET "+ plansPlanCols[i] +"='"+pt.getPlanlist()[i]+"', "+plansFlagCols[j]+"= "+ pt.getFlaglist()[j]
                           // + " WHERE date(timestamp) = (date('"+todaytime+"')) AND userNo ="+pt.getUserNo()+";" );
                }
            }
        }*/
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
