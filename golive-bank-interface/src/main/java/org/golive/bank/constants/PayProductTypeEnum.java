package org.golive.bank.constants;

public enum PayProductTypeEnum {

    GOLIVE_COIN(0, "直播间充值-golive虚拟币产品");

    Integer code;
    String desc;

    PayProductTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
