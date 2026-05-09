<<<<<<< HEAD
package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ReadHistoryRespDto {
    private Long id;
    private Long bookId;
    private String bookName;
    private String authorName;
    private String cover;
    private Long chapterId;
    private Integer chapterNum;
    private String chapterName;
    private LocalDateTime readTime;
=======
package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ReadHistoryRespDto {
    private Long id;
    private Long bookId;
    private String bookName;
    private String authorName;
    private String cover;
    private Long chapterId;
    private Integer chapterNum;
    private String chapterName;
    private LocalDateTime readTime;
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}