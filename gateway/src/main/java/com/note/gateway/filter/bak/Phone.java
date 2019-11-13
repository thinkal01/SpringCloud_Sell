package com.note.gateway.filter.bak;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Phone {
    //移动电话号码前三位
    public static final String[] YD = {
            "134", "135", "136",
            "137", "138", "139",
            "150", "151", "152",
            "157", "158", "159",
            "180", "181", "182",
            "183", "184", "185",
            "174", "192", "178",
    };
    //电信号码前三位
    public static final String[] DX = {
            "133", "149", "153",
            "173", "177", "180",
            "181", "189", "199"
    };
    //联通号码前三位
    public static final String[] LT = {
            "130", "131", "132",
            "145", "155", "156",
            "166", "171", "175",
            "176", "185", "186", "166"
    };

    public static void main(String[] args) {
        try {
            writer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writer() throws IOException {
        FileOutputStream fos = new FileOutputStream(new File("phone2.txt"));
        Scanner scanner = new Scanner(System.in);
        int count = 0;
        while (true) {
            //写入移动
            fos.write(scyYD().toString().getBytes());
            //写入分隔符
            fos.write("\t".getBytes());
            //写入电信
            fos.write(scyDX().toString().getBytes());
            //写入分隔符
            fos.write("\t".getBytes());
            //写入联通
            fos.write(scyLT().toString().getBytes());
            //写入分隔符
            fos.write("\t".getBytes());
            //累加计数
            count++;
            //判断是否换行
            if (count % 3 == 0) {
                //每1000行暂停，输入q+enter结束 ，其他任意字母继续执行
                if (count % 1000 == 0) {
                    String next = scanner.next();
                    if (next.equals("q")) {
                        //关闭退出循环
                        fos.close();
                        break;
                    }
                }
                //每行9个电话号码换行
                fos.write("\n".getBytes());
            } else {
                fos.write("\t".getBytes());
            }
        }
    }

    public static String getPhone() {
        int nextInt = new Random().nextInt(10);
        if (nextInt < 4) {
            return scyYD().toString();
        } else if (nextInt < 8) {
            return scyLT().toString();
        } else {
            return scyDX().toString();
        }
    }

    public static StringBuilder scyYD() {
        //定义随机数
        Random random = new Random();
        //从移动号码规则里面随机一个号码前三位
        int i = random.nextInt(YD.length);
        //随机号码的第4位数字
        int i1 = random.nextInt(10);
        //随机号码的第5位数字
        int i2 = random.nextInt(10);
        //随机号码的第6位数字
        int i3 = random.nextInt(10);
        //随机号码的第7位数字
        int i4 = random.nextInt(10);
        //随机号码的第8位数字
        int i5 = random.nextInt(10);
        //随机号码的第9位数字
        int i6 = random.nextInt(10);
        //随机号码的第10位数字
        int i7 = random.nextInt(10);
        //随机号码的第11位数字
        int i8 = random.nextInt(10);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(YD[i]).append(i1).append(i2).append(i3).append(i4).append(i5).append(i6).append(i7).append(i8);
        return stringBuilder;
    }

    //生成电信号码
    public static StringBuilder scyDX() {
        //定义随机数
        Random random = new Random();
        //从电信号码规则里面随机一个号码前三位
        int i = random.nextInt(DX.length);
        //随机号码的第4位数字
        int i1 = random.nextInt(10);
        //随机号码的第5位数字
        int i2 = random.nextInt(10);
        //随机号码的第6位数字
        int i3 = random.nextInt(10);
        //随机号码的第7位数字
        int i4 = random.nextInt(10);
        //随机号码的第8位数字
        int i5 = random.nextInt(10);
        //随机号码的第9位数字
        int i6 = random.nextInt(10);
        //随机号码的第10位数字
        int i7 = random.nextInt(10);
        //随机号码的第11位数字
        int i8 = random.nextInt(10);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DX[i]).append(i1).append(i2).append(i3).append(i4).append(i5).append(i6).append(i7).append(i8);
        return stringBuilder;
    }

    //生成联通号码
    public static StringBuilder scyLT() {
        //定义随机数
        Random random = new Random();
        //从移动号码规则里面随机一个号码前三位
        int i = random.nextInt(LT.length);
        //随机号码的第4位数字
        int i1 = random.nextInt(10);
        //随机号码的第5位数字
        int i2 = random.nextInt(10);
        //随机号码的第6位数字
        int i3 = random.nextInt(10);
        //随机号码的第7位数字
        int i4 = random.nextInt(10);
        //随机号码的第8位数字
        int i5 = random.nextInt(10);
        //随机号码的第9位数字
        int i6 = random.nextInt(10);
        //随机号码的第10位数字
        int i7 = random.nextInt(10);
        //随机号码的第11位数字
        int i8 = random.nextInt(10);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LT[i]).append(i1).append(i2).append(i3).append(i4).append(i5).append(i6).append(i7).append(i8);
        return stringBuilder;
    }
}