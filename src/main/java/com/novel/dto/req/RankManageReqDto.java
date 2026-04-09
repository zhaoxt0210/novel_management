package com.novel.dto.req;

import lombok.Data;

@Data
public class RankManageReqDto {
    private Long id;
    private Long bookId;
    private Integer rankType;
    private Integer rankNum;
}
