package io.andy.pigeon.rpc.core.client;

import io.andy.pigeon.rpc.core.domain.RpcRequest;
import io.andy.pigeon.rpc.core.serialize.Serializer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接管理
 *
 */
public class ConnectClient {

    private static Serializer serializer = Serializer.SerializeEnum.HESSIAN.getSerializer();
    private static RpcInvokerFactory invokerFactory = RpcInvokerFactory.getInstance();

    // ---------------------- client pool map ----------------------

    /**
     * async send
     */
    public static void asyncSend(RpcRequest request, String address) throws Exception {
        // 创建Netty客户端,并与Netty服务端建立连接
        // client pool	[tips03 : may save 35ms/100invoke if move it to constructor, but it is necessary. cause by ConcurrentHashMap.get]
        NettyClient connectClient = ConnectClient.getPool(address);

        try {
            // do invoke
            connectClient.send(request);
        } catch (Exception e) {
            throw e;
        }

    }

    private static volatile ConcurrentHashMap<String, NettyClient> connectClientMap;        // (static) alread addStopCallBack
    private static volatile ConcurrentHashMap<String, Object> connectClientLockMap = new ConcurrentHashMap<>();

    private static NettyClient getPool(String address) throws Exception {

        // init base compont, avoid repeat init
        if (connectClientMap == null) {
            synchronized (ConnectClient.class) {
                if (connectClientMap == null) {
                    // init
                    connectClientMap = new ConcurrentHashMap<String, NettyClient>();
                }
            }
        }

        // get-valid client
        NettyClient nettyClient = connectClientMap.get(address);
        if (nettyClient != null && nettyClient.isValidate()) {
            return nettyClient;
        }

        // lock
        Object clientLock = connectClientLockMap.get(address);
        if (clientLock == null) {
            connectClientLockMap.putIfAbsent(address, new Object());
            clientLock = connectClientLockMap.get(address);
        }

        // remove-create new client
        synchronized (clientLock) {

            // get-valid client, avlid repeat
            nettyClient = connectClientMap.get(address);
            if (nettyClient != null && nettyClient.isValidate()) {
                return nettyClient;
            }

            // remove old
            if (nettyClient != null) {
                nettyClient.close();
                connectClientMap.remove(address);
            }

            // set pool
            NettyClient nettyClient_new = NettyClient.getInstance();
            nettyClient_new.init(address, serializer, invokerFactory);
            connectClientMap.put(address, nettyClient_new);

            return nettyClient_new;
        }

    }
}
