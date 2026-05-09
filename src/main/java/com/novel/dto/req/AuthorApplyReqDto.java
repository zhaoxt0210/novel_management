package com.novel.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorApplyReqDto {
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    private String reason;
}