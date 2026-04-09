package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class RankBookRespDto {
    private Long rankId;
    private Long bookId;
    private String bookName;
    private String authorName;
    private String cover;
    private String description;
    private Integer totalWords;
    private Long visitCount;
    private Long favoriteCount;
    private Integer rating;
    private Integer status;
    private Integer rankNum;
    private Integer rankType;
    private LocalDateTime updateTime;
}
