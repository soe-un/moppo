package com.example.moppo;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UsersTable extends StringRequest {
    final static private ServerInfo si = new ServerInfo();
    final static private String loURL = si.getLoginURL();
    final static private String reURL = si.getRegisterURL();
    final static private String raURL = si.getRankingURL();
    final static private String plURL = si.getPlanlistingURL();
    private Map<String, String> map;

    public UsersTable (Response.Listener<String> listener){ //To Ranking
        super(Method.POST, raURL, listener, null);
    }

    public UsersTable (Response.Listener<String> listener, String userNo){ //To Plan listing
        super(Method.POST, plURL, listener, null);

        map = new HashMap<>();
        map.put("userNo", userNo);
    }

    public UsersTable (Response.Listener<String> listener, String userID, String userPwd){ //To Login
        super(Method.POST, loURL, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("userPwd", userPwd);
    }

    public UsersTable (Response.Listener<String> listener, String ... params){ //To Register
        super(Method.POST, reURL, listener, null);

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