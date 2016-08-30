//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.nd.hano.web.util;

import android.annotation.SuppressLint;
import android.util.Log;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressLint({"DefaultLocale"})
public final class StringUtils {
    public static final String EMPTY_STRING = "";
    private static final String TAG = "StringUtils";

    public StringUtils() {
    }

    public static int toInt(String text) {
        try {
            if(text.indexOf(".") > 0) {
                text = text.substring(0, text.indexOf("."));
            }

            return Integer.valueOf(text).intValue();
        } catch (Exception var2) {
            Log.w("StringUtils", "" + var2.getMessage());
            return 0;
        }
    }

    public static long toLong(String text) {
        try {
            if(text.indexOf(".") > 0) {
                text = text.substring(0, text.indexOf("."));
            }

            return Long.valueOf(text.toString()).longValue();
        } catch (Exception var2) {
            Log.w("StringUtils", "" + var2.getMessage());
            return 0L;
        }
    }

    public static boolean isEmpty(String text) {
        return text == null?true:text.isEmpty();
    }

    public static boolean isWhitespace(String text) {
        if(isEmpty(text)) {
            return true;
        } else {
            for(int i = 0; i < text.length(); ++i) {
                if(!Character.isWhitespace(text.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean equals(String str1, String str2) {
        return null == str1?null == str2:str1.equals(str2);
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return null == str1?null == str2:str1.equalsIgnoreCase(str2);
    }

    public static boolean isDigitsOnly(String text) {
        if(null == text) {
            return false;
        } else {
            int length = text.length();

            for(int i = 0; i < length; ++i) {
                if(!Character.isDigit(text.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isDigitsWithSpace(String text) {
        if(null == text) {
            return false;
        } else {
            int length = text.length();

            for(int i = 0; i < length; ++i) {
                if(!Character.isDigit(text.charAt(i)) && text.charAt(i) != 32) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isAlpha(String text) {
        if(null == text) {
            return false;
        } else {
            int length = text.length();

            for(int i = 0; i < length; ++i) {
                if(!Character.isLetter(text.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isAlphaSpaceWithSpace(String text) {
        if(null == text) {
            return false;
        } else {
            int length = text.length();

            for(int i = 0; i < length; ++i) {
                if(!Character.isLetter(text.charAt(i)) && text.charAt(i) != 32) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean contains(String text, char containsChar) {
        return null != text && text.length() != 0?text.indexOf(containsChar) >= 0:false;
    }

    public static boolean contains(String text, String containsStr) {
        return null != text && null != containsStr?text.indexOf(containsStr) >= 0:false;
    }

    public static String defaultIfNull(String text) {
        return null == text?"":text;
    }

    public static String defaultIfNull(String text, String defaultStr) {
        return null == text?defaultStr:text;
    }

    public static String defaultIfEmpty(String text, String defaultStr) {
        return null != text && text.length() != 0?text:defaultStr;
    }

    public static String toUpperCase(String text) {
        return null == text?null:text.toUpperCase();
    }

    public static String toLowerCase(String text) {
        return null == text?null:text.toLowerCase();
    }

    public static String reverse(String text) {
        return null != text && text.length() != 0?(new StringBuffer(text)).reverse().toString():text;
    }

    public static String getMD5(byte[] source) {
        String s = null;
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            e.update(source);
            byte[] tmp = e.digest();
            char[] text = new char[32];
            int k = 0;

            for(int i = 0; i < 16; ++i) {
                byte byte0 = tmp[i];
                text[k++] = hexDigits[byte0 >>> 4 & 15];
                text[k++] = hexDigits[byte0 & 15];
            }

            s = new String(text);
        } catch (Exception var9) {
            Log.w("StringUtils", "" + var9.getMessage());
        }

        return s;
    }

    public static String getFileName(String path) {
        int start = path.lastIndexOf(47) + 1;
        int end = path.lastIndexOf(46);
        return end == -1?path.substring(start):path.substring(start, end);
    }

    public static String convertTimeToHms(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second)});
    }

    public static String convertTimeToMs(int time) {
        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(minute), Integer.valueOf(second)});
    }

    public static int parseStringToInt(String data, int def) {
        return isEmpty(data)?def:(isDigitsOnly(data)?Integer.valueOf(data).intValue():def);
    }

    public static long parseStringToLong(String data, long def) {
        return isEmpty(data)?def:(isDigitsOnly(data)?Long.valueOf(data).longValue():def);
    }

    public static String upperCaseFirstLetter(String value) {
        return isEmpty(value)?value:(Character.isUpperCase(value.charAt(0))?value:(value.length() > 1?Character.toUpperCase(value.charAt(0)) + value.substring(1):value.toUpperCase()));
    }

    public static String lowerCaseFirstLetter(String value) {
        return isEmpty(value)?value:(Character.isLowerCase(value.charAt(0))?value:(value.length() > 1?Character.toLowerCase(value.charAt(0)) + value.substring(1):value.toLowerCase()));
    }

    public static String sqliteEscape(String text) {
        String ret;
        if(text == null) {
            ret = "";
        } else {
            ret = text.replaceAll("/", "//");
            ret = ret.replaceAll("\'", "\'\'");
            ret = ret.replaceAll("\\[", "/[");
            ret = ret.replaceAll("\\]", "/]");
            ret = ret.replaceAll("%", "/%");
            ret = ret.replaceAll("&", "/&");
            ret = ret.replaceAll("_", "/_");
            ret = ret.replaceAll("\\(", "/(");
            ret = ret.replaceAll("\\)", "/)");
        }

        return ret;
    }

    public static String escape(String text) {
        String ret;
        if(text == null) {
            ret = "";
        } else {
            ret = text.replace("\\", "\\\\");
            ret = ret.replace("\'", "\\\'");
            ret = ret.replace("\"", "\\\"");
        }

        return ret;
    }

    public static String guessEncoding(String contents) {
        return contents == null?"":(isUTF8(contents.getBytes())?"UTF-8":"");
    }

    private static boolean isUTF8(byte[] rawtext) {
        boolean score = false;
        boolean rawtextlen = false;
        int goodbytes = 0;
        int asciibytes = 0;
        int var7 = rawtext.length;

        for(int i = 0; i < var7; ++i) {
            if((rawtext[i] & 127) == rawtext[i]) {
                ++asciibytes;
            } else if(-64 <= rawtext[i] && rawtext[i] <= -33 && i + 1 < var7 && -128 <= rawtext[i + 1] && rawtext[i + 1] <= -65) {
                goodbytes += 2;
                ++i;
            } else if(-32 <= rawtext[i] && rawtext[i] <= -17 && i + 2 < var7 && -128 <= rawtext[i + 1] && rawtext[i + 1] <= -65 && -128 <= rawtext[i + 2] && rawtext[i + 2] <= -65) {
                goodbytes += 3;
                i += 2;
            }
        }

        if(asciibytes == var7) {
            return true;
        } else {
            int var6 = 100 * goodbytes / (var7 - asciibytes);
            if(var6 > 98) {
                return true;
            } else if(var6 > 95 && goodbytes > 30) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static String nullToString(String text) {
        return text != null && !text.trim().equals("")?text:"";
    }

    public static int stringToInt(String text) {
        byte n = 0;

        try {
            String e = nullToString(text);
            if(e.equals("")) {
                return n;
            } else {
                int n1 = Integer.parseInt(text);
                return n1;
            }
        } catch (Exception var3) {
            return n;
        }
    }

    public static long stringToLong(String text) {
        long n = 0L;

        try {
            String e = nullToString(text);
            if(e.equals("")) {
                return n;
            } else {
                n = Long.parseLong(text);
                return n;
            }
        } catch (Exception var4) {
            return n;
        }
    }

    public static String subString(String text, int n) {
        String tempStr = "";
        if(text != null && !text.equals("")) {
            int len = text.length();
            if(len > n) {
                tempStr = text.substring(0, n);
            } else {
                tempStr = text;
            }

            return tempStr;
        } else {
            return tempStr;
        }
    }

    public static String nullToString(Object obj) {
        if(obj != null && !obj.equals("null")) {
            String string = String.valueOf(obj);
            string = string.trim();
            return string;
        } else {
            return "";
        }
    }

    public static Map<String, String> decodeURLParam(String param) {
        HashMap map = new HashMap();

        int index1;
        for(boolean index = false; (index1 = param.lastIndexOf("&")) != -1; param = param.substring(0, index1)) {
            String params = param.substring(index1 + 1);
            String[] params1 = params.split("=");
            map.put(params1[0], params1[1]);
        }

        if(!isEmpty(param)) {
            index1 = param.indexOf("?");
            if(-1 != index1) {
                param = param.substring(index1 + 1);
            }

            String[] params2 = param.split("=");
            map.put(params2[0], params2[1]);
        }

        return map;
    }

    public static String encodeURLParam(Map<Object, Object> param) {
        StringBuffer sbBuffer = new StringBuffer();
        Iterator iterator = param.keySet().iterator();

        Object key;
        String value;
        for(boolean first = true; iterator.hasNext(); sbBuffer.append(key).append("=").append(value)) {
            key = iterator.next();
            value = param.get(key).toString();
            if(first) {
                first = false;
            } else {
                sbBuffer.append("&");
            }
        }

        return sbBuffer.toString();
    }

    public static String addSlashes(String string) {
        string = string.replace("\\", "\\\\");
        string = string.replace("\"", "\\\"");
        string = string.replace("\'", "\\\'");
        return string;
    }
}
