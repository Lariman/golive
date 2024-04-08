package org.golive.id.generator.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.id.generator.interfaces.IdGenerateRpc;
import org.golive.id.generator.provider.service.IdGenerateService;

@DubboService
public class IdGenerateRpcImpl implements IdGenerateRpc {

    @Resource
    private IdGenerateService idBuilderService;

    @Override
    public Long getSeqId(Integer id) {
        return null;
    }

    @Override
    public Long getUnSeqId(Integer id) {
        return null;
    }
}
