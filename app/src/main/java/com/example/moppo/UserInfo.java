package com.example.moppo;

import java.util.ArrayList;

public class UserInfo {
    private String nick;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public UserInfo(String nick) {
        this.nick = nick;
    }
}
