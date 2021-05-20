package com.example.moppo;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.Map;

public class ServerTable extends JsonArrayRequest {
    final static private ServerInfo si = new ServerInfo();
    final static private String puURL = si.getPlanupdatingURL();

    public ServerTable (Response.Listener<JSONArray> listener, JSONArray json){ //To Update Server
        super(Method.POST, puURL, json, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        });
    }


}
