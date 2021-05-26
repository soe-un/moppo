package com.example.moppo;

import java.util.ArrayList;

public class InfoUser {
    private String nick;
    private String userID;
    private int idx;
    private int totalMoney;
    public InfoUser(String nick) {
        this.nick = nick;
    }


    public InfoUser(String nick, int idx, int totalMoney, String userID) {
        this.nick = nick;
        this.idx = idx;
        this.totalMoney = totalMoney;
        this.userID = userID;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
