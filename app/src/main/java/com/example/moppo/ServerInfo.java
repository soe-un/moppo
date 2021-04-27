package com.example.moppo;

public class ServerInfo {
    final static private String IPaddress = "172.30.1.15";
    final static private String loginURL = "http://"+IPaddress+"/login.php";

    public static String getIPaddress() {
        return IPaddress;
    }

    public static String getLoginURL() {
        return loginURL;
    }
}
