package com.note.alogrithm;

public class StringUtils {

    public static boolean isLike(String source, String regex) {
        return isLike(source, regex, false);
    }

    /**
     * 判断source字符串是否能够被regex匹配,源字符串不能包含?,*
     * @param source 任意字符串
     * @param regex 包含*或？的匹配表达式
     * @param ignoreCase 大小写敏感
     */
    public static boolean isLike(String source, String regex, boolean ignoreCase) {
        if (source == null || regex == null) return false;
        if (ignoreCase) {
            source = source.toLowerCase();
            regex = regex.toLowerCase();
        }
        //去除多余*号
        return matches(source, regex.replaceAll("\\*{2,}", "*"));
    }

    private static boolean matches(String source, String regex) {
        // 如果source与regex完全相等，返回true
        if (source.equals(regex)) return true;
        int regexIdx = 0, sourceIdx = 0;
        while (regexIdx < regex.length() && sourceIdx < source.length()) {
            //以匹配表达式为主导
            char c = regex.charAt(regexIdx);
            switch (c) {
                case '*':
                    String tempRegex = regex.substring(regexIdx + 1);//从星号后一位开始认为是新的匹配表达式
                    //此处等号不能缺，如（ABCD，*），等号能达成("", *)条件
                    for (int j = sourceIdx; j <= source.length(); j++) {
                        //去除前面已经完全匹配的前缀
                        String tempSource = source.substring(j);
                        if (matches(tempSource, tempRegex)) {
                            return true;
                        }
                    }
                    return false;//排除所有潜在可能性，则返回false
                case '?':
                    break;
                default:
                    if (source.charAt(sourceIdx) != c) return false;//普通字符的匹配
            }
            regexIdx++;
            sourceIdx++;
        }
        //最终source被匹配完全，而regex也被匹配完整或只剩一个*号
        return source.length() == sourceIdx &&
                (regex.length() == regexIdx ||
                        regex.length() == regexIdx + 1 && regex.charAt(regexIdx) == '*');
    }

    public static void main(String args[]) {
        // System.out.println("str=ABC regex=AB\\C :" + isLike("ABC", "AB\\C"));
        // System.out.println("str=ABCD regex=ABC? :" + isLike("ABCD", "ABC?"));
        // System.out.println("str=ABCD regex=A??? :" + isLike("ABCD", "A???"));
        // System.out.println("str=ABCD regex=A?? :" + isLike("ABCD", "A??"));
        // System.out.println("str=ABCD regex=?BC? :" + isLike("ABCD", "?BC?"));
        // System.out.println("str=ABCD regex=*B*D :" + isLike("ABCD", "*B*D"));
        // System.out.println("str=ABCD regex=*BCD :" + isLike("ABCD", "*BCD"));
        // System.out.println("str=ABCD regex=*A*B*D :" + isLike("ABCD", "*A*B*D"));
        System.out.println("str=ABCD regex=A* :" + isLike("ABCD", "A*"));
        // System.out.println("str=A?BCD regex=A\\?* :" + isLike("A?BCD", "A\\?*"));
        // System.out.println("str=? regex=\\? :" + isLike("?", "\\?"));
        // System.out.println("str=\\? regex=\\? :" + isLike("\\?", "\\?"));//这个只匹配问号，而源字符串为反斜杠问号
        // System.out.println("str=\\? regex=\\\\\\? :" + isLike("\\?", "\\\\\\?"));
    }

    //借用正则表达式(投机取巧)。
    //不建议使用，如要使用还得更正。需要去除regex中一些字符的特殊含义
    //如'.','?','=','[',']','?','^','$','{','}',',',':','!','-','+'等
    //由于情况复杂，不做过多的更正
    public static boolean isLike2(String source, String regex) {
        if (source == null || regex == null) return false;
        StringBuilder newRegex = new StringBuilder();
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            switch (c) {
                case '*':
                    newRegex.append(".*");
                    break;
                case '?':
                    newRegex.append(".");
                    break;
                case '\\':
                    newRegex.append("\\");
                    c = regex.charAt(++i);
                default:
                    newRegex.append(c);
            }
        }
        return source.matches(newRegex.toString());
    }
}