package org.golive.api.service;

import jakarta.servlet.http.HttpServletResponse;
import org.golive.common.interfaces.vo.WebResponseVO;

public interface IUserLoginService {

    WebResponseVO sendLoginCode(String phone);

    WebResponseVO login(String phone, Integer code, HttpServletResponse response);
}
