package com.imooc.zuul;

import com.imooc.zuul.consistent.ConsistentHashWithVirtualNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

@Slf4j
public class ConsistentHashTest {

    private List<String> uniqueIdList;

    @Test
    public void test01() {
        HashMap<String, Integer> result;
        Map<Double, String> varMap = new TreeMap<>();
        List<String> serverList = Arrays.asList("30.79.120.85", "30.79.120.80", "30.79.120.90");

        List<String> uniqueIdList = getUniqueIdList();
        int realSize = serverList.size();
        int sum = uniqueIdList.size();
        int average = sum / realSize;
        System.out.println(MessageFormat.format("真实节点数{0},样本总数{1},平均数{2}", realSize, sum, average));

        for (int virtualNodeNum = realSize; virtualNodeNum < realSize * 10; virtualNodeNum++) {
            result = new HashMap<>();
            ConsistentHashWithVirtualNode consistentHashWithVirtualNode = new ConsistentHashWithVirtualNode(serverList, virtualNodeNum);
            for (String uniqueId : uniqueIdList) {
                String server = consistentHashWithVirtualNode.getServer(uniqueId);
                result.put(server, (result.containsKey(server) ? result.get(server) + 1 : 1));
            }
            double var = getVar(result.values(), average);
            varMap.put(var, MessageFormat.format("虚拟节点数{0},结果{1}", virtualNodeNum, result.values()));
            // System.out.println(MessageFormat.format("虚拟节点数{0},方差{1}", virtualNodeNum, var));
        }

        varMap.values().forEach(System.out::println);
        // List<Integer> valueList = new ArrayList<>(result.values());
        // Collections.sort(valueList);
        // System.out.println(result);
    }

    private double getVar(Collection<Integer> valueList, int average) {
        double var = 0;
        for (Integer value : valueList) {
            double diff = value - average;
            var += diff * diff;
        }
        return var;
    }

    public List<String> getUniqueIdList() {
        if (!CollectionUtils.isEmpty(uniqueIdList)) {
            return uniqueIdList;
        }

        uniqueIdList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(getResourcePath("uniqueId.txt")))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                uniqueIdList.add(line);
            }
        } catch (Exception e) {
        }

        return uniqueIdList;
    }

    // 获取资源目录
    public static String getResourcePath(String fileName) {
        URL resource = Object.class.getResource("/" + fileName);
        String path = resource.getPath();
        return path;
    }

}
