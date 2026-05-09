package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRespDto {
    private Long id;
    private String name;
    private Integer workDirection;
    private Integer sort;
}