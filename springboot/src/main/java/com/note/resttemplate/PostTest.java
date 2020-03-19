package com.note.resttemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.awt.print.Book;
import java.util.HashMap;
import java.util.Map;

public class PostTest {

    // post请求不带参数
    public void postForObject01() {
        RestTemplate restTemplate = new RestTemplate();
        String res = restTemplate.postForObject("http://localhost:8080/test", null, String.class);
    }

    // post请求带参数
    public void postForObject02() {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("str", "hello");
        String result = restTemplate.postForObject("http://localhost:8080/test", map, String.class);
        System.out.println(result);
    }

    public void postForEntity02() {
        RestTemplate restTemplate = new RestTemplate();
        // Book book = new Book();
        // book.setName("红楼梦");
        // ResponseEntity<Book> responseEntity = restTemplate.postForEntity("http://HELLO-SERVICE/getbook2", book, Book.class);
    }

    // json请求
    public void test05() {
        RestTemplate restTemplate = new RestTemplate();
        Map paramMap = new HashMap();
        paramMap.put("param1", "value1");

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Map> formEntity = new HttpEntity<>(paramMap, headers);

        String s = restTemplate.postForEntity("", formEntity, String.class).getBody();
    }
}