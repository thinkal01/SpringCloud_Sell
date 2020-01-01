package com.note.expression;

import com.googlecode.aviator.AviatorEvaluator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

public class Aviator02 {
    /**
     * 通过中括号去访问数组和java.util.List对象
     * 通过map.key访问java.util.Map中key对应的value
     */
    @Test
    public void test01() {
        final List<String> list = new ArrayList<>();
        list.add("hello");
        list.add(" world");

        final int[] array = new int[3];
        array[0] = 0;
        array[1] = 1;
        array[2] = 3;

        final Map<String, Date> map = new HashMap<>();
        map.put("date", new Date());

        Map<String, Object> env = new HashMap<>();
        env.put("list", list);
        env.put("array", array);
        env.put("mmap", map);

        System.out.println(AviatorEvaluator.execute(
                "list[0]+list[1]+'\narray[0]+array[1]+array[2]='+(array[0]+array[1]+array[2]) +' \ntoday is '+mmap.date ", env));
    }

    /**
     * Aviator不提供if else语句
     * 三元操作符?:用于条件判断
     */
    @Test
    public void test02() {
        Map<String, Object> env = new HashMap<>();
        env.put("a", 10);
        String result = (String) AviatorEvaluator.execute("a>0? 'yes':'no'", env);
        System.out.println(result);
    }

    /**
     * 正则表达式匹配
     */
    @Test
    public void test03() {
        String email = "killme2008@gmail.com";
        Map<String, Object> env = new HashMap<>();
        env.put("email", email);
        String username = (String) AviatorEvaluator.execute("email=~/([\\w0-8]+@\\w+[\\.\\w+]+)/ ? $1:'unknow'", env);
        System.out.println(username);
    }

    /**
     * 语法糖
     * 访问对象属性
     */
    @Test
    public void test04() {
        Foo foo = new Foo(100, 3.14f, new Date());
        Map<String, Object> env = new HashMap<>();
        env.put("foo", foo);
        System.out.println(AviatorEvaluator.execute("'foo.i = '+foo.i", env));   // foo.i = 100
        System.out.println(AviatorEvaluator.execute("'foo.f = '+foo.f", env));   // foo.f = 3.14
        System.out.println(AviatorEvaluator.execute("'foo.date.year = '+(foo.date.year+1990)", env));  // foo.date.year = 2106
    }

    @AllArgsConstructor
    @Getter
    class Foo {
        int i;
        float f;
        Date date;
    }

    /**
     * nil对象
     */
    @Test
    public void test05() {
        AviatorEvaluator.execute("nil == nil");   //true
        AviatorEvaluator.execute(" 3> nil");      //true
        AviatorEvaluator.execute(" true!= nil");  //true
        AviatorEvaluator.execute(" ' '>nil ");    //true
        AviatorEvaluator.execute(" a==nil ");     //true, a 是 null
    }

    /**
     * Aviator 并不支持日期类型,如果要比较日期,你需要将日期写字符串的形式,并且要求是形如 “yyyy-MM-dd HH:mm:ss:SS”的字符串,否则都将报错。
     * 字符串跟java.util.Date比较的时候将自动转换为Date对象进行比较
     * String除了能跟String比较之外,还能跟nil和java.util.Date对象比较。
     */
    @Test
    public void test06() {
        Map<String, Object> env = new HashMap<>();
        Date date = new Date();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(date);
        env.put("date", date);
        env.put("dateStr", dateStr);

        Boolean result = (Boolean) AviatorEvaluator.execute("date==dateStr", env);
        System.out.println(result);  // true

        result = (Boolean) AviatorEvaluator.execute("date > '2010-12-20 00:00:00:00' ", env);
        System.out.println(result);  // true

        result = (Boolean) AviatorEvaluator.execute("date < '2200-12-20 00:00:00:00' ", env);
        System.out.println(result);  // true

        result = (Boolean) AviatorEvaluator.execute("date==date ", env);
        System.out.println(result);  // true
    }

    /**
     *大数计算和精度
     * 从 2.3.0 版本开始,aviator 开始支持大数字计算和特定精度的计算, 本质上就是支持java.math.BigInteger和java.math.BigDecimal两种类型,
     * 这两种类型在 aviator 中简称为big int和decimal类型。
     * 类似99999999999999999999999999999999这样的数字在 Java 语言里是没办法编译通过的, 因为它超过了Long类型的范围, 只能用BigInteger来封装。
     * 但是 aviator 通过包装,可以直接支持这种大整数的计算
     */
    @Test
    public void test07() {
        System.out.println(AviatorEvaluator.execute("99999999999999999999999999999999 + 99999999999999999999999999999999"));
    }

    @Test
    public void test08() {
        Object rt = AviatorEvaluator.execute("9223372036854775807100.356M * 2");
        System.out.println(rt + " " + rt.getClass());  // 18446744073709551614200.712 class java.math.BigDecimal

        rt = AviatorEvaluator.execute("92233720368547758074+1000");
        System.out.println(rt + " " + rt.getClass());  // 92233720368547759074 class java.math.BigInteger

        BigInteger a = new BigInteger(String.valueOf(Long.MAX_VALUE) + String.valueOf(Long.MAX_VALUE));
        BigDecimal b = new BigDecimal("3.2");
        BigDecimal c = new BigDecimal("9999.99999");
        rt = AviatorEvaluator.exec("a+10000000000000000000", a);
        System.out.println(rt + " " + rt.getClass());  // 92233720368547758089223372036854775807 class java.math.BigInteger

        rt = AviatorEvaluator.exec("b+c*2", b, c);
        System.out.println(rt + " " + rt.getClass());  // 20003.19998 class java.math.BigDecimal

        rt = AviatorEvaluator.exec("a*b/c", a, b, c);
        System.out.println(rt + " " + rt.getClass());  // 2.951479054745007313280155218459508E+34 class java.math.BigDecimal
    }
}
