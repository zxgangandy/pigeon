package io.andy.pigeon.rpc.core.client;



import io.andy.pigeon.rpc.core.domain.RpcFutureResponse;
import io.andy.pigeon.rpc.core.domain.RpcResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 */
public class RpcInvokerFactory {

    private static volatile RpcInvokerFactory instance = new RpcInvokerFactory();

    public static RpcInvokerFactory getInstance() {
        return instance;
    }

    private ConcurrentMap<String, RpcFutureResponse> futureResponsePool = new ConcurrentHashMap<String, RpcFutureResponse>();

    public void setInvokerFuture(String requestId, RpcFutureResponse futureResponse) {
        futureResponsePool.put(requestId, futureResponse);
    }

    public void notifyInvokerFuture(String requestId, final RpcResponse response) {
        final RpcFutureResponse futureResponse = futureResponsePool.get(requestId);
        if (futureResponse == null) {
            return;
        }
        futureResponse.setResponse(response);
        futureResponsePool.remove(requestId);
    }
}
