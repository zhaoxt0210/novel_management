package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class BookshelfRespDto {
    private Long id;
    private Long bookId;
    private String bookName;
    private String authorName;
    private String cover;
    private String description;
    private Integer totalWords;
    private Long favoriteCount;
    private Integer status;
    
    private Long lastReadChapterId;
    private Integer lastReadChapterNum;
    private String lastReadChapterName;
    private Integer readProgress;
    
    private LocalDateTime addTime;
    private LocalDateTime lastReadTime;
}
