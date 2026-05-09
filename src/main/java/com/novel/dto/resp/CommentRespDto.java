package com.novel.dto.resp;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentRespDto {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String content;
    private Integer likeCount;
    private LocalDateTime createTime;
}