package io.andy.pigeon.rpc.core.client;


import io.andy.pigeon.rpc.core.domain.RpcRequest;
import io.andy.pigeon.rpc.core.domain.RpcResponse;
import io.andy.pigeon.rpc.core.serialize.NettyDecoder;
import io.andy.pigeon.rpc.core.serialize.NettyEncoder;
import io.andy.pigeon.rpc.core.serialize.Serializer;
import io.andy.pigeon.rpc.core.util.IpUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * netty client
 */
public class NettyClient {

    private Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private EventLoopGroup group;
    private Channel channel;

    public static final NettyClient nettyClient = new NettyClient();

    private NettyClient() {

    }

    public static NettyClient getInstance() {
        return nettyClient;
    }

    // 创建 Netty 客户端
    public void init(String address, final Serializer serializer, final RpcInvokerFactory invokerFactory) throws Exception {

        Object[] array = IpUtil.parseIpPort(address);
        String host = (String) array[0];
        int port = (int) array[1];

        this.group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new NettyEncoder(RpcRequest.class, serializer))
                                .addLast(new NettyDecoder(RpcResponse.class, serializer))
                                .addLast(new NettyClientHandler(invokerFactory));
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        this.channel = bootstrap.connect(host, port).sync().channel();// 和Netty服务端建立连接

        // valid
        if (!isValidate()) {
            close();
            return;
        }

        logger.debug(">>>>>>>>>>> 连接Netty服务端成功 at host:{}, port:{}", host, port);
    }


    public boolean isValidate() {
        if (this.channel != null) {
            return this.channel.isActive();
        }
        return false;
    }

    public void close() {
        if (this.channel != null && this.channel.isActive()) {
            this.channel.close();        // if this.channel.isOpen()
        }
        if (this.group != null && !this.group.isShutdown()) {
            this.group.shutdownGracefully();
        }
        logger.debug(">>>>>>>>>>> netty client close.");
    }


    public void send(RpcRequest request) throws Exception {
        this.channel.writeAndFlush(request).sync();
    }

}
