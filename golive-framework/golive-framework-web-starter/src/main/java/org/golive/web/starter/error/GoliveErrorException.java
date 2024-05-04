package org.golive.web.starter.error;

/**
 * @Author idea
 * @Date: Created in 11:15 2023/8/2
 * @Description
 */
public class GoliveErrorException extends RuntimeException{

    private int errorCode;
    private String errorMsg;

    public GoliveErrorException(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public GoliveErrorException(GoliveBaseError GoliveBaseError) {
        this.errorCode = GoliveBaseError.getErrorCode();
        this.errorMsg = GoliveBaseError.getErrorMsg();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
