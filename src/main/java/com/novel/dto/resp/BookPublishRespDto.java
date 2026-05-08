package com.novel.dto.resp;

import lombok.Data;

@Data
public class BookPublishRespDto {
    private Long bookId;
    private String bookName;
    private Integer chapterCount;

    public BookPublishRespDto(Long bookId, String bookName, Integer chapterCount) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.chapterCount = chapterCount;
    }
}