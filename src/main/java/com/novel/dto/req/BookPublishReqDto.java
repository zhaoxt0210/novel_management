package com.novel.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class BookPublishReqDto {

    @NotBlank(message = "书籍标题不能为空")
    @Size(min = 2, max = 50, message = "书籍标题长度必须在2-50个字符之间")
    private String bookName;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Size(max = 500, message = "书籍简介不能超过500个字符")
    private String description;

    private String cover;

    // 发布状态改为非必填，默认为0（连载中）
    private Integer publishStatus = 0;

    @NotEmpty(message = "至少需要添加一个章节")
    @Valid
    private List<ChapterItem> chapters;

    @Data
    public static class ChapterItem {
        @NotBlank(message = "章节标题不能为空")
        @Size(min = 2, max = 30, message = "章节标题长度必须在2-30个字符之间")
        private String chapterName;

        @NotBlank(message = "章节内容不能为空")
        @Size(min = 20, message = "章节内容至少需要20个字符")
        private String content;
    }
}