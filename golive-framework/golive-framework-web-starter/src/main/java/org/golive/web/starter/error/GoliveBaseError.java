package org.golive.web.starter.error;

/**
 * @Author idea
 * @Date: Created in 11:26 2023/8/2
 * @Description
 */
public interface GoliveBaseError {

    // 定义返回的错误代码
    int getErrorCode();

    // 定义返回的错误提示语
    String getErrorMsg();
}
