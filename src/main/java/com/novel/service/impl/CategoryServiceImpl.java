package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.common.resp.RestResp;
import com.novel.dto.resp.CategoryRespDto;
import com.novel.entity.Category;
import com.novel.mapper.CategoryMapper;
import com.novel.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public RestResp<List<CategoryRespDto>> listCategories() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        List<Category> categories = categoryMapper.selectList(wrapper);
        
        return RestResp.ok(categories.stream().map(category -> 
            CategoryRespDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .workDirection(category.getWorkDirection())
                    .sort(category.getSort())
                    .build()
        ).collect(Collectors.toList()));
    }
}