package me.patamon.utils;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.List;

/**
 * Desc: pinyin4j util
 *
 * Mail: chk@terminus.io
 * Created by IceMimosa
 * Date: 2017/8/27
 */
@Slf4j
public class PinyinUtil {

    /**
     * 获取字符的首字母, 拼音可能存在多个
     */
    public static List<Character> getFirstCode(Character ch) {
        List<Character> firstCodes = Lists.newArrayList();
        String[] results = getPinyin(ch);
        for (int i = 0; i < results.length; i++) {
            String result = results[i];
            if (Strings.isNullOrEmpty(result)) {
                continue;
            }
            Character firstCode = result.charAt(0);
            firstCodes.add(firstCode);
        }
        return firstCodes;
    }

    /**
     * 获取该字符的拼音, 拼音可能存在多个
     */
    public static List<String> getCode(Character ch) {
        List<String> codes = Lists.newArrayList();
        String[] results = getPinyin(ch);
        for (int i = 0; i < results.length; i++) {
            String result = results[i];
            if (Strings.isNullOrEmpty(result)) {
                continue;
            }
            codes.add(result);
        }
        return codes;
    }

    /**
     * 获取汉语拼音, null则返回空数组
     */
    public static String[] getPinyin(Character ch) {
        String[] empty = new String[0];
        if (ch == null) {
            return empty;
        }
        // 类型
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);     // 小写
        format.setVCharType(HanyuPinyinVCharType.WITH_V);      // ü 变成 v
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  // 没有声调
        try {
            String[] results = PinyinHelper.toHanyuPinyinStringArray(ch, format);
            if (results != null) {
                return results;
            }
        } catch (Exception e) {
            log.error("Chinese to Han Yu Pin Yin error, input is {}, cause by {}", ch, Throwables.getStackTraceAsString(e));
        }
        return empty;
    }
}
