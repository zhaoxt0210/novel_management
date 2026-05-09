package com.novel.controller;

import com.novel.common.resp.RestResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "文件上传", description = "文件上传接口")
@RestController
@RequestMapping("/api/upload")
@Slf4j
public class FileUploadController {

    /**
     * 封面图片最大大小：2MB（与前端描述一致）
     */
    private static final long MAX_COVER_SIZE = 2 * 1024 * 1024;

    /**
     * 允许的封面图片格式
     */
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/jpg"
    );

    /**
     * 允许的封面图片扩展名
     */
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png"
    );

    @Operation(summary = "上传封面图片")
    @PostMapping("/cover")
    public RestResp<Map<String, String>> uploadCover(@RequestParam("file") MultipartFile file) {
        // 1. 检查文件是否为空
        if (file == null || file.isEmpty()) {
            return RestResp.error(400, "上传文件不能为空");
        }

        // 2. 检查文件大小（不超过2MB，与前端描述一致）
        if (file.getSize() > MAX_COVER_SIZE) {
            return RestResp.error(400, "封面图片大小不能超过2MB");
        }

        // 3. 检查文件Content-Type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            return RestResp.error(400, "仅支持 JPG、PNG 格式的图片");
        }

        // 4. 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return RestResp.error(400, "文件名不能为空");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return RestResp.error(400, "仅支持 JPG、PNG 格式的图片");
        }

        // 5. 将图片转为Base64存储
        try {
            byte[] bytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            // 拼接data URI前缀，前端可直接用于img标签的src
            String dataUrl = "data:" + contentType + ";base64," + base64;

            Map<String, String> data = new HashMap<>();
            data.put("url", dataUrl);

            log.info("封面图片上传成功，Base64长度: {}", base64.length());
            return RestResp.ok(data);
        } catch (IOException e) {
            log.error("封面上传失败", e);
            return RestResp.error(500, "文件读取失败，请稍后重试");
        }
    }
}
