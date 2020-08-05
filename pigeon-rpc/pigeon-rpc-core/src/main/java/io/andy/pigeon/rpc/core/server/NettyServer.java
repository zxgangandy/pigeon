package io.andy.pigeon.rpc.core.server;

import io.andy.pigeon.rpc.core.domain.RpcRequest;
import io.andy.pigeon.rpc.core.domain.RpcResponse;
import io.andy.pigeon.rpc.core.serialize.NettyDecoder;
import io.andy.pigeon.rpc.core.serialize.NettyEncoder;
import io.andy.pigeon.rpc.core.util.ThreadPoolUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Netty Server
 *
 */
public class NettyServer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Thread thread;

    public void start(final RpcServerFactory providerFactory) throws Exception {

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                // param
                final ThreadPoolExecutor serverHandlerPool = ThreadPoolUtil.makeServerThreadPool(NettyServer.class.getSimpleName());
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();

                try {
                    // start server
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel channel) throws Exception {
                                    channel.pipeline()
                                            .addLast(new NettyDecoder(RpcRequest.class, providerFactory.getSerializer()))
                                            .addLast(new NettyEncoder(RpcResponse.class, providerFactory.getSerializer()))
                                            .addLast(new NettyServerHandler(providerFactory, serverHandlerPool));
                                }
                            })
                            .childOption(ChannelOption.TCP_NODELAY, true)// 有数据立即发送
                            .childOption(ChannelOption.SO_KEEPALIVE, true);

                    // bind
                    ChannelFuture future = bootstrap.bind(providerFactory.getPort()).sync();

                    logger.info(">>>>>>>>>>> Netty服务器启动成功, nettype = {}, port = {}", NettyServer.class.getName(), providerFactory.getPort());
//                    onStarted();

                    // wait util stop
                    future.channel().closeFuture().sync();

                } catch (Exception e) {
                    if (e instanceof InterruptedException) {
                        logger.info(">>>>>>>>>>> rpc remoting server stop.");
                    } else {
                        logger.error(">>>>>>>>>>> rpc remoting server error.", e);
                    }
                } finally {

                    // stop
                    try {
                        serverHandlerPool.shutdown();    // shutdownNow
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    try {
                        workerGroup.shutdownGracefully();
                        bossGroup.shutdownGracefully();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }

                }
            }

//            private void onStarted() {
//
//            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
