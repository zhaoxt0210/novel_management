package com.novel.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentAddReqDto {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotNull(message = "小说ID不能为空")
    private Long bookId;
    
    @NotBlank(message = "评论内容不能为空")
    private String content;
}