package com.example.moppo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kakao.usermgmt.response.model.User;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "moppo.db";
    private static final int DATABASE_VERSION = 2;

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (idx INTEGER not null PRIMARY KEY AUTOINCREMENT,"+
                "userID TEXT not null, userPwd TEXT not null, name TEXT not null, nickname TEXT not null,"+
                "totalMoney INTEGER not null, updatedTime TEXT not null);");

        db.execSQL("CREATE TABLE money (idx INTEGER not null PRIMARY KEY AUTOINCREMENT,"+
                "userNo INTEGER not null, timestamp TEXT not null,"+
                "typeFlag INTEGER not null, typeMoney INTEGER not null, typeNo INTEGER not null,"+
                "FOREIGN KEY (userNo) REFERENCES users(idx), FOREIGN KEY (typeNo) REFERENCES users(idx));");

        db.execSQL("CREATE TABLE plans (idx INTEGER not null PRIMARY KEY AUTOINCREMENT,"+
                "userNo INTEGER not null, timestamp TEXT not null,"+
                "first TEXT not null, second TEXT, third TEXT, fourth TEXT,"+
                "flagOne TEXT not null, flagTwo TEXT, flagThree TEXT, flagFour TEXT,"+
                "FOREIGN KEY (userNo) REFERENCES users(idx));");

        //users: idx, userID, userPwd, name, nickname, totalMoney, updatedTime

        //money: idx, userNo, timestamp, typeFlag, typeMoney, typeNo
        //------------주체-------------------------------------상대
        //-------------------------------typeFlag 1: 입금, flag 2: 출금
        //ref--FOREIGN KEY (userNo) REFERENCES users(idx),
        //-----FOREIGN KEY (typeNo) REFERENCES users(idx)

        //plans: idx, userID, timestamp, first, second, third, fourth, flagOne, flagTwo, flagThree, flagFour
        //-------------------------------------------------------------각 우선순위가 성공되었으면 1, 아니면 0
        //ref--FOREIGN KEY (userNo) REFERENCES users(idx)

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    //중복 ID 확인
    public boolean isDupId(SQLiteDatabase db, String id){
        boolean result;
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE userID = '"+id+"';", null);
        if(cursor.moveToNext()){
            result = true;
        } else{
            result = false;
        }
        return result;
    }

    //회원가입 - 유저 추가
    public void insertUsers(SQLiteDatabase db, String id, String pwd, String name, String nickname){
        db.execSQL("INSERT INTO users VALUES(null, '" + id + "','" + pwd + "','" + name + "','" + nickname + "', 0, datetime('now', 'localtime'));");
    }

    //플랜 추가
    public void insertPlan(SQLiteDatabase db, String id, String[] planlist, String[] flaglist){
        db.execSQL("INSERT INTO plans VALUES(null, '"+ id +"', datetime('now', 'localtime'), '" +
                planlist[0] + "','" + planlist[1] + "','" + planlist[2] + "','" + planlist[3] +
                "','"+ flaglist[0] + "','" + flaglist[1] + "','" + flaglist[2] + "','" + flaglist[3] +"');" );
    }

    //로그인 확인
    public String isRightUsers(SQLiteDatabase db, String id, String pwd){
        //실패 시 null, 성공 시 user의 nickname 반환
        String result = null;
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE userID = '"+id+"' AND userPwd = '"+pwd+"';", null);
        if(cursor.moveToNext()){
            result = cursor.getString(cursor.getColumnIndex("nickname"));
        }else{
            result = null;
        }
        return result;
    }

    //전체 리스트 반환 (순서는 가입 순)
    public Cursor getRankList(SQLiteDatabase db){
        Cursor cursor = db.rawQuery("SELECT * FROM users;", null);
        return cursor;
    }
}
