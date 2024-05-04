package org.golive.im.constants;

public enum AppIdEnum {

    GOLIVE_BIZ(10001, "golive直播业务");

    int code;
    String desc;

    AppIdEnum(int code, String desc) {
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
