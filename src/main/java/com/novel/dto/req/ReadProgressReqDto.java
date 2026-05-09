package com.novel.dto.req;

import lombok.Data;

@Data
public class ReadProgressReqDto {
    private Long userId;
    private Long bookId;
    private Long chapterId;
    private Integer chapterNum;
    private String chapterName;
    private Integer readProgress;
}
