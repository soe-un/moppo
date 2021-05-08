package com.example.moppo;

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
