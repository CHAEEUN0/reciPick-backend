package team3.recipick.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")   // ✅ 모든 origin 허용 (withCredentials와 같이 사용 가능)
                .allowedMethods("*")          // 모든 HTTP 메서드 허용
                .allowedHeaders("*")          // 모든 헤더 허용
                .allowCredentials(true)       // 쿠키/세션 허용
                .maxAge(86400);
    }


}