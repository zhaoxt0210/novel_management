package com.novel.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.novel.entity.Category;
import com.novel.mapper.CategoryMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryNameUpdater {

    private final CategoryMapper categoryMapper;

    private static final Map<String, String> CLASSIC_CATEGORY_MAP = Map.of(
            "玄幻奇幻", "奇幻文学",
            "武侠仙侠", "武侠经典",
            "都市言情", "都市文学",
            "科幻悬疑", "科幻文学",
            "历史军事", "历史文学",
            "古代言情", "古典文学",
            "现代言情", "现代文学",
            "穿越重生", "幻想文学"
    );

    @PostConstruct
    @Transactional
    public void updateCategoryNames() {
        log.info("=== 开始更新分类名称 ===");

        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        List<Category> categories = categoryMapper.selectList(wrapper);

        int updatedCount = 0;
        for (Category category : categories) {
            String oldName = category.getName();
            String newName = CLASSIC_CATEGORY_MAP.get(oldName);

            if (newName != null && !newName.equals(oldName)) {
                log.info("更新分类: {} -> {}", oldName, newName);
                
                LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Category::getId, category.getId())
                        .set(Category::getName, newName);
                categoryMapper.update(null, updateWrapper);
                updatedCount++;
            }
        }

        log.info("=== 分类名称更新完成，共更新 {} 个分类 ===", updatedCount);
    }
}