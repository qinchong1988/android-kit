package com.bmbstack.kit.util;

import com.bmbstack.kit.log.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/16
 *     desc  : 字符串相关工具类
 * </pre>
 */
public class StringUtils {

  private StringUtils() {
    throw new UnsupportedOperationException("u can't instantiate me...");
  }

  public static String format(String format, Object... args) {
    return String.format(Locale.US, format, args);
  }

  /**
   * 将字符串转换为gbk编码
   *
   * @return 转换编码后的字符串
   */
  public static String toGbk(String string) {
    String gbk = null;
    try {
      gbk = URLEncoder.encode(string, "GBK");
    } catch (UnsupportedEncodingException e) {
      Logger.d(e.getMessage());
    }
    return gbk;
  }

  /**
   * 将字符串从ut8编码转换为gb2312
   */
  public static String utf8Togb2312(String str) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      switch (c) {
        case '+':
          sb.append(' ');
          break;
        case '%':
          try {
            sb.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
          } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
          }
          i += 2;
          break;
        default:
          sb.append(c);
          break;
      }
    }
    // Undo conversion to external encoding
    String result = sb.toString();
    String res = null;
    try {
      byte[] inputBytes = result.getBytes("8859_1");
      res = new String(inputBytes, "UTF-8");
    } catch (Exception e) {
    }
    return res;
  }

  /**
   * 将字节数组转换为字符串
   */
  public static String bytes2String(byte[] value) {
    return (value == null) ? "" : new String(value);
  }

  /**
   * 将字节数组转换为十六进制字符串
   */
  public static String byte2hex(byte[] b) {
    String hs = "";
    String stmp = "";
    for (int n = 0; n < b.length; n++) {
      stmp = Integer.toHexString(b[n] & 0XFF);
      if (stmp.length() == 1) {
        hs = hs + "0" + stmp;
      } else {
        hs = hs + stmp;
      }
    }
    return hs.toUpperCase(Locale.US);
  }

  /**
   * 字符串转换为数字
   */
  public static int string2Int(String value) {
    try {
      if (isEmpty(value)) {
        return Integer.valueOf(value);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return 0;
  }

  /**
   * 字符串转换为浮点数
   */
  public static float string2Float(String value) {
    try {
      if (isEmpty(value)) {
        return Float.valueOf(value);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return 0;
  }

  /**
   * 判断字符串是否为空
   *
   * @return true 表示字符串为空 false 字符串不为空
   */
  public static boolean isEmpty(String str) {
    return (str == null) || ("".equals(str.trim())) || ("NULL".equalsIgnoreCase(str));
  }

  /**
   * all paramString are not null,"" or NULL
   */
  public static boolean isEmpty(String... paramString) {
    boolean isEmpty = false;
    for (String param : paramString) {
      isEmpty = isEmpty(param);
      if (isEmpty) {
        return true;
      }
    }
    return isEmpty;
  }

  /**
   * 判断字符产是否不为空
   */
  public static boolean isNotEmpty(String str) {
    return (!isEmpty(str));
  }

  public static boolean isEmptyArray(Object[] obj) {
    return isEmptyArray(obj, 1);
  }

  public static boolean isEmptyArray(Object[] array, int paramInt) {
    return (array == null) || (array.length < paramInt);
  }

  /**
   * 忽略大小写判断字符串是否相等
   */
  public static boolean isEqualIgnoreCase(String a, String b) {
    return ((null == a) ? isEmpty(b) : a.equalsIgnoreCase(b));
  }

  /**
   * 判断字符串是否只包含数字
   */
  public static boolean isNumeric(String str) {
    final String number = "0123456789";
    for (int i = 0; i < str.length(); i++) {
      if (number.indexOf(str.charAt(i)) == -1) {
        return false;
      }
    }
    return true;
  }

  public static String appendStrs(Object... strs) {
    StringBuilder sb = new StringBuilder();
    for (Object str : strs) {
      sb.append(str);
    }
    return sb.toString();
  }

  public static String splitJoint(String strSrc, String strTarget) {
    try {
      boolean isFirst = true;
      if (strTarget.equals("")) {
        return strSrc;
      }
      String checkStr = strSrc;
      checkStr = "," + checkStr + ",";
      String ids[] = strTarget.split(",");
      strTarget = "";
      for (String id : ids) {
        if (id.equals("")) {
          continue;
        }
        if (!checkStr.contains("," + id + ",")) {
          if (!isFirst) {
            strTarget += ",";
          }
          isFirst = false;
          strTarget += id;
        }
      }
      if (strSrc.equals("")) {
        return strTarget;
      }
      if (!strTarget.equals("")) {
        strSrc += ("," + strTarget);
      }
    } catch (Exception e) {
    }
    return strSrc;
  }

  /**
   * 格式化时间，将秒转换为 hh：mm:ss
   */
  public static String formatTime(int second) {
    if (second < 0) {
      second = 0;
    }
    int hh = second / 3600;
    int mm = (second % 3600) / 60;
    int ss = second % 60;

    if (0 != hh) {
      return String.format("%02d:%02d:%02d", hh, mm, ss);
    } else {
      return String.format("%02d:%02d", mm, ss);
    }
  }

  public static String formatSize(long value) {

    double k = (double) value / 1024;
    if (k < 1) {
      return String.format("%d B", value);
    }

    double m = k / 1024;
    if (m < 1) {
      return String.format("%.2f K", k);
    }

    double g = m / 1024;
    if (g < 1) {
      return String.format("%.2f M", m);
    }

    return String.format("%.2f G", g);
  }

  /**
   * 判断字符串是否为null或长度为0
   *
   * @param s 待校验字符串
   * @return {@code true}: 空<br> {@code false}: 不为空
   */
  public static boolean isEmpty(CharSequence s) {
    return s == null || s.length() == 0;
  }

  /**
   * 判断字符串是否为null或全为空格
   *
   * @param s 待校验字符串
   * @return {@code true}: null或全空格<br> {@code false}: 不为null且不全空格
   */
  public static boolean isSpace(String s) {
    return (s == null || s.trim().length() == 0);
  }

  /**
   * 判断两字符串是否相等
   *
   * @param a 待校验字符串a
   * @param b 待校验字符串b
   * @return {@code true}: 相等<br>{@code false}: 不相等
   */
  public static boolean equals(CharSequence a, CharSequence b) {
    if (a == b) return true;
    int length;
    if (a != null && b != null && (length = a.length()) == b.length()) {
      if (a instanceof String && b instanceof String) {
        return a.equals(b);
      } else {
        for (int i = 0; i < length; i++) {
          if (a.charAt(i) != b.charAt(i)) return false;
        }
        return true;
      }
    }
    return false;
  }

  /**
   * 判断两字符串忽略大小写是否相等
   *
   * @param a 待校验字符串a
   * @param b 待校验字符串b
   * @return {@code true}: 相等<br>{@code false}: 不相等
   */
  public static boolean equalsIgnoreCase(String a, String b) {
    return (a == b) || (b != null) && (a.length() == b.length()) && a.regionMatches(true, 0, b, 0,
        b.length());
  }

  /**
   * null转为长度为0的字符串
   *
   * @param s 待转字符串
   * @return s为null转为长度为0字符串，否则不改变
   */
  public static String null2Length0(String s) {
    return s == null ? "" : s;
  }

  /**
   * 返回字符串长度
   *
   * @param s 字符串
   * @return null返回0，其他返回自身长度
   */
  public static int length(CharSequence s) {
    return s == null ? 0 : s.length();
  }

  /**
   * 首字母大写
   *
   * @param s 待转字符串
   * @return 首字母大写字符串
   */
  public static String upperFirstLetter(String s) {
    if (isEmpty(s) || !Character.isLowerCase(s.charAt(0))) return s;
    return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
  }

  /**
   * 首字母小写
   *
   * @param s 待转字符串
   * @return 首字母小写字符串
   */
  public static String lowerFirstLetter(String s) {
    if (isEmpty(s) || !Character.isUpperCase(s.charAt(0))) return s;
    return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
  }

  /**
   * 反转字符串
   *
   * @param s 待反转字符串
   * @return 反转字符串
   */
  public static String reverse(String s) {
    int len = length(s);
    if (len <= 1) return s;
    int mid = len >> 1;
    char[] chars = s.toCharArray();
    char c;
    for (int i = 0; i < mid; ++i) {
      c = chars[i];
      chars[i] = chars[len - i - 1];
      chars[len - i - 1] = c;
    }
    return new String(chars);
  }

  /**
   * 转化为半角字符
   *
   * @param s 待转字符串
   * @return 半角字符串
   */
  public static String toDBC(String s) {
    if (isEmpty(s)) return s;
    char[] chars = s.toCharArray();
    for (int i = 0, len = chars.length; i < len; i++) {
      if (chars[i] == 12288) {
        chars[i] = ' ';
      } else if (65281 <= chars[i] && chars[i] <= 65374) {
        chars[i] = (char) (chars[i] - 65248);
      } else {
        chars[i] = chars[i];
      }
    }
    return new String(chars);
  }

  /**
   * 转化为全角字符
   *
   * @param s 待转字符串
   * @return 全角字符串
   */
  public static String toSBC(String s) {
    if (isEmpty(s)) return s;
    char[] chars = s.toCharArray();
    for (int i = 0, len = chars.length; i < len; i++) {
      if (chars[i] == ' ') {
        chars[i] = (char) 12288;
      } else if (33 <= chars[i] && chars[i] <= 126) {
        chars[i] = (char) (chars[i] + 65248);
      } else {
        chars[i] = chars[i];
      }
    }
    return new String(chars);
  }
}