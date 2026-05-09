<<<<<<< HEAD
package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AuthorApplyRespDto {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String realName;
    private String idCard;
    private String phone;
    private String reason;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
=======
package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AuthorApplyRespDto {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String realName;
    private String idCard;
    private String phone;
    private String reason;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}