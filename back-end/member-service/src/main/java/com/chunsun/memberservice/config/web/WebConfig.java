package com.chunsun.memberservice.config.web;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.chunsun.memberservice.config.interceptor.StudentIdArgumentResolver;
import com.chunsun.memberservice.config.interceptor.TeacherIdArgumentResolver;
import com.chunsun.memberservice.config.interceptor.UserInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final UserInterceptor userInterceptor;
    private final StudentIdArgumentResolver studentIdArgumentResolver;
    private final TeacherIdArgumentResolver teacherIdArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(studentIdArgumentResolver);
        resolvers.add(teacherIdArgumentResolver);
    }
}
