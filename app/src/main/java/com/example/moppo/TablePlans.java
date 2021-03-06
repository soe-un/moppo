package com.example.moppo;

public class TablePlans {

    int server_idx;
    String plan_name;
    int plan_order;
    int income;
    int is_complete;
    String timestamp;

    public TablePlans(int server_idx, String plan_name, int plan_order, int income, int is_complete, String timestamp) {
        this.server_idx = server_idx;
        this.plan_name = plan_name;
        this.plan_order = plan_order;
        this.income = income;
        this.is_complete = is_complete;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "{ \"server_idx\": " + server_idx +
                ",\"plan_name\":\"" + plan_name + '\"' +
                ",\"plan_order\":" + plan_order +
                ", \"income\":" + income +
                ", \"is_complete\":" + is_complete +
                ", \"timestamp\":\"" + timestamp + '\"' +
                ", \"empty\":\"no\""+
                '}';
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

    public int getIs_complete() {
        return is_complete;
    }

    public void setIs_complete(int is_complete) {
        this.is_complete = is_complete;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
