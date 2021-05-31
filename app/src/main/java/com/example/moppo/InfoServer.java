package com.example.moppo;

public class InfoServer {
    final static private String IPaddress = "3.36.103.244";
    final static private String loginURL = "http://"+IPaddress+"/login.php";
    final static private String registerURL = "http://"+IPaddress+"/register.php";
    final static private String rankingURL = "http://"+IPaddress+"/ranking.php";
    final static private String planlistingURL = "http://"+IPaddress+"/planlisting.php";
    final static private String planupdatingURL = "http://"+IPaddress+"/planUpdating.php";
    final static private String doitURL = "http://"+IPaddress+"/doit.php";
    final static private String cashbackURL = "http://"+IPaddress+"/cashback.php";

    public static String getIPaddress() {
        return IPaddress;
    }

    public static String getLoginURL() {
        return loginURL;
    }

    public static String getRegisterURL(){
        return registerURL;
    }

    public static String getRankingURL() { return rankingURL; }

    public static String getPlanlistingURL() { return planlistingURL; }

    public static String getPlanupdatingURL() { return planupdatingURL; }

    public static String getDoitURL() { return doitURL; }

    public static String getCashbackURL() { return  cashbackURL; }
}
