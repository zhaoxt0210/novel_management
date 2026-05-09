package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.resp.RankBookRespDto;
import java.util.List;

public interface RankService {
    RestResp<List<RankBookRespDto>> getRankList(Integer rankType, Integer limit);
    RestResp<List<RankBookRespDto>> getAllRankTypes(Integer limit);
    RestResp<Void> addToRank(Long bookId, Integer rankType, Integer rankNum);
    RestResp<Void> removeFromRank(Long bookId, Integer rankType);
    RestResp<Void> updateRankOrder(Long bookId, Integer rankType, Integer rankNum);
    RestResp<Void> autoGenerateRank(Integer rankType, Integer limit);
}