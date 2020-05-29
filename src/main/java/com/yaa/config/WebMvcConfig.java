package com.yaa.config;

import com.yaa.interceptor.BaseInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;
import java.util.Arrays;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Resource
    private PathConfig pathConfig;
    @Resource
    private BaseInterceptor baseInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**").addResourceLocations("file:///"+pathConfig.getFilePath());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        //在Spring添加拦截器之前先自己创建一下这个Spring Bean，这样就能在Spring映射这个拦截器前，把拦截器中的依赖注入给完成了。
        registry.addInterceptor(baseInterceptor).addPathPatterns("/**")
                .excludePathPatterns(Arrays.asList("/themes/css/**","/themes/js/**","/themes/img/**",
                        "/admin/css/**","/admin/fonts/**","/admin/js/**","/admin/layui/**"));
        super.addInterceptors(registry);
    }
}
