package com.example.moppo;

import java.util.Arrays;

public class PlansTable {
    //plans: idx, userNo, timestamp, first, second, third, fourth, flagOne, flagTwo, flagThree, flagFour
    //-------------------------------------------------------------각 우선순위가 성공되었으면 1, 아니면 0
    //ref--FOREIGN KEY (userNo) REFERENCES users(idx)

    int idx;
    int userNo;
    String[] planlist;
    int[] flaglist;
    String timestamp;

    public PlansTable(){

    }

    @Override
    public String toString() {
        return "PlansTable{" +
                "userNo=" + userNo +
                ", planlist=" + Arrays.toString(planlist) +
                ", flaglist=" + Arrays.toString(flaglist) +
                '}';
    }

    //To add plan
    public PlansTable(int userNo, String[] planlist, int[] flaglist) {
        this.userNo = userNo;
        this.planlist = planlist;
        this.flaglist = flaglist;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getUserNo() {
        return userNo;
    }

    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public String[] getPlanlist() {
        return planlist;
    }

    public void setPlanlist(String[] planlist) {
        this.planlist = planlist;
    }

    public int[] getFlaglist() {
        return flaglist;
    }

    public void setFlaglist(int[] flaglist) {
        this.flaglist = flaglist;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
