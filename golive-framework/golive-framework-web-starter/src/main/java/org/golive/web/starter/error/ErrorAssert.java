package org.golive.web.starter.error;


/**
 * @Author idea
 * @Date: Created in 11:18 2023/8/2
 * @Description
 */
public class ErrorAssert {

    /**
     * 判断参数不能为空
     * @param obj
     * @param goliveBaseError
     */
    public static void isNotNull(Object obj, GoliveBaseError goliveBaseError){
        if(obj == null){
            throw new GoliveErrorException(goliveBaseError.getErrorCode(), goliveBaseError.getErrorMsg());
        }
    }

    /**
     * 判断字符串不为空
     * @param str
     * @param goliveBaseError
     */
    public static void isNotBlank(String str, GoliveBaseError goliveBaseError){
        if(str == null || str.trim().length() == 0){
            throw new GoliveErrorException(goliveBaseError.getErrorCode(), goliveBaseError.getErrorMsg());
        }
    }

    /**
     * flag == true
     * @param flag
     * @param goliveBaseError
     */
    public static void isTrue(boolean flag, GoliveBaseError goliveBaseError){
        if(!flag){
            throw new GoliveErrorException(goliveBaseError.getErrorCode(), goliveBaseError.getErrorMsg());
        }
    }
}
