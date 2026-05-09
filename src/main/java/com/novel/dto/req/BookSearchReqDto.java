package com.novel.dto.req;

import lombok.Data;

@Data
public class BookSearchReqDto {
    private String keyword;
    private Long categoryId;
    private String sortBy;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}