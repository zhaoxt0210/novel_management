package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.resp.RankBookRespDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RankServiceTest {

    @Autowired
    private RankService rankService;

    @Test
    public void testGetRankList() {
        RestResp<List<RankBookRespDto>> result = rankService.getRankList(1, 10);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    public void testGetRankListWithNullType() {
        RestResp<List<RankBookRespDto>> result = rankService.getRankList(null, 10);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetAllRankTypes() {
        RestResp<List<RankBookRespDto>> result = rankService.getAllRankTypes(5);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
    }

    @Test
    public void testAutoGenerateRank() {
        RestResp<Void> result = rankService.autoGenerateRank(1, 20);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
        
        RestResp<List<RankBookRespDto>> rankList = rankService.getRankList(1, 20);
        assertNotNull(rankList.getData());
    }
}
