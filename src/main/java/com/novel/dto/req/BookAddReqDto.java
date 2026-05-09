<<<<<<< HEAD
package com.novel.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookAddReqDto {
    @NotBlank(message = "小说名称不能为空")
    private String bookName;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private String description;
    private String cover;
=======
package com.novel.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookAddReqDto {
    @NotBlank(message = "小说名称不能为空")
    private String bookName;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private String description;
    private String cover;
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}