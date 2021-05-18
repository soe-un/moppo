package com.example.moppo;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {


    DbHelper helper;
    SQLiteDatabase db;
    private EditText et_id, et_pass, et_name, et_nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        helper = new DbHelper(this);

        try{ //get database
            db = helper.getWritableDatabase();
        }catch (SQLException ex){
            db = helper.getReadableDatabase();
        }
        et_id           = (EditText) findViewById(R.id.et_id);
        et_pass         = (EditText) findViewById(R.id.et_pass);
        et_name         = (EditText) findViewById(R.id.et_name);
        et_nick         = (EditText) findViewById(R.id.et_nick);

    }

    public void setTextClear(){
        et_id.setText("");
        et_pass.setText("");
        et_name.setText("");
        et_nick.setText("");
    }

    public void register(View target){
        String id = et_id.getText().toString();
        String pwd = et_pass.getText().toString();
        String name = et_name.getText().toString();
        String nickname = et_nick.getText().toString();

        //users: idx, userID, userPwd, name, nickname, totalMoney, updatedTime

        if      (id.equals("") || id == null)               { Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }else if(pwd.equals("") || pwd == null)             { Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }else if(name.equals("") || name == null)           { Toast.makeText(getApplicationContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }else if(nickname.equals("") || nickname == null)   { Toast.makeText(getApplicationContext(), "별명을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }else {

            if(helper.isDupId(db, id)) { //id 중복 check
                Toast.makeText(getApplicationContext(), "중복된 ID입니다.", Toast.LENGTH_SHORT).show();
            }else{
                helper.insertUsers(db, id, pwd, name, nickname);
                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                setTextClear();
            }
        }
    }
}
