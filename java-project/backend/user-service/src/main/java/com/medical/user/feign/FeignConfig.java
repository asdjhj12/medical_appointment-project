package com.medical.user.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Feign配置类，用于在服务间调用时传递认证信息
 */
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 始终添加固定的认证令牌以支持服务间通信
                template.header("Authorization", "Bearer " + generateServiceToken());
                
                // 尝试从当前请求上下文获取头信息
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    
                    // 传递其他可能需要的头信息
                    Enumeration<String> headerNames = request.getHeaderNames();
                    if (headerNames != null) {
                        while (headerNames.hasMoreElements()) {
                            String headerName = headerNames.nextElement();
                            // 排除一些不需要传递的头
                            if (!"Content-Length".equalsIgnoreCase(headerName) && 
                                !"Host".equalsIgnoreCase(headerName) && 
                                !"Connection".equalsIgnoreCase(headerName) && 
                                !"Content-Type".equalsIgnoreCase(headerName) &&
                                !"Authorization".equalsIgnoreCase(headerName)) {
                                
                                String headerValue = request.getHeader(headerName);
                                template.header(headerName, headerValue);
                            }
                        }
                    }
                }
                
                // 打印调试信息
                System.out.println("[FeignInterceptor] 请求URL: " + template.url());
                template.headers().forEach((key, values) -> {
                    System.out.println("[FeignInterceptor] 请求头: " + key + " = " + values);
                });
            }
        };
    }
    
    /**
     * 生成用于服务间通信的固定令牌
     * 注意：实际生产环境应该使用更安全的方式，如OAuth2客户端凭证授权流程
     */
    private String generateServiceToken() {
        // 这个是一个固定的服务间通信令牌，实际生产环境应该更安全
        // 您可以替换为从配置文件中读取的值
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    }
} 