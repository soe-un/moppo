package com.example.moppo;

public class DailyPlan {
    private String plan; //일정
    private boolean isSelected;//일정을 완료했는지 //0이면 no 1이면 yes
    private String order; //우선순위 //힘들면 그냥 string 해라
    private String income; //수입 //힘들면 그냥 string 해라

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public DailyPlan(String plan, Boolean isSelected, String order, String income) {
        this.plan = plan;
        this.isSelected = isSelected;
        this.order = order;
        this.income = income;
    }
}
