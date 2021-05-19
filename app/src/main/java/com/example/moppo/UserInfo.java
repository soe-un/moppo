package com.example.moppo;

import java.util.ArrayList;

public class UserInfo {
    private String nick;
    private int idx;
    private int totalMoney;
    public UserInfo(String nick) {
        this.nick = nick;
    }


    public UserInfo(String nick, int idx, int totalMoney) {
        this.nick = nick;
        this.idx = idx;
        this.totalMoney = totalMoney;
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
}
