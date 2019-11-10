package cn.lovezsm.locationsystem.base.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    /**
     * 压缩成ZIP 方法1
     *
     * @param srcDir 压缩文件夹路径
     * @param out    压缩文件输出流
     */
    public static void toZip(String srcDir, OutputStream out) {

        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            File file = new File(srcDir);
            compress(file, zos, file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 压缩成ZIP 方法2
     *
     * @param files 需要压缩的文件列表
     * @param out   压缩文件输出流
     */
    public static void toZip(List<File> files, OutputStream out) {

        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            for (File srcFile : files) {
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                FileInputStream in = new FileInputStream(srcFile);
                copy(in, zos);
                zos.closeEntry();
                in.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 递归压缩方法
     *
     * @param file 源文件
     * @param zos  zip输出流
     * @param name 压缩后的名称
     * @throws Exception
     */
    private static void compress(File file, ZipOutputStream zos, String name) throws Exception {

        if (file.isFile()) {
            zos.putNextEntry(new ZipEntry(name));
            FileInputStream in = new FileInputStream(file);
            copy(in, zos);
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = file.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                zos.putNextEntry(new ZipEntry(name + "/"));
                zos.closeEntry();
            } else {
                for (File target : listFiles) {
                    compress(target, zos, name + "/" + target.getName());
                }
            }
        }
    }

    private static boolean copy(InputStream in, OutputStream out, int size) {

        BufferedInputStream bis = new BufferedInputStream(in);
        byte[] buf = new byte[size];

        try {
            while (bis.read(buf) != -1) {
                out.write(buf);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private static int copy(InputStream in, OutputStream out) {

        try {
            return IOUtils.copy(in,out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }




    public static int getTotalLines(File file) throws IOException {
        int lineNum = 0;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.readLine()!=null){
            lineNum++;
        }
        reader.close();
        return lineNum;
    }
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }
    /*
     * Java文件操作 获取不带扩展名的文件名
     *
     *  Created on: 2011-8-2
     *      Author: blueeagle
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }
}
