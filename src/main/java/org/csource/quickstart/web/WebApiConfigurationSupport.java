package org.csource.quickstart.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableWebMvc
@EnableSwagger2 
@ComponentScan(basePackages = {"org.csource.quickstart.web.controller"}) 
@Configuration
public class WebApiConfigurationSupport extends WebMvcConfigurationSupport{
  
  @Bean 
  public Docket createRestApi() { 
      return new Docket(DocumentationType.SWAGGER_2) 
              .apiInfo(apiInfo()) 
              .select() 
              .apis(RequestHandlerSelectors.basePackage("org.csource.quickstart.web.controller")) 
              .paths(PathSelectors.any()) 
              .build(); 
  } 
 
  private ApiInfo apiInfo() { 
      Contact contact = new Contact("SongJian", "https://github.com/devpage", "1422204321@qq.com");
      ApiInfo apiInfo = new ApiInfo("上传文件服务",//大标题
              "相关接口",//小标题
              "1.2.1",//版本
              org.csource.quickstart.Constants.configure.get("site"),
              contact,//作者
              "Apache Licene 2.0",//链接显示文字
              "http://www.apache.org/licenses/LICENSE-2.0.html"//网站链接
      );
      return apiInfo;
  } 
  
}
