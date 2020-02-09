package cn.lovezsm.locationsystem.base.starter;


import cn.lovezsm.locationsystem.base.config.LocationConfig;
import cn.lovezsm.locationsystem.base.mapper.MessageMapper;
import cn.lovezsm.locationsystem.base.util.ClassUtils;
import cn.lovezsm.locationsystem.base.util.SpringUtils;
import cn.lovezsm.locationsystem.locationSystem.algorithm.LocationAlgorithm;
import cn.lovezsm.locationsystem.udpServer.server.netty.NettyUDPServer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.SneakyThrows;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.*;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
//@Component
public class ApplicationStarter implements CommandLineRunner {

    @Autowired
    ApplicationContext applicationContext;
    private static int port = 16661;

    @Override
    public void run(String... args) throws Exception {

        NettyUDPServer server = NettyUDPServer.getINSTANCE();
        server.start();

        new Thread(() -> {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(
                        new File("C:\\Users\\61616\\Desktop\\实验数据\\one-1\\20.txt")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            List<String> data = new ArrayList<String>();
            String s = null;
            while (true){
                try {
                    if (!((s=reader.readLine())!=null)) {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                data.add(s);
            }
            Long ft = Long.parseLong(data.get(0).split(":")[0]);
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioDatagramChannel.class)
                        .handler(new CLientHandler());

                Channel ch = b.bind(0).sync().channel();
                System.out.println("start....");
                for (String line:data){
                    Long t = Long.parseLong(line.split(":")[0]);
                    String raw = line.split(":")[1];
                    ch.writeAndFlush(new DatagramPacket(
                            Unpooled.copiedBuffer(ByteBufUtil.decodeHexDump(raw)),
                            new InetSocketAddress("127.0.0.1", port))).sync();

                    Thread.sleep(t-ft);
                    ft = t;

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("end.....");
        }).start();


    }

    private static class CLientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {

        }
    }
}
