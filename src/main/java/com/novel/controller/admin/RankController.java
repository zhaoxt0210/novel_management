package com.novel.controller.admin;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.RankManageReqDto;
import com.novel.dto.resp.RankBookRespDto;
import com.novel.service.RankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "排行榜模块", description = "排行榜管理接口")
@RestController
@RequestMapping("/api/admin/rank")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    @Operation(summary = "获取排行榜列表")
    @GetMapping("/list")
    public RestResp<List<RankBookRespDto>> getRankList(
            @RequestParam(required = false) Integer rankType,
            @RequestParam(defaultValue = "50") Integer limit) {
        return rankService.getRankList(rankType, limit);
    }

    @Operation(summary = "获取所有排行榜类型")
    @GetMapping("/all")
    public RestResp<List<RankBookRespDto>> getAllRankTypes(
            @RequestParam(defaultValue = "10") Integer limit) {
        return rankService.getAllRankTypes(limit);
    }

    @Operation(summary = "添加书籍到排行榜")
    @PostMapping("/add")
    public RestResp<Void> addToRank(@RequestBody RankManageReqDto dto) {
        return rankService.addToRank(dto.getBookId(), dto.getRankType(), dto.getRankNum());
    }

    @Operation(summary = "从排行榜移除")
    @DeleteMapping("/remove")
    public RestResp<Void> removeFromRank(@RequestParam Long bookId, 
                                         @RequestParam(required = false) Integer rankType) {
        return rankService.removeFromRank(bookId, rankType);
    }

    @Operation(summary = "更新排行榜排名")
    @PutMapping("/update")
    public RestResp<Void> updateRankOrder(@RequestBody RankManageReqDto dto) {
        return rankService.updateRankOrder(dto.getBookId(), dto.getRankType(), dto.getRankNum());
    }

    @Operation(summary = "自动生成排行榜")
    @PostMapping("/generate")
    public RestResp<Void> autoGenerateRank(@RequestParam Integer rankType,
                                            @RequestParam(defaultValue = "50") Integer limit) {
        return rankService.autoGenerateRank(rankType, limit);
    }
}
