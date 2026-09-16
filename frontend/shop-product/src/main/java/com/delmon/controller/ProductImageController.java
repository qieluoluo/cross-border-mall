package com.delmon.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductImageController {

    private static final Logger log = LoggerFactory.getLogger(ProductImageController.class);
    
    private static String resolveImageDir() {
        String userDir = System.getProperty("user.dir");
        String[] candidates = {
                "F:/大三课内/小学期/github/frontend-web/public/images/",
                userDir + "/frontend-web/public/images/",
                userDir + "/src/main/resources/static/images/",
                userDir + "/frontend/shop-product/src/main/resources/static/images/",
                "F:/study_project/springcloud/Bundaberg/projects/bundbaberg/bundaberg/static/images/"
        };
        for (String candidate : candidates) {
            File dir = new File(candidate);
            if (dir.exists() || dir.mkdirs()) {
                String absolute = dir.getAbsolutePath().replace("\\", "/");
                return absolute.endsWith("/") ? absolute : absolute + "/";
            }
        }
        return candidates[0];
    }

    @PostMapping("/image/upload")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        log.info("开始处理图片上传请求");
        
        if (file == null || file.isEmpty()) {
            log.warn("上传的文件为空");
            result.put("code", 400);
            result.put("message", "请选择图片");
            return result;
        }

        try {
            String imageDir = resolveImageDir();
            Path imagePath = Paths.get(imageDir);
            if (!Files.exists(imagePath)) {
                log.info("创建图片存储目录: {}", imageDir);
                Files.createDirectories(imagePath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String newFilename = UUID.randomUUID().toString() + extension;
            File targetFile = new File(imageDir + newFilename);
            
            log.info("保存图片到: {}", targetFile.getAbsolutePath());
            file.transferTo(targetFile);
            
            if (!targetFile.exists()) {
                throw new IOException("文件保存失败，文件不存在");
            }

            String imageUrl = "/images/" + newFilename;
            log.info("图片上传成功，访问路径: {}", imageUrl);
            
            result.put("code", 200);
            result.put("message", "上传成功");
            result.put("data", imageUrl);
            
        } catch (IOException e) {
            log.error("图片上传失败: {}", e.getMessage(), e);
            result.put("code", 500);
            result.put("message", "上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("图片上传发生异常: {}", e.getMessage(), e);
            result.put("code", 500);
            result.put("message", "上传失败: " + e.getMessage());
        }

        return result;
    }
}
