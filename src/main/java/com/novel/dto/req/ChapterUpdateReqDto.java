package com.novel.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChapterUpdateReqDto {
    @NotNull(message = "章节ID不能为空")
    private Long chapterId;
    
    @NotBlank(message = "章节内容不能为空")
    private String content;
}