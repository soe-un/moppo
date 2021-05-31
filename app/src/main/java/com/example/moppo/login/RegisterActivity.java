package com.example.moppo.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.moppo.R;
import com.example.moppo.TableUsers;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {


    private EditText et_id, et_pass, et_name, et_nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
            Response.Listener<String> responseListener;
            responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        System.out.println(success);
                        String message = jsonObject.getString("message");

                        if (success) {
                            Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            //로그인으로 돌아가기
                            RegisterActivity.this.finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "회원가입에 실패하셨습니다. " + message, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            TableUsers tableUsers = new TableUsers(responseListener, id, pwd, name, nickname);
            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
            queue.add(tableUsers);
        }
    }
}
