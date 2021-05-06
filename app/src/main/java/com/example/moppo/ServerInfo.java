package com.example.moppo;

public class ServerInfo {
    final static private String IPaddress = "52.78.45.99";
    final static private String loginURL = "http://"+IPaddress+"/login.php";

    public static String getIPaddress() {
        return IPaddress;
    }

    public static String getLoginURL() {
        return loginURL;
    }
}
