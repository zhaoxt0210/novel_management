package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginRespDto {
    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private Integer role;
}