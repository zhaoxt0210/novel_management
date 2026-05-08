package com.novel.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.novel.entity.Book;
import com.novel.entity.Category;
import com.novel.entity.Chapter;
import com.novel.mapper.BookMapper;
import com.novel.mapper.CategoryMapper;
import com.novel.mapper.ChapterMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClassicBookSupplementer {

    private final BookMapper bookMapper;
    private final ChapterMapper chapterMapper;
    private final CategoryMapper categoryMapper;

    private static final class SupplementBook {
        String bookName;
        String authorName;
        String description;
        String publicationDate;
        Integer totalWords;
        BigDecimal rating;
        Integer status;
        Long visitCount;
        Long favoriteCount;

        SupplementBook(String bookName, String authorName, String description,
                      String publicationDate, Integer totalWords, BigDecimal rating,
                      Integer status, Long visitCount, Long favoriteCount) {
            this.bookName = bookName;
            this.authorName = authorName;
            this.description = description;
            this.publicationDate = publicationDate;
            this.totalWords = totalWords;
            this.rating = rating;
            this.status = status;
            this.visitCount = visitCount;
            this.favoriteCount = favoriteCount;
        }
    }

    private static final List<SupplementBook> SUPPLEMENT_BOOKS = List.of(
            new SupplementBook("鲁宾逊漂流记", "丹尼尔·笛福", "一个水手在荒岛独自求生二十八年的传奇故事，展现人类不屈不挠的生存意志。",
                    "1719-04-25", 120000, new BigDecimal("8.7"), 1, 3200000L, 760000L),
            new SupplementBook("格列佛游记", "乔纳森·斯威夫特", "外科医生格列佛在小人国、大人国等地的奇妙历险，讽刺社会现实的经典之作。",
                    "1726-01-28", 102000, new BigDecimal("8.6"), 1, 2800000L, 680000L),
            new SupplementBook("汤姆·索亚历险记", "马克·吐温", "美国小镇男孩汤姆的冒险故事，充满童真与幽默的成长小说。",
                    "1876-04-21", 75000, new BigDecimal("8.5"), 1, 2500000L, 620000L),
            new SupplementBook("金银岛", "罗伯特·路易斯·史蒂文森", "少年吉姆寻找藏宝图的冒险故事，海盗题材的经典之作。",
                    "1883-11-14", 90000, new BigDecimal("8.4"), 1, 2300000L, 580000L),
            new SupplementBook("爱的教育", "埃迪蒙托·德·亚米契斯", "一个意大利小学生的日记，记录师生之情、同学之谊和父母之爱。",
                    "1886-10-18", 85000, new BigDecimal("8.8"), 1, 2100000L, 520000L),
            new SupplementBook("童年", "马克西姆·高尔基", "阿廖沙在外祖父家的成长经历，展现19世纪俄国社会的黑暗与人性之光。",
                    "1913-01-01", 140000, new BigDecimal("8.9"), 1, 2800000L, 650000L),
            new SupplementBook("在人间", "马克西姆·高尔基", "阿廖沙走向社会后的艰辛生活，底层人民的苦难与奋斗。",
                    "1916-01-01", 135000, new BigDecimal("8.8"), 1, 2400000L, 590000L),
            new SupplementBook("我的大学", "马克西姆·高尔基", "阿廖沙在喀山的求学经历，青年知识分子的探索与成长。",
                    "1923-01-01", 125000, new BigDecimal("8.7"), 1, 2200000L, 540000L),
            new SupplementBook("雾都孤儿", "查尔斯·狄更斯", "奥利弗在伦敦底层社会的苦难经历，揭露维多利亚时代的社会黑暗。",
                    "1838-02-19", 160000, new BigDecimal("8.8"), 1, 3100000L, 730000L),
            new SupplementBook("大卫·科波菲尔", "查尔斯·狄更斯", "大卫从童年到成年的人生历程，狄更斯自传体小说的巅峰之作。",
                    "1850-05-01", 340000, new BigDecimal("9.0"), 1, 3500000L, 820000L)
    );

    private static final String[] CHAPTER_TITLES = {
            "第一章 命运的起点", "第二章 踏上旅途", "第三章 意外相遇", "第四章 困境之中",
            "第五章 一线生机", "第六章 真相浮现", "第七章 艰难抉择", "第八章 光明再现"
    };

    private static final String[][] CONTENT_TEMPLATES = {
            {
                    "故事的开始总是平淡无奇，然而命运早已埋下伏笔。%s的故事，要从那个难忘的日子说起。",
                    "一切都从一个平凡的早晨开始，谁也没想到这将是改变命运的一天。"
            },
            {
                    "带着梦想与期待，%s踏上了未知的旅程。前路漫漫，充满了未知的挑战与机遇。",
                    "背上行囊，告别熟悉的一切，%s毅然走向远方，去追寻心中的理想。"
            },
            {
                    "旅途中的一次偶然相遇，改变了%s的人生轨迹。命运的齿轮开始加速转动。",
                    "在最意想不到的时刻，%s遇见了改变一生的人。这次相遇，将带来怎样的改变？"
            },
            {
                    "困难接踵而至，%s陷入了前所未有的困境。是放弃还是坚持？这是一个艰难的选择。",
                    "暴风雨来临，%s必须面对人生中最大的挑战。在逆境中，才能真正看清自己。"
            },
            {
                    "就在最绝望的时刻，一线生机悄然出现。%s抓住了这根救命稻草。",
                    "黑暗中总会有一丝光明，%s终于找到了前进的方向。希望，就在前方。"
            },
            {
                    "层层迷雾逐渐散去，真相终于浮出水面。%s终于明白一切的缘由。",
                    "当所有谜团解开，真相令人震惊。%s面临着人生中最重要的抉择。"
            },
            {
                    "站在人生的十字路口，%s必须做出艰难的选择。每个选择都将通向不同的命运。",
                    "命运的天平摇摆不定，%s的决定将影响整个故事的走向。选择，从来都不容易。"
            },
            {
                    "风雨过后，阳光再次照耀大地。%s的故事迎来了圆满的结局。",
                    "历经千辛万苦，%s终于迎来了属于自己的光明。这段经历，将成为永恒的记忆。"
            }
    };

    @PostConstruct
    @Transactional
    public void supplementClassicBooks() {
        log.info("=== 开始补充世界名著数据 ===");

        List<Book> existingBooks = bookMapper.selectList(null);
        log.info("当前数据库中有 {} 本书籍", existingBooks.size());

        if (existingBooks.size() >= 33) {
            log.info("书籍数量已达到33本，无需补充");
            return;
        }

        List<Category> categories = categoryMapper.selectList(null);
        Map<Long, Long> categoryBookCount = calculateCategoryBookCount(existingBooks);

        List<Long> sortedCategoryIds = categories.stream()
                .sorted(Comparator.comparing(c -> categoryBookCount.getOrDefault(c.getId(), 0L)))
                .map(Category::getId)
                .collect(Collectors.toList());

        log.info("分类书籍数量排序：{}", sortedCategoryIds.stream()
                .map(id -> id + ":" + categoryBookCount.getOrDefault(id, 0L))
                .collect(Collectors.joining(", ")));

        List<Book> booksToInsert = new ArrayList<>();
        Random random = new Random();
        int categoryIndex = 0;

        for (SupplementBook supplement : SUPPLEMENT_BOOKS) {
            Long categoryId = sortedCategoryIds.get(categoryIndex % sortedCategoryIds.size());
            
            Book newBook = new Book();
            newBook.setBookName(supplement.bookName);
            newBook.setCategoryId(categoryId);
            newBook.setAuthorId(3L);
            newBook.setAuthorName(supplement.authorName);
            newBook.setDescription(supplement.description);
            newBook.setPublicationDate(LocalDate.parse(supplement.publicationDate));
            newBook.setTotalWords(supplement.totalWords);
            newBook.setRating(supplement.rating);
            newBook.setStatus(supplement.status);
            newBook.setVisitCount(supplement.visitCount);
            newBook.setFavoriteCount(supplement.favoriteCount);
            
            bookMapper.insert(newBook);
            booksToInsert.add(newBook);

            generateChapters(newBook.getId(), newBook.getBookName(), random);
            categoryIndex++;
        }

        log.info("=== 成功补充 {} 本世界名著 ===", booksToInsert.size());

        verifyData();
    }

    private Map<Long, Long> calculateCategoryBookCount(List<Book> books) {
        Map<Long, Long> countMap = new HashMap<>();
        for (Book book : books) {
            countMap.merge(book.getCategoryId(), 1L, Long::sum);
        }
        return countMap;
    }

    private void generateChapters(Long bookId, String bookName, Random random) {
        int chapterNum = random.nextInt(4) + 5;

        for (int i = 1; i <= chapterNum; i++) {
            Chapter chapter = new Chapter();
            chapter.setBookId(bookId);
            chapter.setChapterNum(i);
            chapter.setChapterName(CHAPTER_TITLES[i - 1]);

            String[] templates = CONTENT_TEMPLATES[i - 1];
            String content = String.format(templates[random.nextInt(templates.length)], bookName);
            chapter.setContent(content);
            chapter.setWordCount(content.length());
            chapter.setStatus(1);

            chapterMapper.insert(chapter);

            if (i == chapterNum) {
                LambdaUpdateWrapper<Book> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(Book::getId, bookId)
                        .set(Book::getLastChapterId, chapter.getId())
                        .set(Book::getLastChapterName, chapter.getChapterName());
                bookMapper.update(null, wrapper);
            }
        }
    }

    private void verifyData() {
        List<Book> allBooks = bookMapper.selectList(null);
        log.info("验证：数据库中共有 {} 本书籍", allBooks.size());

        Map<Long, List<Book>> booksByCategory = allBooks.stream()
                .collect(Collectors.groupingBy(Book::getCategoryId));

        log.info("各分类书籍分布：");
        for (Map.Entry<Long, List<Book>> entry : booksByCategory.entrySet()) {
            Category category = categoryMapper.selectById(entry.getKey());
            String categoryName = category != null ? category.getName() : "未知";
            log.info("  分类[{}] {}: {} 本", entry.getKey(), categoryName, entry.getValue().size());
        }

        List<Category> categories = categoryMapper.selectList(null);
        List<Long> categoriesWithNoBooks = categories.stream()
                .filter(c -> !booksByCategory.containsKey(c.getId()))
                .map(Category::getId)
                .collect(Collectors.toList());

        if (categoriesWithNoBooks.isEmpty()) {
            log.info("验证通过：所有分类都至少有1本书籍");
        } else {
            log.warn("验证警告：以下分类没有书籍: {}", categoriesWithNoBooks);
        }

        if (allBooks.size() == 33) {
            log.info("验证通过：书籍总数达到33本");
        } else {
            log.warn("验证警告：书籍总数为 {} 本，目标为33本", allBooks.size());
        }
    }
}