package com.example.moppo;

public class TableMoney {
    //money: idx, userNo, timestamp, typeFlag, typeMoney, typeNo
    //------------주체-------------------------------------상대
    //-------------------------------typeFlag 1: 입금, flag 0: 출금
    //ref--FOREIGN KEY (userNo) REFERENCES users(idx),
    //-----FOREIGN KEY (typeNo) REFERENCES users(idx)

    int idx;
    int userNo;
    int typeFlag;
    int typeMoney;
    int typeNo;

    public TableMoney(){

    }

    //case: PLAN, realIncome
    public TableMoney(int id, int typeMoney) {
        this.userNo = id;
        this.typeMoney = typeMoney;
        int typeFlag = 0;
        int typeNo = id;
    }

    @Override
    public String toString() {
        return "MoneyTable{" +
                "userNo=" + userNo +
                ", typeFlag=" + typeFlag +
                ", typeMoney=" + typeMoney +
                ", typeNo=" + typeNo +
                '}';
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

    public int getTypeFlag() {
        return typeFlag;
    }

    public void setTypeFlag(int typeFlag) {
        this.typeFlag = typeFlag;
    }

    public int getTypeMoney() {
        return typeMoney;
    }

    public void setTypeMoney(int typeMoney) {
        this.typeMoney = typeMoney;
    }

    public int getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(int typeNo) {
        this.typeNo = typeNo;
    }



}
