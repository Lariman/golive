package org.golive.api.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.golive.api.service.IHomePageService;
import org.golive.api.vo.HomePageVO;
import org.golive.user.constants.UserTagsEnum;
import org.golive.user.dto.UserDTO;
import org.golive.user.interfaces.IUserRpc;
import org.golive.user.interfaces.IUserTagRpc;
import org.springframework.stereotype.Service;

/**
 * @Author idea
 * @Date: Created in 23:03 2023/7/19
 * @Description
 */
@Service
public class HomePageServiceImpl implements IHomePageService {

    @DubboReference
    private IUserRpc userRpc;
    @DubboReference
    private IUserTagRpc userTagRpc;

    @Override
    public HomePageVO initPage(Long userId) {
        UserDTO userDTO = userRpc.getByUserId(userId);
        HomePageVO homePageVO = new HomePageVO();
        if (userDTO != null) {
            homePageVO.setAvatar(userDTO.getAvatar());
            homePageVO.setUserId(userId);
            homePageVO.setNickName(userDTO.getNickName());
            //vip用户有权利开播
            homePageVO.setShowStartLivingBtn(userTagRpc.containTag(userId, UserTagsEnum.IS_VIP));
        }
        return homePageVO;
    }
}
