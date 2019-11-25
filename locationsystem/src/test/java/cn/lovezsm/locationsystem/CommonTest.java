package cn.lovezsm.locationsystem;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;

public class CommonTest {

    @Test
    public void test3(){
        ThreadLocal<String> threadLocal = new ThreadLocal<>();

        for (int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (threadLocal.get()==null){
                        threadLocal.set(Thread.currentThread().getName()+"----");
                    }

                    String s = threadLocal.get();
                    System.out.println(s);
                }
            }).start();
        }
    }

    double fx = 1107.856547042833;
    double cx = 351.3922909741557;
    double fy = 1596.170441480193;
    double cy = 287.3736436464851;
    double r11 = 0.2627610639841095;
    double r12 = -0.964676711300373;
    double r13 = -0.01885380408924212;
    double r21 = 0.9636485860821554;
    double r22 = 0.2614015464955917;
    double r23 = 0.05523254503981899;
    double r31 = -0.04835313635950951;
    double r32 = 0.03268140395408121;
    double r33 = 0.9982954973552615;
    double t1 = -2.06295468933956;
    double t2 = -2.084249876669967;
    double t3 = -0.05308868557705982;
    @Test
    public void test2(){
        double w1 = fx*r12/(fx*r12*fy*r21-fx*r11*fy*r22);
        double w2 = -(fy*r22)/(fx*r12*fy*r21-fx*r11*fy*r22);
        double w3 = fx*r11/(fx*r11*fy*r22-fx*r12*fy*r21);
        double w4 = -(fy*r21)/(fx*r11*fy*r22-fx*r12*fy*r21);

        double b1 = (fy*r22*fx*t1-fx*r12*fy*t2+cx*fy*r22-cy*fx*r12)/(fx*r12*fy*r21-fx*r11*fy*r22);
        double b2 = (fy*r21*fx*t1-fx*r11*fy*t2+cx*fy*r21-cy*fx*r11)/(fy*r22*fx*r11-fx*r21*fy*r12);


        System.out.println("w1"+w1);
        System.out.println("w2"+w2);
        System.out.println("w3"+w3);
        System.out.println("w4"+w4);

        System.out.println("b1"+b1);
        System.out.println("b2"+b2);

    }
    @Test
    public void test() throws IOException {
        File dic = new File("H:\\CameraCalibration\\CameraCalibration\\3");
        File[] files = dic.listFiles();
        int i =1;

        for (File file:files){
            FileImageInputStream fiis=new FileImageInputStream(file);
            File out = new File("H:\\CameraCalibration\\CameraCalibration\\3\\"+i+".bmp");
            FileImageOutputStream fios=new FileImageOutputStream(out);
            i++;
            ImageReader jpegReader = null;
            Iterator<ImageReader> it1 = ImageIO.getImageReadersByFormatName("jpeg");
            if(it1.hasNext())
            {
                jpegReader = it1.next();
            }
            jpegReader.setInput(fiis);

            ImageWriter bmpWriter = null;
            Iterator<ImageWriter> it2 = ImageIO.getImageWritersByFormatName("bmp");
            if(it2.hasNext())
            {
                bmpWriter = it2.next();
            }
            bmpWriter.setOutput(fios);
            BufferedImage br = jpegReader.read(0);
            bmpWriter.write(br);
            fiis.close();
            fios.close();
            System.out.println("Jpeg到bmp图片转换完成.");
        }


    }
}
