package org.golive.api.service;

import org.golive.api.vo.HomePageVO;


public interface IHomePageService {


    /**
     * 初始化页面获取的信息
     *
     * @param userId
     * @return
     */
    HomePageVO initPage(Long userId);


}
