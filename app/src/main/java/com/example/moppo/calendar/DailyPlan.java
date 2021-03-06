package com.example.moppo.calendar;

import android.database.DatabaseUtils;

import com.example.moppo.TablePlans;

public class DailyPlan implements Comparable<DailyPlan>{
    private String plan; //일정
    private int isSelected;//일정을 완료했는지
    private int order; //우선순위
    private int income; //수입
    private int localIdx;

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public int getSelected() {
        return isSelected;
    }

    public void setSelected(int selected) {
        isSelected = selected;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getLocalIdx() {
        return localIdx;
    }

    public void setLocalIdx(int localIdx) {
        this.localIdx = localIdx;
    }

    @Override
    public int compareTo(DailyPlan p) {
        if(this.getOrder() > p.getOrder())
            return 1;
        else
            return -1;
    }

    public TablePlans toPlansTable(int server_idx, String timestamp){
        TablePlans pt = new TablePlans(server_idx, plan, order, income, isSelected, timestamp);
        return pt;
    }

    public DailyPlan(String plan, int isSelected, int order, int income, int localIdx) {
        this.plan = plan;
        this.isSelected = isSelected;
        this.order = order;
        this.income = income;
        this.localIdx = localIdx;
    }


}
