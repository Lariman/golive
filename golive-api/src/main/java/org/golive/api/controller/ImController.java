package org.golive.api.controller;

import jakarta.annotation.Resource;
import org.golive.api.service.ImService;
import org.golive.api.vo.resp.ImConfigVO;
import org.golive.common.interfaces.vo.WebResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/im")
public class ImController{

    @Resource
    private ImService imService;

    @PostMapping("/getImConfig")
    public WebResponseVO getImConfig(){
        ImConfigVO imConfigVO = imService.getImConfig();
        return WebResponseVO.success(imConfigVO);
    }
}
