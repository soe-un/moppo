package com.example.moppo;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class TableUsers extends StringRequest {
    final static private InfoServer si = new InfoServer();
    final static private String loURL = si.getLoginURL();
    final static private String reURL = si.getRegisterURL();
    final static private String raURL = si.getRankingURL();
    final static private String plURL = si.getPlanlistingURL();
    final static private String puURL = si.getPlanupdatingURL();
    final static private String diURL = si.getDoitURL();
    final static private String cbURL = si.getCashbackURL();
    final static private String gMURL = si.getGetTotalURL();
    private Map<String, String> map;
    private Map<String, JSONArray> servermap;

    public TableUsers(Response.Listener<String> listener){ //To Ranking
        super(Method.POST, raURL, listener, null);
    }

    public TableUsers(Response.Listener<String> listener, JSONArray inserts, JSONArray updates, JSONArray deletes){
        super(Method.POST, puURL, listener, null);

        servermap = new HashMap<String, JSONArray>();
        servermap.put("insert_list", inserts);
        servermap.put("update_list", updates);
        servermap.put("delete_list", deletes);
    }

    public TableUsers(Response.Listener<String> listener, String userNo){ //To Plan listing
        super(Method.POST, plURL, listener, null);

        map = new HashMap<>();
        map.put("userNo", userNo);
    }

    public TableUsers(Response.Listener<String> listener, int idx){
        super(Method.POST, gMURL, listener, null);

        map = new HashMap<>();
        map.put("userIdx", String.valueOf(idx));
    }

    public TableUsers(Response.Listener<String> listener, String userID, String userPwd){ //To Login
        super(Method.POST, loURL, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("userPwd", userPwd);
    }

    public TableUsers(Response.Listener<String> listener, String userNo, int typeMoney){ //To CashBack
        super(Method.POST, cbURL, listener, null);

        map = new HashMap<>();
        map.put("userNo", userNo);
        map.put("typeRate", String.valueOf(typeMoney));
    }

    public TableUsers(Response.Listener<String> listener, String userNo, String typeMoney, String typeNo){ //To Money table
        super(Method.POST, diURL, listener, null);

        map = new HashMap<>();
        map.put("userNo", userNo);
        map.put("typeMoney", typeMoney);
        map.put("typeNo", typeNo);
    }


    public TableUsers(Response.Listener<String> listener, String ... params){ //To Register
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