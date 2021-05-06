package com.example.moppo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_id,et_pass,et_name,et_nick;
    private Button register_btn;

    private static ServerInfo si = new ServerInfo();
    private static String IP_ADDRESS = si.getIPaddress();
    private static String TAG = "phptest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //아이디 값 찾아주기
        et_id=findViewById(R.id.et_id);
        et_pass=findViewById(R.id.et_pass);
        et_name=findViewById(R.id.et_name);
        et_nick=findViewById(R.id.et_nick);
        register_btn=findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){//Eidt Text에 입력되어 있는 값 가져오기
                String userID=et_id.getText().toString();
                String userPass=et_pass.getText().toString();
                String name=et_name.getText().toString();
                String nickname=et_nick.getText().toString();

                RegisterData task = new RegisterData(getApplicationContext());
                task.execute("http://" + IP_ADDRESS + "/register.php", userID, userPass, name, nickname);

            }
        });
    }


}
