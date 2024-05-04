package org.golive.gift.constants;

public enum SendGiftTypeEnum {
    DEFAULT_SEND_GIFT(0, "直播间默认送礼物"),
    PK_SEND_GIFT(1, "直播间PK送礼物");

    Integer code;
    String desc;

    SendGiftTypeEnum(Integer code, String desc) {
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
