package cn.lovezsm.locationsystem.udpServer.server.netty;


import cn.lovezsm.locationsystem.udpServer.server.UDPServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class NettyUDPServer extends UDPServer {
    static ThreadPoolExecutor pool;
    public volatile int port = 16661;
    private volatile static NettyUDPServer INSTANCE;
    private volatile EventLoopGroup group;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private NettyUDPServer() {

    }

    @SuppressWarnings("AlibabaThreadPoolCreation")
    public static NettyUDPServer getINSTANCE(){
        if (INSTANCE == null){
            synchronized (NettyUDPServer.class){
                if (INSTANCE == null){
                    INSTANCE = new NettyUDPServer();
                    pool = new ThreadPoolExecutor(3,Integer.MAX_VALUE,0,TimeUnit.MILLISECONDS,new SynchronousQueue<Runnable>());
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        if (isOpen()==true){
            logger.warn("UDP服务器的状态是正在运行，切勿重复启动。");
            return;
        }
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        NettyUDPServerHander hander = new NettyUDPServerHander();
        b.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(hander);
        try {

            Channel channel = b.bind(port).sync().channel();
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        channel.closeFuture().await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            group.shutdownGracefully().sync();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setOpen(true);
        logger.info("UDP服务器已启动,端口号为:"+port);
    }
    @Override
    public void stop(){
        if (isOpen() == false){
            logger.warn("UDP服务器未开启");
            return;
        }
        try {
            group.shutdownGracefully().sync().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setOpen(false);
        logger.info("UDP服务器已关闭");
    }
}
