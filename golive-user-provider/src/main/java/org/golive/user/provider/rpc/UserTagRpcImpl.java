package org.golive.user.provider.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import org.golive.user.constants.UserTagsEnum;
import org.golive.user.interfaces.IUserTagRpc;

@DubboService
public class UserTagRpcImpl implements IUserTagRpc {


    @Override
    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {
        return false;
    }

    @Override
    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {
        return false;
    }

    @Override
    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {
        return false;
    }
}
