<<<<<<< HEAD
package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChapterRespDto {
    private Long id;
    private Integer chapterNum;
    private String chapterName;
    private String content;
    private Integer wordCount;
    private Long prevChapterId;
    private Long nextChapterId;
=======
package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChapterRespDto {
    private Long id;
    private Integer chapterNum;
    private String chapterName;
    private String content;
    private Integer wordCount;
    private Long prevChapterId;
    private Long nextChapterId;
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}