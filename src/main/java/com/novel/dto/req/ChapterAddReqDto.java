package com.novel.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChapterAddReqDto {
    @NotNull(message = "小说ID不能为空")
    private Long bookId;

    @NotBlank(message = "章节名称不能为空")
    private String chapterName;

    @NotBlank(message = "章节内容不能为空")
    private String content;
}