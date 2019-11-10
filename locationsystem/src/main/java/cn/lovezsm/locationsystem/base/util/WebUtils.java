package cn.lovezsm.locationsystem.base.util;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

public class WebUtils {

    public static String getCookie(HttpServletRequest request,String cookieName){
        Cookie[] cookies = request.getCookies();
        if (cookies!=null){
            for (Cookie cookie:cookies){
                if (cookie.getName().equals(cookieName)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void writeCookie(HttpServletResponse response,String cookieName,String value){
        Cookie cookie = new Cookie(cookieName,value);
        cookie.setPath("/");
        cookie.setMaxAge(24*60*60);
        response.addCookie(cookie);
    }

    public static File transMultipartFile(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        final File ans = File.createTempFile(UUIDGenerator.getUUID(),prefix);
        multipartFile.transferTo(ans);
        return ans;
    }

    public static void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static class UUIDGenerator {
        /**
      * 获取32位UUID字符串
      * @return
      */
        public static String getUUID() {
            return UUID.randomUUID().toString().replaceAll("-", "");
        }

        /**
      * 获取32位UUID大写字符串
      * @return
      */
        public static String getUpperCaseUUID() {
            return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        }


    }

}
