package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.resp.CategoryRespDto;
import java.util.List;

public interface CategoryService {
    RestResp<List<CategoryRespDto>> listCategories();
}