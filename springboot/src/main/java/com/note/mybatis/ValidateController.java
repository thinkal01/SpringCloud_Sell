package com.note.mybatis;


import com.note.pojo.Users;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * SpringBoot 表单数据校验
 */
@Controller
public class ValidateController {
    /**
     * 解决异常的方式。可以在跳转页面的方法中注入一个 Uesrs 对象。
     * 注意：springmvc 会将该对象放入到 Model 中传递。key 的名称会使用该对象的驼峰式的命名规则来作为 key。
     * 参数的变量名需要与对象的名称相同。将首字母小写。
     * 如果想为传递的对象更改名称，可以使用@ModelAttribute("aa")这表示当前传递的对象的key为aa。
     * 那么在页面中获取该对象的key也需要修改为aa
     *
     * @param users
     */
    @RequestMapping("/addUser")
    public String showPage(@ModelAttribute("aa") Users users) {
        return "validate/add";
    }

    /**
     * 完成用户添加
     *
     * @Valid 开启对Users对象的数据校验
     * BindingResult:封装了校验的结果
     */
    @RequestMapping("/save")
    public String saveUser(@ModelAttribute("aa") @Valid Users users, BindingResult result) {
        if (result.hasErrors()) {
            return "validate/add";
        }
        System.out.println(users);
        return "validate/ok";
    }
}
