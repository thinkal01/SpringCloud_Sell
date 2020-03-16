package com.note.expression;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorBigInt;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Aviator01 {
    // Aviator的使用都是通过com.googlecode.aviator.AviatorEvaluator这个入口类来处理
    @Test
    public void test() {
        // Aviator只支持四种数字类型（2.3.0后）：Long、Double、big int、decimal
        // 理论上任何整数都将转换成Long（超过Long范围的，将自动转换为big int），任何浮点数都将转换为Double，包括用户传入的变量数值
        // 以大写字母N为后缀的整数都被认为是big int，如1N,2N,99999N等，都是big int类型。
        // 以大写字母M为后缀的数字都被认为是decimal，如1M,2.222M, 100000.9999M等，都是decimal类型。
        Long result = (Long) AviatorEvaluator.execute("1+2+3");
        System.out.println(result);
    }

    /**
     * 使用变量
     */
    @Test
    public void test02() {
        String name = "zhangsan";
        Map<String, Object> env = new HashMap<>();
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
        String str = "Hello Aviator";
        Map<String, Object> env = new HashMap<>();
        env.put("str", str);
        Long length = (Long) AviatorEvaluator.execute("string.length(str)", env);
        System.out.println(length);
        // 嵌套调用
        // true
        System.out.println(AviatorEvaluator.execute("string.contains(\"test\",string.substring('hello',1,2))"));
    }

    /**
     * compile用法
     */
    @Test
    public void test05() {
        String expression = "a-(b-c)>100";
        Expression compiledExp = AviatorEvaluator.compile(expression);
        Map<String, Object> env = new HashMap<>();
        env.put("a", 100.3);
        env.put("b", 45);
        env.put("c", -199.100);
        Boolean result = (Boolean) compiledExp.execute(env);
        System.out.println(result);

        /**
         * 可以把一个编译好的Expression 缓存起来。
         * 这样可以直接获取之前编译的结果直接进行计算，避免Perm区OutOfMemory
         * Aviator本身自带一个全局缓存
         * 如果决定缓存本次的编译结果，只需要
         * Expression compiledExp = AviatorEvaluator.compile(expression,true);
         * 这样设置后，下一次编译同样的表达式，Aviator会自从从全局缓存中，拿出已经编译好的结果，不需要动态编译。
         * 如果需要使缓存失效，可以使用
         * AviatorEvaluator.invalidateCache(String expression);
         */
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

        AviatorEvaluator.addFunction(new MinFunction());
        String expression = "min(a,b)";
        Expression compiledExp = AviatorEvaluator.compile(expression, true);
        Map<String, Object> env = new HashMap<>();
        env.put("a", 100.3);
        env.put("b", 45);
        Double result = (Double) compiledExp.execute(env);
        System.out.println(result);
    }

    static class AddFunction extends AbstractFunction {
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
            Number left = FunctionUtils.getNumberValue(arg1, env);
            Number right = FunctionUtils.getNumberValue(arg2, env);
            return new AviatorDouble(left.doubleValue() + right.doubleValue());
            /*AviatorLong left = (AviatorLong) arg1;
            AviatorLong right = (AviatorLong) arg2;
            return AviatorLong.valueOf(left.longValue() + right.longValue());*/
        }

        // 实现 getName这个方法，用于定义函数在Aviator中使用的名字
        public String getName() {
            return "add";
        }
    }

    // 实际业务中，这两个数字为多个表达式计算的结果，如果不写自定函数，只能使用?:表达式来进行计算，会显得异常凌乱
    static class MinFunction extends AbstractFunction {
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
            Number left = FunctionUtils.getNumberValue(arg1, env);
            Number right = FunctionUtils.getNumberValue(arg2, env);
            return new AviatorBigInt(Math.min(left.doubleValue(), right.doubleValue()));
        }

        public String getName() {
            return "min";
        }
    }
}
