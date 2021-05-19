package com.example.moppo;

public class DailyPlan {
    private String plan; //일정
    private boolean isSelected;//일정을 완료했는지
    private int order; //우선순위
    private int income; //수입

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
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

    public DailyPlan(String plan, boolean isSelected, int order, int income) {
        this.plan = plan;
        this.isSelected = isSelected;
        this.order = order;
        this.income = income;
    }
}
