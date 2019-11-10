package cn.lovezsm.locationsystem.base.util;

public class BaseUtils {
    public static String transforMac(String mac){
        StringBuilder sb = new StringBuilder();
        mac = mac.toLowerCase();
        char[] chars = mac.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i]>='0'&&chars[i]<='9'){
                sb.append(chars[i]);
            }else if (chars[i]>='a'&&chars[i]<='z'){
                sb.append(chars[i]);
            }else if (chars[i]==':'||chars[i]=='-'){
                continue;
            }else {
                return null;
            }
        }
        if (sb.length()!=12){
            return null;
        }
        return sb.toString();
    }


}
