package com.note.expression;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Aviator01 {
    // Aviator的使用都是通过com.googlecode.aviator.AviatorEvaluator这个入口类来处理
    @Test
    public void test() {
        // Aviator的数值类型仅支持Long和Double，
        // 任何整数都将转换成Long，任何浮点数都将转换为Double，包括用户传入的变量数值
        Long result = (Long) AviatorEvaluator.execute("1+2+3");
        System.out.println(result);
    }

    /**
     * 使用变量
     */
    @Test
    public void test02() {
        String name = "zhangsan";
        Map<String, Object> env = new HashMap<String, Object>();
        env.put("_name", name);
        /**
         * Aviator的String是任何用单引号或者双引号括起来的字符序列，
         * String可以比较大小(基于unicode顺序)，可以参与正则匹配，可以与任何对象相加，
         * 任何对象与String相加结果为String。String中也可以有转义字符，如/n、//、/'等。
         */
        String result = (String) AviatorEvaluator.execute(" 'hello ' + _name ", env); // hello zhangsan
        System.out.println(result);

        System.out.println(AviatorEvaluator.execute("'a\"b'"));   //字符串 a"b
        System.out.println(AviatorEvaluator.execute("'hello '+3"));  //字符串 hello 3
        System.out.println(AviatorEvaluator.execute("'hello '+ unknow"));  //字符串 hello null
    }

    /**
     * 调用函数
     * Aviator支持函数调用，函数调用的风格类似lua
     */
    @Test
    public void test03() {
        System.out.println(AviatorEvaluator.execute("string.length('hello')"));
        // 嵌套调用
        // true
        System.out.println(AviatorEvaluator.execute("string.contains(\"test\",string.substring('hello',1,2))"));
    }

    /**
     * 自定义函数
     */
    @Test
    public void test04() {
        //注册函数
        AviatorEvaluator.addFunction(new AddFunction());
        System.out.println(AviatorEvaluator.execute("add(1,2)"));
        System.out.println(AviatorEvaluator.execute("add(add(1,2),100)"));
    }

    class AddFunction extends AbstractFunction {
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
            AviatorDouble left = (AviatorDouble) arg1;
            AviatorDouble right = (AviatorDouble) arg2;
            return new AviatorDouble(arg1.getValue(env) + arg2.getValue(env));
        }

        public String getName() {
            return "add";
        }
    }
}
