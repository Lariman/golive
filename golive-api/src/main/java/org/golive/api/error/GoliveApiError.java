package org.golive.api.error;

import org.golive.web.starter.error.GoliveBaseError;

public enum GoliveApiError implements GoliveBaseError {

    GOLIVE_ROOM_TYPE_MISSING(10001, "需要给定直播间类型");


    int code;
    String desc;

    GoliveApiError(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public int getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return desc;
    }
}
