package com.note.client.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class ClientController {
    @Value("${client}")
    private String client;

    @Value("${priprop}")
    private String priprop;

    // @GetMapping("/client/test")
    @RequestMapping("/client/test")
    // 可以获取json字符串
    private String test(@RequestBody String phone) throws Exception {
        /*BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("echarts.js")));
        String returnStr = "";
        char[] bytes = new char[1024];
        while (bufferedReader.read(bytes) > 0) {
            returnStr += new String(bytes);
        }*/
        return client;
    }

    @GetMapping("/info")
    private String info() {
        return "hello " + priprop;
    }

    @RequestMapping(value = "/path/test", method = {RequestMethod.GET, RequestMethod.POST})
    private String path() {
        return "path " + client;
    }

    @GetMapping("getId")
    public Integer getId(int count) {
        return count;
    }
}
