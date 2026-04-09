package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.common.resp.RestResp;
import com.novel.dto.resp.RankBookRespDto;
import com.novel.entity.Book;
import com.novel.entity.BookRank;
import com.novel.mapper.BookMapper;
import com.novel.mapper.BookRankMapper;
import com.novel.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private final BookRankMapper bookRankMapper;
    private final BookMapper bookMapper;

    @Override
    public RestResp<List<RankBookRespDto>> getRankList(Integer rankType, Integer limit) {
        LambdaQueryWrapper<BookRank> wrapper = new LambdaQueryWrapper<>();
        if (rankType != null && rankType > 0) {
            wrapper.eq(BookRank::getRankType, rankType);
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
                    .favoriteCount(book.getFavoriteCount())
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
        
        List<Integer> rankTypes = List.of(1, 2, 3, 4, 5);
        for (Integer rankType : rankTypes) {
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
        if (rankType != null && rankType > 0) {
            wrapper.eq(BookRank::getRankType, rankType);
        }
        
        bookRankMapper.delete(wrapper);
        return RestResp.ok();
    }

    @Override
    @Transactional
    public RestResp<Void> updateRankOrder(Long bookId, Integer rankType, Integer rankNum) {
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
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getStatus, 0);
        
        switch (rankType != null ? rankType : 1) {
            case 1:
                wrapper.orderByDesc(Book::getVisitCount);
                break;
            case 2:
                wrapper.orderByDesc(Book::getFavoriteCount);
                break;
            case 3:
                wrapper.orderByDesc(Book::getRating);
                break;
            case 4:
                wrapper.orderByDesc(Book::getUpdateTime);
                break;
            default:
                wrapper.orderByDesc(Book::getVisitCount);
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
}
