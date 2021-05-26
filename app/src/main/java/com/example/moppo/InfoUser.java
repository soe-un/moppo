package com.example.moppo;

import java.util.ArrayList;

public class InfoUser {
    private String nick;
    private String userID;
    private int idx;
    private int totalMoney;
    private int inMoney;
    public InfoUser(String nick) {
        this.nick = nick;
    }


    public InfoUser(String nick, int idx, int totalMoney, String userID, int inMoney) {
        this.nick = nick;
        this.idx = idx;
        this.totalMoney = totalMoney;
        this.userID = userID;
        this.inMoney = inMoney;
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

    public int getInMoney() {
        return inMoney;
    }

    public void setInMoney(int inMoney) {
        this.inMoney = inMoney;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
