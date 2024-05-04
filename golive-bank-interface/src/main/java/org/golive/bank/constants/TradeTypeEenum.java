package org.golive.bank.constants;

public enum TradeTypeEenum {

    SEND_GIFT_TRADE(0, "送礼物交易"),
    LIVING_RECHARGE(1, "直播间充值");


    int code;
    String desc;

    TradeTypeEenum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
