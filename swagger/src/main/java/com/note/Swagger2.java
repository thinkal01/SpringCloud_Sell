package com.note;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger2配置类
 * 在与spring boot集成时，放在与Application.java同级的目录下
 * 通过@Configuration注解，让Spring来加载该类配置
 * 再通过@EnableSwagger2注解来启用Swagger2
 */
@Configuration
@EnableSwagger2
public class Swagger2 {
    /**
     * 创建API应用
     * apiInfo() 增加API相关信息
     * 通过select() 函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
     * 本例采用指定扫描包路径来定义指定要建立API的目录。
     */
    @Bean
    public Docket createRestApi() {
        // 默认参数，添加上后，所有接口都会有一个公共参数，不需要在每个接口单独配置
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("token").description("令牌").modelRef(new ModelRef("string")).
                parameterType("query").required(false).build();
        pars.add(tokenPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 基于注解扫描的位置，写到目录最外层即可
                .apis(RequestHandlerSelectors.basePackage("com.note.controller"))
                .paths(PathSelectors.any())
                .build()
                // .globalOperationParameters(pars)
                ;
    }

    /**
     * 创建该API的基本信息（这些基本信息会展现在文档页面中）
     * 访问地址：http://项目实际地址/swagger-ui.html
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot中使用Swagger2构建RESTful APIs")
                .description("个人测试用api")
                .termsOfServiceUrl("http://www.baidu.com")
                .contact("测试")
                .version("1.0")
                .build();
    }

}
