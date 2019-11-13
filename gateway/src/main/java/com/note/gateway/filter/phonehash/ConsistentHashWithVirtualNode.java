package com.note.gateway.filter.phonehash;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 带虚拟节点的一致性Hash算法
 */
@Slf4j
@Getter
public class ConsistentHashWithVirtualNode {
    // 待添加入Hash环的服务器列表
    public List<String> serverList;

    // 真实结点列表,考虑到服务器上线、下线的场景，即添加、删除的场景会比较频繁，这里使用LinkedList会更好
    public List<String> realNodes = new LinkedList<>();

    // 虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
    public SortedMap<Integer, String> HASH_CIRCLE = new TreeMap<>();

    // 虚拟节点数量
    public int VIRTUAL_NODE_NUM;

    public ConsistentHashWithVirtualNode(List<String> serverList) {
        this.serverList = serverList;
        realNodes.addAll(serverList);
        // 暂定为服务实例数,经测试分发请求数较为平均
        VIRTUAL_NODE_NUM = serverList.size();

        // 添加虚拟节点，遍历LinkedList使用foreach循环效率会比较高
        for (String str : realNodes) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                String virtualNode = str + "&&VN" + String.valueOf(i);
                setServer(virtualNode, null);
            }
        }
    }

    // 将虚拟节点存到hash环中
    public void setServer(String virtualNode, Integer hash) {
        hash = hash != null ? getHash(hash.toString()) : getHash(virtualNode);
        if (StringUtils.isBlank(HASH_CIRCLE.get(hash))) {
            // 截取ip值存入map，方便取出使用
            HASH_CIRCLE.put(hash, virtualNode.substring(0, virtualNode.indexOf("&&")));
            log.info("虚拟节点[" + virtualNode + "]被添加, hash值为" + hash);
        } else {
            // 解决hash碰撞
            setServer(virtualNode, hash);
        }
    }

    // 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
    public int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    // 得到应当路由到的结点
    public String getServer(String key) {
        // 得到该key的hash值
        int hash = getHash(key);
        // 得到大于该Hash值的所有Map
        SortedMap<Integer, String> subMap = HASH_CIRCLE.tailMap(hash);
        String virtualNode;
        if (subMap.isEmpty()) {
            //如果没有比该key的hash值大的，则从第一个node开始
            Integer i = HASH_CIRCLE.firstKey();
            //返回对应的服务器
            virtualNode = HASH_CIRCLE.get(i);
        } else {
            //第一个Key就是顺时针过去离node最近的那个结点
            Integer i = subMap.firstKey();
            //返回对应的服务器
            virtualNode = subMap.get(i);
        }
        //virtualNode虚拟节点名称要截取一下
        if (StringUtils.isNotBlank(virtualNode)) {
            return virtualNode;
        }
        return null;
    }
}