package com.delmon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 使用固定的项目根目录路径
        String staticImagesPath = "file:F:/study_project/springcloud/Bundaberg/projects/bundbaberg/bundaberg/static/images/";
        File staticDir = new File("F:/study_project/springcloud/Bundaberg/projects/bundbaberg/bundaberg/static/images/");
        
        System.out.println("===========================================");
        System.out.println("静态资源配置加载中...");
        System.out.println("静态资源目录是否存在: " + staticDir.exists());
        System.out.println("静态资源目录路径: " + staticDir.getAbsolutePath());
        if (staticDir.exists()) {
            File[] files = staticDir.listFiles();
            if (files != null) {
                System.out.println("目录中的文件数量: " + files.length);
                for (File f : files) {
                    System.out.println("  - " + f.getName());
                }
            }
        }
        System.out.println("===========================================");
        
        String avatarPath = "file:F:/study_project/springcloud/Bundaberg/projects/bundbaberg/bundaberg/static/images/avatars/";
        File avatarDir = new File("F:/study_project/springcloud/Bundaberg/projects/bundbaberg/bundaberg/static/images/avatars/");
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }
        
        registry.addResourceHandler("/images/**")
                .addResourceLocations(staticImagesPath)
                .addResourceLocations(avatarPath);
    }
}
