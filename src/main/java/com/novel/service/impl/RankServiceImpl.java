package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.common.resp.RestResp;
import com.novel.dto.resp.RankBookRespDto;
import com.novel.entity.Book;
import com.novel.entity.BookRank;
import com.novel.mapper.BookFavoriteMapper;
import com.novel.mapper.BookMapper;
import com.novel.mapper.BookRankMapper;
import com.novel.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private static final List<Integer> SUPPORTED_RANK_TYPES = Arrays.asList(1, 2);

    private final BookRankMapper bookRankMapper;
    private final BookMapper bookMapper;
    private final BookFavoriteMapper bookFavoriteMapper;

    @Override
    public RestResp<List<RankBookRespDto>> getRankList(Integer rankType, Integer limit) {
        LambdaQueryWrapper<BookRank> wrapper = new LambdaQueryWrapper<>();
        if (rankType != null) {
            if (!isSupportedRankType(rankType)) {
                return RestResp.error("仅支持点击榜和收藏榜");
            }
            wrapper.eq(BookRank::getRankType, rankType);
        } else {
            wrapper.in(BookRank::getRankType, SUPPORTED_RANK_TYPES);
        }
        wrapper.orderByAsc(BookRank::getRankNum);
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        
        List<BookRank> ranks = bookRankMapper.selectList(wrapper);
        
        if (ranks.isEmpty()) {
            return RestResp.ok(new ArrayList<>());
        }
        
        List<Long> bookIds = ranks.stream()
                .map(BookRank::getBookId)
                .collect(Collectors.toList());
        
        List<Book> books = bookMapper.selectBatchIds(bookIds);
        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        Map<Long, Long> favoriteCountMap = queryFavoriteCountMap(bookIds);
        
        return RestResp.ok(ranks.stream().map(rank -> {
            Book book = bookMap.get(rank.getBookId());
            if (book == null) return null;
            
            return RankBookRespDto.builder()
                    .rankId(rank.getId())
                    .bookId(book.getId())
                    .bookName(book.getBookName())
                    .authorName(book.getAuthorName())
                    .cover(book.getCover())
                    .description(book.getDescription())
                    .totalWords(book.getTotalWords())
                    .visitCount(book.getVisitCount())
                    .favoriteCount(favoriteCountMap.getOrDefault(book.getId(), 0L))
                    .rating(book.getRating() != null ? book.getRating().intValue() : null)
                    .status(book.getStatus())
                    .rankNum(rank.getRankNum())
                    .rankType(rank.getRankType())
                    .updateTime(rank.getUpdateTime())
                    .build();
        }).filter(item -> item != null).collect(Collectors.toList()));
    }

    @Override
    public RestResp<List<RankBookRespDto>> getAllRankTypes(Integer limit) {
        List<RankBookRespDto> allRanks = new ArrayList<>();

        for (Integer rankType : SUPPORTED_RANK_TYPES) {
            RestResp<List<RankBookRespDto>> result = getRankList(rankType, limit);
            if (result.getCode() == 200 && result.getData() != null) {
                allRanks.addAll(result.getData());
            }
        }
        
        return RestResp.ok(allRanks);
    }

    @Override
    @Transactional
    public RestResp<Void> addToRank(Long bookId, Integer rankType, Integer rankNum) {
        if (!isSupportedRankType(rankType)) {
            return RestResp.error("仅支持点击榜和收藏榜");
        }

        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            return RestResp.error("书籍不存在");
        }
        
        LambdaQueryWrapper<BookRank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookRank::getBookId, bookId)
                .eq(BookRank::getRankType, rankType);
        
        if (bookRankMapper.selectCount(wrapper) > 0) {
            return RestResp.error("该书籍已在排行榜中");
        }
        
        BookRank bookRank = new BookRank();
        bookRank.setBookId(bookId);
        bookRank.setRankType(rankType);
        bookRank.setRankNum(rankNum);
        bookRank.setCreateTime(LocalDateTime.now());
        bookRank.setUpdateTime(LocalDateTime.now());
        
        bookRankMapper.insert(bookRank);
        return RestResp.ok();
    }

    @Override
    @Transactional
    public RestResp<Void> removeFromRank(Long bookId, Integer rankType) {
        LambdaQueryWrapper<BookRank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookRank::getBookId, bookId);
        if (rankType != null) {
            if (!isSupportedRankType(rankType)) {
                return RestResp.error("仅支持点击榜和收藏榜");
            }
            wrapper.eq(BookRank::getRankType, rankType);
        } else {
            wrapper.in(BookRank::getRankType, SUPPORTED_RANK_TYPES);
        }
        
        bookRankMapper.delete(wrapper);
        return RestResp.ok();
    }

    @Override
    @Transactional
    public RestResp<Void> updateRankOrder(Long bookId, Integer rankType, Integer rankNum) {
        if (!isSupportedRankType(rankType)) {
            return RestResp.error("仅支持点击榜和收藏榜");
        }

        LambdaQueryWrapper<BookRank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookRank::getBookId, bookId)
                .eq(BookRank::getRankType, rankType);
        
        BookRank bookRank = bookRankMapper.selectOne(wrapper);
        if (bookRank == null) {
            return RestResp.error("该书籍不在排行榜中");
        }
        
        bookRank.setRankNum(rankNum);
        bookRank.setUpdateTime(LocalDateTime.now());
        bookRankMapper.updateById(bookRank);
        
        return RestResp.ok();
    }

    @Override
    @Transactional
    public RestResp<Void> autoGenerateRank(Integer rankType, Integer limit) {
        if (!isSupportedRankType(rankType)) {
            return RestResp.error("仅支持点击榜和收藏榜");
        }

        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getStatus, 0);

        if (rankType == 1) {
            wrapper.orderByDesc(Book::getVisitCount);
        } else {
            wrapper.orderByDesc(Book::getFavoriteCount);
        }
        
        wrapper.last("LIMIT " + (limit != null ? limit : 50));
        List<Book> books = bookMapper.selectList(wrapper);
        
        bookRankMapper.delete(
            new LambdaQueryWrapper<BookRank>().eq(BookRank::getRankType, rankType)
        );
        
        int rankNum = 1;
        for (Book book : books) {
            BookRank bookRank = new BookRank();
            bookRank.setBookId(book.getId());
            bookRank.setRankType(rankType);
            bookRank.setRankNum(rankNum++);
            bookRank.setCreateTime(LocalDateTime.now());
            bookRank.setUpdateTime(LocalDateTime.now());
            bookRankMapper.insert(bookRank);
        }
        
        return RestResp.ok();
    }

    private boolean isSupportedRankType(Integer rankType) {
        return rankType != null && SUPPORTED_RANK_TYPES.contains(rankType);
    }

    private Map<Long, Long> queryFavoriteCountMap(List<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            List<Map<String, Object>> countRows = bookFavoriteMapper.countByBookIds(bookIds);
            if (countRows == null || countRows.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<Long, Long> favoriteCountMap = new HashMap<>();
            for (Map<String, Object> row : countRows) {
                if (row == null) {
                    continue;
                }
                Long bookId = parseLong(row.get("bookId"));
                Long favoriteCount = parseLong(row.get("favoriteCount"));
                if (bookId != null) {
                    favoriteCountMap.put(bookId, favoriteCount != null ? favoriteCount : 0L);
                }
            }
            return favoriteCountMap;
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }

    private Long parseLong(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
