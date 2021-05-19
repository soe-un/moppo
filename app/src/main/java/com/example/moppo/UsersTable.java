package com.example.moppo;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UsersTable extends StringRequest {
    final static private ServerInfo si = new ServerInfo();
    final static private String URL = si.getLoginURL();
    final static private String rURL = si.getRegisterURL();
    private Map<String, String> map;

    public UsersTable (Response.Listener<String> listener, String userID, String userPwd){ //To Login
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("userPwd", userPwd);
    }

    public UsersTable (Response.Listener<String> listener, String ... params){ //To Register
        super(Method.POST, rURL, listener, null);

        map = new HashMap<>();
        map.put("userID", params[0]);
        map.put("userPwd", params[1]);
        map.put("name", params[2]);
        map.put("nickname", params[3]);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError{
        return map;
    }
}