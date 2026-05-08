package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminLoginRespDto {
    private String token;
    private Long adminId;
    private String username;
    private String realName;
    private Integer role;
}