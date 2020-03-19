package com.note.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * 全局异常处理类
 * 该类上需要添加@ControllerAdvice 注解
 */
@ControllerAdvice
public class AdviceException {
    /**
     * java.lang.ArithmeticException
     * 该方法需要返回一个ModelAndView：封装异常信息以及视图的指定
     * 参数Exception e:会将产生异常对象注入到方法中
     */
    @ExceptionHandler(value = {ArithmeticException.class})
    public ModelAndView arithmeticExceptionHandler(Exception e) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("error", e.toString());
        mv.setViewName("error/error1");
        return mv;
    }

    @ExceptionHandler(value = {NullPointerException.class})
    public ModelAndView nullPointerExceptionHandler(Exception e) {
        ModelAndView mv = new ModelAndView();
        mv.addObject("error", e.toString());
        mv.setViewName("error/error2");
        return mv;
    }

}
