package org.golive.api.service.impl;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.golive.api.service.ImService;
import org.golive.api.vo.resp.ImConfigVO;
import org.golive.im.constants.AppIdEnum;
import org.golive.im.interfaces.ImTokenRpc;
import org.golive.web.starter.context.GoliveRequestContext;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ImServiceImpl implements ImService {

    @DubboReference
    private ImTokenRpc imTokenRpc;
    @Resource
    private DiscoveryClient discoveryClient;

    @Override
    public ImConfigVO getImConfig() {
        ImConfigVO imConfigVO = new ImConfigVO();
        imConfigVO.setToken(imTokenRpc.createImLoginToken(GoliveRequestContext.getUserId(), AppIdEnum.GOLIVE_BIZ.getCode()));
        buildImServerAddress(imConfigVO);
        return imConfigVO;
    }

    private void buildImServerAddress(ImConfigVO imConfigVO){
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances("golive-im-core-server");
        Collections.shuffle(serviceInstanceList);
        ServiceInstance aimInstance = serviceInstanceList.get(0);
        imConfigVO.setWsImServerAddress(aimInstance.getHost() + ":8086");
        imConfigVO.setTcpImServerAddress(aimInstance.getHost() + ":8085");
    }
}
