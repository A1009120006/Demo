package org.mys.example.demo.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.springframework.stereotype.Component;

/**
 * 拼音工具类
 */
@Component
public class PinyinUtil {

    /**
     * 获取中文串转汉语全拼。（支持多音字，英文字符和特殊字符都丢弃。）
     *
     * @param str 字符串，为null，返回“”
     * @return 汉语全拼
     */
    public static String getFullSpell(String str) {
        String fullPinyin = "";
        if (str == null) {
            return fullPinyin;
        }

        HanyuPinyinOutputFormat pinyinOutputFormat = new HanyuPinyinOutputFormat();
        /**
         * 定义汉语拼音字符串的输出大小写：
         * LOWERCASE(默认) - 表示汉语拼音作为大写字母输出
         * UPPERCASE - 表示汉语拼音以小写字母输出
         */
        pinyinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        /**
         *定义汉语拼音声调的输出格式：汉语有四个声调和一个“无音”音。它们被称为Píng(平坦)，Shǎng(上升)，Qù(高下降)，Rù(下降)和Qing(无音调)。
         * WITH_TONE_NUMBER(默认) - 表示汉语拼音以声调数字输出。比如：你说呢 - ni3 shuo1ne2
         * WITHOUT_TONE - 该选项表示不输出音号或音标记的汉语拼音 比如：你说呢 - ni shuone
         * WITH_TONE_MARK - 表示输出带有音调标记的汉语拼音 比如：你说呢 - nĭshuō
         */
        pinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        try {
            /**
             * 获取一个字符串，其中所有的中文字符都被相应的主(第一)汉语拼音表示所取代。
             * 参数:
             *  str - 中文串
             *  outputFormat - 描述返回的汉语拼音字符串的期望格式
             *  separate - 每个字的拼音使用什么分割符串显示。注意：分隔符不会出现在非中文字符之后。一般不使用任何分隔符（""）时，大家都是紧挨着，看不出来的。
             *  retain - 是否保留不能转换为拼音的字符。true保留
             */
            fullPinyin = PinyinHelper.toHanYuPinyinString(str, pinyinOutputFormat, " ", false);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return fullPinyin;
    }

    /**
     * 获取中文串转汉语全拼。（支持多音字，英文字符和特殊字符都保留。）
     *
     * @param str 字符串，为null，返回“”
     * @return 汉语全拼
     */
    public static String getFullSpellAndStr(String str) {
        String fullPinyin = "";
        if (str == null) {
            return fullPinyin;
        }

        HanyuPinyinOutputFormat pinyinOutputFormat = new HanyuPinyinOutputFormat();
        pinyinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        pinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        try {
            // 保留不能转换为拼音的字符
            fullPinyin = PinyinHelper.toHanYuPinyinString(str, pinyinOutputFormat, " ", true);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return fullPinyin;
    }

    /**
     * 获取中文串转汉语全拼首字母。（支持多音字。英文字符和特殊字符都丢弃。）
     *
     * @param str 字符串，为null，返回“”
     * @return 汉语全拼首字母
     */
    public static String getFirstSpell(String str) {
        StringBuilder result = new StringBuilder();
        String fullSpell = getFullSpell(str);
        String[] fullSpellArray = fullSpell.split(" ");
        for (int i = 0; i < fullSpellArray.length; i++) {
            try {
                String spell = fullSpellArray[i];
                result.append(spell.charAt(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    /**
     * 获取中文。（除中文之外，其他字符丢弃。）
     * @param str
     */
    public static String getChinese(String str) {
        if (str == null) {
            return "";
        }
        String regex = "[^\u4e00-\u9fa5]"; // 匹配非中文字符
        return str.replaceAll(regex, ""); // 替换非中文字符为空字符串
    }


    public static void main(String[] args) {
        System.out.println(getFullSpell("你说 重庆话"));
        System.out.println(getFullSpell("AbCd你说 重庆话"));
        System.out.println(getFullSpell("你说 重庆话(){}【】.@张三")); // NI SHUO CHONG QING HUA ZHANGSAN  注意：张三没有按分隔符分隔。因为它在非中文之后
        System.out.println(getFullSpell("AbCd你说 重庆话()zhangsan"));

        System.out.println("===============");
        System.out.println(getFullSpellAndStr("你说 重庆话"));
        System.out.println(getFullSpellAndStr("AbCd你说 重庆话"));
        System.out.println(getFullSpellAndStr("你说 重庆话(){}【】.@张三")); // NI SHUO  CHONG QING HUA (){}【】.@ZHANGSAN
        System.out.println(getFullSpellAndStr("AbCd你说 重庆话()zhangsan"));

        System.out.println("===============");
        System.out.println(getFirstSpell("你说 重庆话"));
//        System.out.println(getFirstSpell("你说 重庆话张三"));
//        System.out.println(getFirstSpell("你说 重庆话(){}【】.@张三"));

        System.out.println("===============");
        System.out.println(getChinese(""));
        System.out.println(getChinese("AbCd你说 重庆话(){}【】.@张三zhangsan"));

    }

}
