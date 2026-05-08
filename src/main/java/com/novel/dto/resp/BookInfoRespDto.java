package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class BookInfoRespDto {
    private Long id;
    private String bookName;
    private String categoryName;
    private Long categoryId;
    private Long authorId;
    private String authorName;
    private String cover;
    private String description;
    private Integer status;
    private Integer auditStatus;
    private Long visitCount;
    private Long favoriteCount;
    private Integer totalWords;
    private String lastChapterName;
    private Long lastChapterId;
    private LocalDateTime updateTime;
}