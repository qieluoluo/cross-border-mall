package com.delmon.controller;

import com.delmon.entity.User;
import com.delmon.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserAvatarController {

    @Autowired
    private UserMapper userMapper;

    private static final String AVATAR_DIR = "F:/study_project/springcloud/Bundaberg/projects/bundbaberg/bundaberg/static/images/avatars/";

    @PostMapping("/avatar/upload")
    public Map<String, Object> uploadAvatar(@RequestParam("file") MultipartFile file, 
                                            @RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        if (file.isEmpty()) {
            result.put("code", 400);
            result.put("message", "请选择图片");
            return result;
        }

        try {
            File dir = new File(AVATAR_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
            String newFilename = UUID.randomUUID().toString() + extension;
            String filePath = AVATAR_DIR + newFilename;

            file.transferTo(new File(filePath));

            String avatarUrl = "/images/avatars/" + newFilename;
            
            User user = new User();
            user.setId(userId);
            user.setAvatar(avatarUrl);
            userMapper.updateById(user);

            result.put("code", 200);
            result.put("message", "上传成功");
            result.put("data", avatarUrl);
            
        } catch (IOException e) {
            result.put("code", 500);
            result.put("message", "上传失败: " + e.getMessage());
        }

        return result;
    }

    @GetMapping("/avatar/{userId}")
    public Map<String, Object> getAvatar(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        User user = userMapper.selectById(userId);
        if (user != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            result.put("code", 200);
            result.put("data", user.getAvatar());
        } else {
            result.put("code", 200);
            result.put("data", "/images/yonghu.jpg");
        }
        
        return result;
    }
}
