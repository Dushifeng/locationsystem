package cn.lovezsm.locationsystem.udpServer.server.netty;

import cn.lovezsm.locationsystem.base.bean.FingerPrint;
import cn.lovezsm.locationsystem.base.bean.FingerPrintBuilder;
import cn.lovezsm.locationsystem.base.bean.GridMap;
import cn.lovezsm.locationsystem.base.config.APConfig;
import cn.lovezsm.locationsystem.base.config.LocationConfig;
import cn.lovezsm.locationsystem.base.data.CollectionInfoCache;
import cn.lovezsm.locationsystem.base.service.DataDirectCenter;

import cn.lovezsm.locationsystem.base.util.SpringUtils;
import cn.lovezsm.locationsystem.base.data.DataCache;

import cn.lovezsm.locationsystem.locationSystem.service.LocationSystemService;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.io.File;


public class NettyUDPServerHander extends SimpleChannelInboundHandler<DatagramPacket> {

//    private DataCache cache;
//    private LocationConfig config;
//
//    private LocationSystemService locationSystemService;
//    private CollectionInfoCache collectionInfoCache;
//    {
//
//        cache = SpringUtils.getBean(DataCache.class);
//        config = SpringUtils.getBean(LocationConfig.class);
//        collectionInfoCache = SpringUtils.getBean(CollectionInfoCache.class);
//
//        APConfig apConfig = SpringUtils.getBean(APConfig.class);
//        GridMap gridMap = SpringUtils.getBean(GridMap.class);
//        LocationConfig locationConfig = SpringUtils.getBean(LocationConfig.class);
//        FingerPrint fingerPrint = SpringUtils.getBean(FingerPrint.class);
//        try {
//
//            FingerPrint f1 = FingerPrintBuilder.build("",
//                    new File("Fingerprint_avg_test.dat"),
//                    new File("Fingerprint_std_test.dat"));
//            fingerPrint.setAvg(f1.getAvg());
//            fingerPrint.setStd(f1.getStd());
//            fingerPrint.setName(f1.getName());
//
//            APConfig apConfig1 = APConfig.buildByFile(new File("ap.txt"));
//            apConfig.setApList(apConfig1.getApList());
//            GridMap gridMap1 = GridMap.buildByFile(new File("coordinate.txt"),"");
//            gridMap.setX(gridMap1.getX());
//            gridMap.setY(gridMap1.getY());
//            gridMap.setHeight(gridMap1.getHeight());
//            gridMap.setWidth(gridMap1.getWidth());
//            gridMap.setGridNum(gridMap1.getGridNum());
//            gridMap.setName(gridMap1.getName());
//
//            locationConfig.setLocationAlgorithmName("全排列定位算法");
//            locationConfig.setResultExpireQueueTime(1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        locationSystemService = SpringUtils.getBean(LocationSystemService.class);
//        locationSystemService.startLocation();
//
//        System.out.println(cache);
//        System.out.println(config);
//        System.out.println(collectionInfoCache);
//    }

    public NettyUDPServerHander() {
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
    }
}
