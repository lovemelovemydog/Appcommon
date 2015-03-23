package com.common.app.common.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        try {
            Pattern p = null;
            Matcher m = null;
            boolean b = false;
//            p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号11位
            p = Pattern.compile("^[1][0-9]{10}$"); // 验证手机号11位
            m = p.matcher(str);
            b = m.matches();
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 电话号码验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isHomePhone(String str) {
        try {
            Pattern p1 = null, p2 = null;
            Matcher m = null;
            boolean b = false;
            p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
            p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
            if (str.length() > 9) {
                m = p1.matcher(str);
                b = m.matches();
            } else {
                m = p2.matcher(str);
                b = m.matches();
            }
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 邮箱验证
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        try {
            String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
            Pattern p = Pattern.compile(str);
            Matcher m = p.matcher(email);
            return m.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 去掉Html标签影响居中的标签
     *
     * @param source
     * @return
     */
    public static String moveHtmlCenterTag(String source) {
        if (source == null)
            return null;
        source = replace(source, "<p>", "");
        source = replace(source, "</p>", "");
        source = replace(source, "</br>", "");
        source = replace(source, "<br>", "");
        return source;
    }

    /**
     * 字符替换
     *
     * @param source
     * @param regex
     * @param replacement
     * @return
     */
    public static String replace(String source, String regex, String replacement) {
        int index = -1;
        StringBuffer buffer = new StringBuffer();
        while ((index = source.indexOf(regex)) >= 0) {
            buffer.append(source.substring(0, index));
            buffer.append(replacement);
            source = source.substring(index + regex.length());
        }
        buffer.append(source);
        return buffer.toString();
    }

    /**
     * 工程默认编码
     *
     * @param obj
     * @return
     */
    public static String urlEncode(String obj) {
        return urlEncode(obj, "GBK");
    }

    /**
     * 工程默认解码
     *
     * @param obj
     * @return
     */
    public static String urlDecode(String obj) {
        return urlDecode(obj, "GBK");
    }

    public static String urlEncode(String obj, String charset) {
        String result = null;
        if (obj != null) {
            try {
                result = URLEncoder.encode(obj, charset);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                return result;
            }
        }
        return result;
    }

    public static String urlDecode(String obj, String charset) {
        String result = null;
        if (obj != null) {
            try {
                result = URLDecoder.decode(obj, charset);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                return result;
            }
        }
        return result;
    }


    /**
     * md5加密
     *
     * @param str
     * @return
     */
    public static String md5(String str) {
        StringBuffer buf = new StringBuffer("");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return buf.toString();
    }

    /**
     * 获取http请求的host
     *
     * @param url
     * @return
     */
    public static String getDomain(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost();
        } catch (URISyntaxException e) {
            return null;
        }
    }



    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
    */
    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }


    /**
     * 根据生日获取星座
     *
     * @param birthday
     * @return
     */
    public static String getConstellation(String birthday) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(birthday);
            int month = date.getMonth() + 1;
            int day = date.getDate();
            switch (month) {
                case 1:
                    if (day <= 19) {
                        str = "魔蝎座";
                    } else {
                        str = "水瓶座";
                    }
                    break;
                case 2:
                    if (day <= 18) {
                        str = "水瓶座";
                    } else {
                        str = "双鱼座";
                    }
                    break;
                case 3:
                    if (day <= 20) {
                        str = "双鱼座";
                    } else {
                        str = "白羊座";
                    }
                    break;
                case 4:
                    if (day <= 19) {
                        str = "白羊座";
                    } else {
                        str = "金牛座";
                    }
                    break;
                case 5:
                    if (day <= 20) {
                        str = "金牛座";
                    } else {
                        str = "双子座";
                    }
                    break;
                case 6:
                    if (day <= 21) {
                        str = "双子座";
                    } else {
                        str = "巨蟹座";
                    }
                    break;
                case 7:
                    if (day <= 22) {
                        str = "巨蟹座";
                    } else {
                        str = "狮子座";
                    }
                    break;
                case 8:
                    if (day <= 22) {
                        str = "狮子座";
                    } else {
                        str = "处女座";
                    }
                    break;
                case 9:
                    if (day <= 22) {
                        str = "处女座";
                    } else {
                        str = "天秤座";
                    }
                    break;
                case 10:
                    if (day <= 23) {
                        str = "天秤座";
                    } else {
                        str = "天蝎座";
                    }
                    break;
                case 11:
                    if (day <= 22) {
                        str = "天蝎座";
                    } else {
                        str = "射手座";
                    }
                    break;
                case 12:
                    if (day <= 21) {
                        str = "射手座";
                    } else {
                        str = "魔蝎座";
                    }
                    break;
                default:
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;

    }

    /**
     * 获取手机DeviceId
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String mac = info.getMacAddress();
            if (mac == null) {
                mac = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            if (mac == null) {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class, String.class);
                mac = (String) (get.invoke(c, "ro.serialno", "unknown"));
            }
            return mac;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取手机Mac地址
     * @param context
     * @return
     */
    public static String getMac(Context context) {
        String str = "";
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            str = info.getMacAddress();
        } catch (Exception e) {
            return "";
        }
        return str;
    }


    public static String formatPlayCount(long playcount) {

        double y = playcount / 100000000f;
        if (playcount / 100000000 > 0) {
            return round(y, 1) + "亿";
        }
        double w = playcount / 10000f;
        if (playcount / 10000 > 0) {
            return round(w, 1) + "万";
        }
//        double q = playcount/1000f;
//        if(playcount/1000>0){
//            return round(q,2)+"千";
//        }
        return String.valueOf(playcount);
    }

    public static double round(double v, int scale) {
        String temp = "####.";
        for (int i = 0; i < scale; i++) {
            temp += "0";
        }
        return Double.valueOf(new java.text.DecimalFormat(temp).format(v));
    }

    public static byte[] decode(final byte[] bytes) {
        return Base64.decode(bytes, 2);
    }

    public static String encode(final byte[] bytes) {
        return new String(Base64.encode(bytes, 2));
    }

    public static String formatDate(int hour) {
        String src = "";
        if (hour >= 23 || hour < 1) {
            src = "子时";
        } else if (hour >= 1 && hour < 3) {
            src = "丑时";
        } else if (hour >= 3 && hour < 5) {
            src = "寅时";
        } else if (hour >= 5 && hour < 7) {
            src = "卯时";
        } else if (hour >= 7 && hour < 9) {
            src = "辰时";
        } else if (hour >= 9 && hour < 11) {
            src = "巳时";
        } else if (hour >= 11 && hour < 13) {
            src = "午时";
        } else if (hour >= 13 && hour < 15) {
            src = "未时";
        } else if (hour >= 15 && hour < 17) {
            src = "申时";
        } else if (hour >= 17 && hour < 19) {
            src = "酉时";
        } else if (hour >= 19 && hour < 21) {
            src = "戌时";
        } else if (hour >= 21 && hour < 23) {
            src = "亥时";
        }
        return src;
    }

    public static String formatDate1(int position) {
        String src = "";
        switch (position) {
            case 0:
                src = "子时";
                break;
            case 1:
                src = "丑时";
                break;
            case 2:
                src = "寅时";
                break;
            case 3:
                src = "卯时";
                break;
            case 4:
                src = "辰时";
                break;
            case 5:
                src = "巳时";
                break;
            case 6:
                src = "午时";
                break;
            case 7:
                src = "未时";
                break;
            case 8:
                src = "申时";
                break;
            case 9:
                src = "酉时";
                break;
            case 10:
                src = "戌时";
                break;
            case 11:
                src = "亥时";
                break;
        }
        return src;
    }

    public static int formatDateIndex(int hour) {
        int position = -1;
        if (hour >= 23 && hour < 1) {
            position = 0;
        } else if (hour >= 1 && hour < 3) {
            position = 1;
        } else if (hour >= 3 && hour < 5) {
            position = 2;
        } else if (hour >= 5 && hour < 7) {
            position = 3;
        } else if (hour >= 7 && hour < 9) {
            position = 4;
        } else if (hour >= 9 && hour < 11) {
            position = 5;
        } else if (hour >= 11 && hour < 13) {
            position = 6;
        } else if (hour >= 13 && hour < 15) {
            position = 7;
        } else if (hour >= 15 && hour < 17) {
            position = 8;
        } else if (hour >= 17 && hour < 19) {
            position = 9;
        } else if (hour >= 19 && hour < 21) {
            position = 10;
        } else if (hour >= 21 && hour < 23) {
            position = 11;
        }
        return position;
    }

    public static void main(String[] args) {

//        System.out.println(formatPlayCount(1000));
        String sr = urlEncode("1", "utf-8");
        System.out.println(sr);
    }
}