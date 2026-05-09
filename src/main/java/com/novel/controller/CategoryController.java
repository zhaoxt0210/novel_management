<<<<<<< HEAD
package com.novel.controller;

import com.novel.common.resp.RestResp;
import com.novel.dto.resp.CategoryRespDto;
import com.novel.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "分类模块", description = "小说分类接口")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取所有分类")
    @GetMapping("/list")
    public RestResp<List<CategoryRespDto>> listCategories() {
        return categoryService.listCategories();
    }
=======
package com.novel.controller;

import com.novel.common.resp.RestResp;
import com.novel.dto.resp.CategoryRespDto;
import com.novel.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "分类模块", description = "小说分类接口")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取所有分类")
    @GetMapping("/list")
    public RestResp<List<CategoryRespDto>> listCategories() {
        return categoryService.listCategories();
    }
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}