package org.golive.web.starter.error;

public enum BizBaseErrorEnum implements GoliveBaseError{

    PARAM_ERROR(100001, "参数异常"),
    TOKEN_ERROR(100002, "用户token异常");

    private int errorCode;
    private String errorMsg;

    BizBaseErrorEnum(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public int getErrorCode() {
        return 0;
    }

    @Override
    public String getErrorMsg() {
        return null;
    }
}
