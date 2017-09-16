package me.patamon.utils;

import com.google.common.base.Strings;

/**
 * Desc: String util
 * <p>
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/9/16
 */
public class StringUtil {

    /**
     * 部分特殊符号, 可自行定义
     */
    private static final String SPECIAL_CHARS = "　 \r\n\t,，。·.．;；:：、！@$%*^`~=+&'\"|_-\\/{}【】〈〉<>[]「」“”（）()";


    /**
     * 去除特殊字符
     * @param source  源字符串
     */
    public static String removeSpecialChars(String source) {
        return removeSpecialChars(source, SPECIAL_CHARS);
    }

    /**
     * 去除特殊字符
     * @param source        源字符串
     * @param specialChars  指定特殊字符, 默认是 {@link StringUtil#SPECIAL_CHARS}
     * @return
     */
    public static String removeSpecialChars(String source, String specialChars) {
        if (Strings.isNullOrEmpty(source) || Strings.isNullOrEmpty(specialChars)) {
            return source;
        }
        StringBuilder sb = new StringBuilder(source.length());
        boolean remove = false;
        for (int i = 0; i < source.length(); i++) {
            char ch = source.charAt(i);
            if (specialChars.indexOf(ch) >= 0) {
                remove = true;
                continue;
            }
            sb.append(ch);
        }
        if (remove)
            return sb.toString();
        else
            return source;
    }

    /**
     * <p>Converts a String to upper case as per {@link String#toUpperCase()}.</p>
     */
    public static String upperCase(final String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    /**
     * <p>Converts a String to lower case as per {@link String#toLowerCase()}.</p>
     */
    public static String lowerCase(final String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }
}
