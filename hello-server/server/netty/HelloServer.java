import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月23日 23:02:51
 */
@Slf4j
public class HelloServer {
    public static void main(String[] args) {
        // 定义一对线程组
        // 主线程组-用于接受客户端的连接-身份跟Boss相同只管分发任务
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 从线程组-用于处理连接-Boss会分发任务给这个组-让这个worker线程组去完成任务
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //Netty服务器的创建 ServerBootstrap是一个启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    //设置主从线程组
                    .group(bossGroup, workerGroup)
                    //设置NIO双向通道
                    .channel(NioServerSocketChannel.class)
                    //子处理器用于处理workerGroup
                    .childHandler(new HelloServerInitializer());
            //启动server 绑定端口号为8080 启动方式为同步阻塞
            ChannelFuture future = serverBootstrap.bind(8080).sync();
            //监听关闭 同样设置为阻塞
            future.channel().closeFuture().sync();
        } catch(InterruptedException e) {
            HelloServer.log.error("Server Error :{}", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
