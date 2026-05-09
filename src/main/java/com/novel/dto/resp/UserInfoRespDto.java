package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoRespDto {
    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    private Integer role;
}