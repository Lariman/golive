package org.golive.web.starter.error;

import jakarta.servlet.http.HttpServletRequest;
import org.golive.common.interfaces.vo.WebResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author idea
 * @Date: Created in 11:11 2023/8/2
 * @Description
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public WebResponseVO errorHandler(HttpServletRequest request, Exception e){
        LOGGER.error(request.getRequestURI() + ", error is ", e);
        return WebResponseVO.sysError("系统异常");
    }

    // 异常捕获,监听自定义异常
    @ExceptionHandler(value = GoliveErrorException.class)
    @ResponseBody
    public WebResponseVO sysErrorHandler(HttpServletRequest request, GoliveErrorException e){
        LOGGER.error(request.getRequestURI() + ", error is ", e);
        return WebResponseVO.bizError(e.getErrorCode(), e.getErrorMsg());
    }
}
