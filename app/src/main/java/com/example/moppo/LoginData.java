package com.example.moppo;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class LoginData extends StringRequest {
    final static private ServerInfo si = new ServerInfo();
    final static private String URL = si.getLoginURL();
    private Map<String, String> map;

    public LoginData(String userID, String userPwd, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID",userID);
        map.put("userPwd",userPwd);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
