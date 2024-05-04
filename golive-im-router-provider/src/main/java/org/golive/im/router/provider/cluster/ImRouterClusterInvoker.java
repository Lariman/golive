package org.golive.im.router.provider.cluster;

import com.alibaba.nacos.api.utils.StringUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;

import java.util.List;

public class ImRouterClusterInvoker<T> extends AbstractClusterInvoker<T> {

    public ImRouterClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    protected Result doInvoke(Invocation invocation, List list, LoadBalance loadbalance) throws RpcException {
        checkWhetherDestroyed();
        String ip = (String) RpcContext.getContext().get("ip");
        if(StringUtils.isEmpty(ip)){
            throw new RuntimeException("ip can not be null!");
        }
        // 获取dubbo所有可以调用的invoker对象
        List<Invoker<T>> invokers = list(invocation);  // list方法返回当前要调用的rpc方法在注册中心上注册的一系列服务提供者的地址,返回地址列表
        Invoker<T> matchInvoker = invokers.stream().filter(invoker -> {
            // 拿到我们服务提供者的暴露地址 (ip:端口格式)
            String serverIp = invoker.getUrl().getHost()  + ":" + invoker.getUrl().getPort();
            return serverIp.equals(ip);
        }).findFirst().orElse(null);
        if(matchInvoker == null){
            throw new RuntimeException("ip is invalid");
        }
        return matchInvoker.invoke(invocation);
    }
}
