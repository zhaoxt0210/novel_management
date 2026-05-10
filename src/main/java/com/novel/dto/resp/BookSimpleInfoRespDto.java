package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class BookSimpleInfoRespDto {
    private Long id;
    private String bookName;
    private Long categoryId;
    private String authorName;
    private Integer status;
    private Integer auditStatus;
    private Long visitCount;
    private Long favoriteCount;
    private Integer totalWords;
    private String lastChapterName;
    private Long lastChapterId;
    private LocalDateTime updateTime;
}