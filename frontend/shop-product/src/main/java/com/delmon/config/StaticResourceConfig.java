package com.delmon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        List<String> locations = new ArrayList<>();
        locations.add("classpath:/static/images/");

        String userDir = System.getProperty("user.dir");
        String[] candidates = {
                userDir + "/frontend-web/public/images/",
                userDir + "/src/main/resources/static/images/",
                userDir + "/frontend/shop-product/src/main/resources/static/images/",
                "F:/大三课内/小学期/github/frontend-web/public/images/",
                "F:/study_project/springcloud/Bundaberg/projects/bundbaberg/bundaberg/static/images/"
        };

        for (String candidate : candidates) {
            File dir = new File(candidate);
            if (dir.exists() && dir.isDirectory()) {
                String absolute = dir.getAbsolutePath().replace("\\", "/");
                if (!absolute.endsWith("/")) {
                    absolute = absolute + "/";
                }
                locations.add("file:" + absolute);
            }
        }

        registry.addResourceHandler("/images/**")
                .addResourceLocations(locations.toArray(new String[0]));
    }
}
