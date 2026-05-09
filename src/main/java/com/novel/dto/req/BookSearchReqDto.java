<<<<<<< HEAD
package com.novel.dto.req;

import lombok.Data;

@Data
public class BookSearchReqDto {
    private String keyword;
    private Long categoryId;
    private String sortBy;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
=======
package com.novel.dto.req;

import lombok.Data;

@Data
public class BookSearchReqDto {
    private String keyword;
    private Long categoryId;
    private String sortBy;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}