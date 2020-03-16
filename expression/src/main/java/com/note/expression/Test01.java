package com.note.expression;

import com.googlecode.aviator.AviatorEvaluator;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Test01 {
    @Test
    public void test01() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("i", 100);
        vars.put("pi", 3.14f);
        vars.put("d", -3.9);
        vars.put("b", (byte) 4);
        vars.put("bool", false);

        //常量运算1
        String express01 = "1000 + 100.0 * 99 - (600 - 3 * 15) % (((68 - 9) - 3) * 2 - 100) + 10000 % 7 * 71";
        //常量运算2
        String express02 = "6.7 - 100 > 39.6 ? 5 == 5 ? 4 + 5 : 6 - 1 : !(100 % 3 - 39.0 < 27) ? 8 * 2 - 199 : 100 % 3";
        //变量+常量复合运算
        String express03 = "i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99 == i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99";
        //简单变量处理
        String express04 = "i * pi";
        //简单常量处理
        String express05 = "1";

        long start = System.currentTimeMillis();
        for (int i = 0; i < 10 * 10000; ++i) {
            System.out.println(AviatorEvaluator.execute(express01, vars));
            System.out.println(AviatorEvaluator.execute(express02, vars));
            System.out.println(AviatorEvaluator.execute(express03, vars));
            System.out.println(AviatorEvaluator.execute(express04, vars));
            System.out.println(AviatorEvaluator.execute(express05, vars));
        }
        long end = System.currentTimeMillis();
        System.out.println("不加缓存耗时：" + (end - start) + " ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < 10 * 10000; ++i) {
            System.out.println(AviatorEvaluator.execute(express01, vars, true));
            System.out.println(AviatorEvaluator.execute(express02, vars, true));
            System.out.println(AviatorEvaluator.execute(express03, vars, true));
            System.out.println(AviatorEvaluator.execute(express04, vars, true));
            System.out.println(AviatorEvaluator.execute(express05, vars, true));
        }
        end = System.currentTimeMillis();
        System.out.println("加缓存耗时：" + (end - start) + " ms");
    }

}
