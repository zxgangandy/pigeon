package io.andy.pigeon.rpc.core.client;

import io.andy.pigeon.rpc.core.domain.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rpc netty client handler
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);


    private RpcInvokerFactory invokerFactory;

    public NettyClientHandler(final RpcInvokerFactory invokerFactory) {
        this.invokerFactory = invokerFactory;
    }

    // 接收Netty服务端的响应结果进行处理
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        System.out.println("---------客户端接收到消息:" + response);
        // notify response
        invokerFactory.notifyInvokerFuture(response.getRequestId(), response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(">>>>>>>>>>> netty client caught exception", cause);
        ctx.close();
    }


}
