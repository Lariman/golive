package org.golive.bank.constants;

/**
 * 支付渠道类型
 */
public enum PaySourceEnum {

    GOLIVE_LIVING_ROOM(1, "Golive直播间内支付"),
    GOLIVE_USER_CENTER(2, "用户中心");

    private int code;
    private String desc;

    public static PaySourceEnum find(int code){
        for (PaySourceEnum value : PaySourceEnum.values()) {
            if(value.getCode() == code){
                return value;
            }
        }
        return null;
    }

    PaySourceEnum(int code, String desc) {
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
