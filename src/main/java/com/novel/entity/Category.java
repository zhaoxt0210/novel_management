<<<<<<< HEAD
package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer workDirection;
    private Integer sort;
    private LocalDateTime createTime;
=======
package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer workDirection;
    private Integer sort;
    private LocalDateTime createTime;
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}