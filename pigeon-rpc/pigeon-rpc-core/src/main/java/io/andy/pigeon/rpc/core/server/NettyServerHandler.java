package io.andy.pigeon.rpc.core.server;

import io.andy.pigeon.rpc.core.domain.RpcRequest;
import io.andy.pigeon.rpc.core.domain.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Netty 服务器端业务处理类
 *
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private RpcServerFactory providerFactory;

    private ThreadPoolExecutor serverHandlerPool;

    public NettyServerHandler(RpcServerFactory providerFactory, ThreadPoolExecutor serverHandlerPool) {
        this.providerFactory = providerFactory;
        this.serverHandlerPool = serverHandlerPool;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final RpcRequest request) throws Exception {
        System.out.println("---------服务端接收到消息:" + request);
        try {
            // do invoke
            serverHandlerPool.execute(new Runnable() {
                @Override
                public void run() {
                    // 调用服务
                    RpcResponse response = providerFactory.invokeService(request);
                    // 响应消息
                    ctx.writeAndFlush(response);
                }
            });
        } catch (Exception e) {
            // catch error
            RpcResponse response = new RpcResponse();
            response.setRequestId(request.getRequestId());
            response.setErrorMsg(e.toString());

            ctx.writeAndFlush(response);
        }
    }
}
