package com.example.moppo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class RegisterData extends AsyncTask<String, Void, String> {
    private static String TAG = "phptest";
    Context registerContext;

    public RegisterData(Context c){
        this.registerContext = c;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, "POST response - " + result);
        if (result.equals("success")) {
            Toast.makeText(registerContext, "회원가입이 되었습니다.", Toast.LENGTH_LONG).show();
        } else { Toast.makeText(registerContext, result, Toast.LENGTH_LONG).show(); }
    }

    @Override
    protected String doInBackground(String... params){
        //id, password, name, nickname
        String userID = (String)params[1];
        String userPwd = (String)params[2];
        String name = (String)params[3];
        String nickname = (String)params[4];

        String serverURL = (String)params[0];
        String postParameters = "userID=" + userID + "&userPwd=" + userPwd + "&name=" + name + "&nickname=" + nickname;

        try{

            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();


            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();


            int responseStatusCode = httpURLConnection.getResponseCode();
            Log.d(TAG, "POST response code - " + responseStatusCode);

            InputStream inputStream;
            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }


            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }

            bufferedReader.close();

            return sb.toString();


        } catch (Exception e) {

            Log.d(TAG, "InsertData: Error ", e);

            return new String("Error: " + e.getMessage());
        }


    }
}


