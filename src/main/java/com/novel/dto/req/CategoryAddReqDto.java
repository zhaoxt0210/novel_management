<<<<<<< HEAD
package com.novel.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryAddReqDto {
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    @NotNull(message = "作品方向不能为空")
    private Integer workDirection;
    
    private Integer sort;
=======
package com.novel.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryAddReqDto {
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    @NotNull(message = "作品方向不能为空")
    private Integer workDirection;
    
    private Integer sort;
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}