package com.note.resttemplate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class GetTest {
    // get请求不带参数
    public void getForEntity01() {
        RestTemplate restTemplate = new RestTemplate();
        // getForObject是对getForEntity的进一步封装，只关注返回的消息体的内容
        String res = restTemplate.getForObject("http://localhost:8080/test", String.class);
        // ResponseEntity<T>是Spring对HTTP请求响应的封装，包括响应码、contentType、contentLength、响应消息体等
        // 一种是采用服务别名方式调用，另一种是直接调用,使用别名去注册中心上获取对应的服务调用地址
        ResponseEntity<String> response = restTemplate.getForEntity("http://app-itmayiedu-member/getUser", String.class);
        System.out.println("statusCode:" + response.getStatusCode());
        System.out.println("Body:" + response.getBody());
    }

    // get请求带参数
    public void getForObject02() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> map = new HashMap<>();
        map.put("str", "hello");
        // 可以用一个数字做占位符，最后是一个可变长度的参数，来替换前面的占位符
        restTemplate.getForObject("http://fantj.top/notice/list/{1}/{2}", String.class, 1, 5);
        // 使用name={name}，map的key即为前边占位符的名字，map的value为参数值
        String res = restTemplate.getForObject("http://localhost:8080/test?str={str}", String.class, map);
    }

    public void getForEntity02() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> map = new HashMap<>();
        map.put("name", "李四");
        // ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE/sayhello?name={1}", String.class, "张三");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE/sayhello?name={name}", String.class, map);
        String body = responseEntity.getBody();
    }

    public String getForEntity04() {
        RestTemplate restTemplate = new RestTemplate();
        UriComponents uriComponents = UriComponentsBuilder.fromUriString("http://HELLO-SERVICE/sayhello?name={name}").build().expand("王五").encode();
        URI uri = uriComponents.toUri();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
        return responseEntity.getBody();
    }
}
