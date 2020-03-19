package com.note.controller;

import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 注意@ApiImplicitParam的使用会影响程序运行，如果使用不当可能造成控制器收不到消息
 */
@Controller
@RequestMapping("/say")
@Api(value = "SayController,用来测试swagger注解的控制器")
public class SayController {

    /**
     * httpMethod 是接口的提交方法，不写会把所有的提交方式都展示一遍
     */
    @ApiOperation(value = "修改用户密码", notes = "根据用户id修改密码", httpMethod = "POST", response = String.class)
    /*
     name是参数名称，
     value是参数说明，
     dataType是参数类型，不是限制，仅做说明使用，
     required true时是必传参数
     paramType是提交方式
    */
    // @ApiImplicitParam(name = "userNumber", value = "用户编号", example = "1", required = true, dataType = "int", paramType = "query")
    // 多个参数
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "token", value = "token", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "userId", value = "用户ID", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", name = "password", value = "旧密码", required = true, dataType = "String"),
    })
    // 返回值说明
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = String.class),
            @ApiResponse(code = 405, message = "Invalid input", response = Void.class)
    })
    @ResponseBody
    @RequestMapping("/updatePassword")
    public String updatePassword(@RequestParam(value = "userId") Integer userId,
                                 @RequestParam(value = "password") String password,
                                 HttpServletRequest request) {
        String token = request.getHeader("token");
        return "密码修改成功!";
    }

    /**
     * 在使用对象封装参数进行传参时，需要在该对象添加注解，将其注册到swagger中
     * 注意：在后台采用对象接收参数时，Swagger自带工具采用的是JSON传参，
     * 测试时需要在参数上加入@RequestBody,正常运行采用form或URL提交时候请删除
     */
    @ApiOperation(value = "添加医生信息", notes = "添加医生信息")
    @ResponseBody
    @RequestMapping(value = "/doctor", method = RequestMethod.POST)
    public String addDoctor(@RequestBody Doctor doctor) {
        return doctor.getId().toString();
    }

    /**
     * 修改医生信息
     *
     * @param doctorId 医生ID
     * @param doctor   医生信息
     */
    @ApiOperation(value = "修改医生信息", notes = "")
    @ApiImplicitParam(paramType = "path", name = "doctorId", value = "医生ID", required = true, dataType = "Integer")
    @ResponseBody
    @RequestMapping(value = "/doctor/{doctorId}", method = RequestMethod.POST)
    public String updateDoctor(@PathVariable Integer doctorId, @RequestBody Doctor doctor) {
        return "修改成功";
    }
}
