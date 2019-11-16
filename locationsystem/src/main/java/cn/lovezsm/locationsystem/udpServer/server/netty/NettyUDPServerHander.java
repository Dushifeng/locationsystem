package cn.lovezsm.locationsystem.udpServer.server.netty;

import cn.lovezsm.locationsystem.base.bean.Message;
import cn.lovezsm.locationsystem.base.config.LocationConfig;
import cn.lovezsm.locationsystem.base.data.CollectionInfoCache;
import cn.lovezsm.locationsystem.base.service.DataDirectCenter;
import cn.lovezsm.locationsystem.base.util.DataParser;
import cn.lovezsm.locationsystem.base.util.SpringUtils;
import cn.lovezsm.locationsystem.base.data.DataCache;
import cn.lovezsm.locationsystem.collectionSystem.service.CollectionService;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NettyUDPServerHander extends SimpleChannelInboundHandler<DatagramPacket> {

    private DataCache cache;
    private LocationConfig config;
    private CollectionInfoCache collectionInfoCache;
    {
        cache = SpringUtils.getBean(DataCache.class);
        config = SpringUtils.getBean(LocationConfig.class);
        collectionInfoCache = SpringUtils.getBean(CollectionInfoCache.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        String rawData = ByteBufUtil.hexDump(datagramPacket.content());
        DataDirectCenter.putData(rawData);
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        // We don't close the channel because we can keep serving requests.
    }
}
