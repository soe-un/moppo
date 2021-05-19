package com.example.moppo;

import com.android.volley.toolbox.StringRequest;

import java.util.Arrays;

public class PlansTable {

    int server_idx;
    String plan_name;
    int plan_order;
    int income;
    boolean is_complete;
    String timestamp;

    public PlansTable(int server_idx, String plan_name, int plan_order, int income, boolean is_complete, String timestamp) {
        this.server_idx = server_idx;
        this.plan_name = plan_name;
        this.plan_order = plan_order;
        this.income = income;
        this.is_complete = is_complete;
        this.timestamp = timestamp;
    }

    public int getServer_idx() {
        return server_idx;
    }

    public void setServer_idx(int server_idx) {
        this.server_idx = server_idx;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public int getPlan_order() {
        return plan_order;
    }

    public void setPlan_order(int plan_order) {
        this.plan_order = plan_order;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public boolean getIs_complete() {
        return is_complete;
    }

    public void setIs_complete(boolean is_complete) {
        this.is_complete = is_complete;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
