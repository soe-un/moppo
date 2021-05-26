package com.example.moppo;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

public class TableServer extends JsonArrayRequest {
    final static private InfoServer si = new InfoServer();
    final static private String puURL = si.getPlanupdatingURL();

    public TableServer(Response.Listener<JSONArray> listener, JSONArray json){ //To Update Server
        super(Method.POST, puURL, json, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        });
    }


}
