package org.golive.bank.dto;

import java.io.Serial;
import java.io.Serializable;

public class AccountTradeReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7722131868825334678L;

    private long userId;
    private int num;


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "AccountTradeReqDTO{" +
                "userId=" + userId +
                ", num=" + num +
                '}';
    }
}
