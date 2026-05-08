package com.novel.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.novel.entity.Book;
import com.novel.entity.Chapter;
import com.novel.mapper.BookMapper;
import com.novel.mapper.ChapterMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorldClassicInitializer {

    private final BookMapper bookMapper;
    private final ChapterMapper chapterMapper;

    private static final class ClassicBook {
        String bookName;
        Long categoryId;
        String authorName;
        String description;
        String publicationDate;
        Integer totalWords;
        BigDecimal rating;
        Integer status;
        Long visitCount;
        Long favoriteCount;

        ClassicBook(String bookName, Long categoryId, String authorName, String description,
                    String publicationDate, Integer totalWords, BigDecimal rating,
                    Integer status, Long visitCount, Long favoriteCount) {
            this.bookName = bookName;
            this.categoryId = categoryId;
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

    private static final List<ClassicBook> CLASSIC_BOOKS = List.of(
            new ClassicBook("指环王", 1L, "J.R.R.托尔金", "中土世界的史诗冒险，魔戒远征队对抗黑暗魔君索伦的传奇故事。",
                    "1954-07-29", 450000, new BigDecimal("9.2"), 1, 5000000L, 1200000L),
            new ClassicBook("哈利波特与魔法石", 1L, "J.K.罗琳", "一个普通男孩发现自己是巫师，进入霍格沃茨魔法学校的奇幻旅程。",
                    "1997-06-26", 76000, new BigDecimal("9.0"), 1, 4500000L, 980000L),
            new ClassicBook("纳尼亚传奇", 1L, "C.S.刘易斯", "四个孩子通过魔衣橱进入奇幻世界纳尼亚，与白女巫展开正邪大战。",
                    "1950-10-16", 350000, new BigDecimal("8.8"), 1, 3200000L, 750000L),
            new ClassicBook("冰与火之歌", 1L, "乔治·R·R·马丁", "维斯特洛大陆上九大家族争夺铁王座的史诗奇幻故事。",
                    "1996-08-06", 2980000, new BigDecimal("9.3"), 0, 6800000L, 1500000L),
            new ClassicBook("西游记", 2L, "吴承恩", "唐僧师徒四人西天取经，降妖除魔的神话传奇。",
                    "1592-01-01", 860000, new BigDecimal("9.5"), 1, 8000000L, 2000000L),
            new ClassicBook("三国演义", 2L, "罗贯中", "东汉末年群雄逐鹿，魏蜀吴三国鼎立的历史史诗。",
                    "1494-01-01", 730000, new BigDecimal("9.4"), 1, 7500000L, 1800000L),
            new ClassicBook("水浒传", 2L, "施耐庵", "一百单八将聚义梁山泊，替天行道的英雄传奇。",
                    "1419-01-01", 960000, new BigDecimal("9.3"), 1, 6800000L, 1600000L),
            new ClassicBook("射雕英雄传", 2L, "金庸", "郭靖从懵懂少年成长为一代大侠的武侠传奇。",
                    "1957-01-01", 600000, new BigDecimal("9.2"), 1, 5200000L, 1100000L),
            new ClassicBook("红楼梦", 6L, "曹雪芹", "大观园里的爱恨情仇，封建王朝由盛转衰的悲剧史诗。",
                    "1791-01-01", 730000, new BigDecimal("9.6"), 1, 9000000L, 2500000L),
            new ClassicBook("傲慢与偏见", 7L, "简·奥斯汀", "伊丽莎白与达西的爱情故事，探讨阶级与偏见的经典名著。",
                    "1813-01-28", 130000, new BigDecimal("8.9"), 1, 3500000L, 890000L),
            new ClassicBook("飘", 7L, "玛格丽特·米切尔", "美国南北战争背景下，斯嘉丽的人生起伏与爱情故事。",
                    "1936-06-30", 418000, new BigDecimal("9.1"), 1, 4200000L, 950000L),
            new ClassicBook("乱世佳人", 7L, "玛格丽特·米切尔", "战火纷飞中，一个女人坚韧不拔的成长历程。",
                    "1936-06-30", 418000, new BigDecimal("9.0"), 1, 3800000L, 820000L),
            new ClassicBook("1984", 4L, "乔治·奥威尔", "反乌托邦经典，极权统治下的人性挣扎与反抗。",
                    "1949-06-08", 120000, new BigDecimal("9.4"), 1, 5800000L, 1300000L),
            new ClassicBook("海底两万里", 4L, "儒勒·凡尔纳", "尼摩船长驾驶鹦鹉螺号进行海底探险的科幻之旅。",
                    "1870-01-01", 190000, new BigDecimal("8.7"), 1, 3600000L, 780000L),
            new ClassicBook("三体", 4L, "刘慈欣", "地球文明与三体文明的史诗级碰撞，宇宙社会学的宏大构想。",
                    "2008-01-01", 360000, new BigDecimal("9.5"), 1, 7200000L, 1800000L),
            new ClassicBook("时间机器", 4L, "H.G.威尔斯", "时间旅行者探索未来世界，见证人类文明的兴衰。",
                    "1895-01-01", 52000, new BigDecimal("8.5"), 1, 2800000L, 620000L),
            new ClassicBook("战争与和平", 5L, "列夫·托尔斯泰", "拿破仑入侵俄国时期，四个贵族家庭的命运沉浮。",
                    "1869-01-01", 587000, new BigDecimal("9.3"), 1, 4500000L, 1100000L),
            new ClassicBook("史记", 5L, "司马迁", "中国第一部纪传体通史，记载从黄帝到汉武帝的历史。",
                    "0091-01-01", 526500, new BigDecimal("9.7"), 1, 3200000L, 950000L),
            new ClassicBook("资治通鉴", 5L, "司马光", "编年体通史巨著，涵盖十六朝一千三百多年历史。",
                    "1084-01-01", 3000000, new BigDecimal("9.5"), 1, 2800000L, 780000L),
            new ClassicBook("钢铁是怎样炼成的", 5L, "尼古拉·奥斯特洛夫斯基", "保尔·柯察金在革命熔炉中成长为钢铁战士的故事。",
                    "1933-01-01", 300000, new BigDecimal("8.8"), 1, 3900000L, 860000L),
            new ClassicBook("呼啸山庄", 6L, "艾米莉·勃朗特", "希斯克利夫与凯瑟琳跨越生死的爱恨纠缠。",
                    "1847-12-14", 140000, new BigDecimal("8.9"), 1, 2600000L, 650000L),
            new ClassicBook("茶花女", 6L, "亚历山大·小仲马", "巴黎交际花玛格丽特的爱情悲剧。",
                    "1848-01-01", 90000, new BigDecimal("8.6"), 1, 3100000L, 720000L),
            new ClassicBook("悲惨世界", 3L, "维克多·雨果", "冉·阿让的救赎之路，展现十九世纪法国社会的众生相。",
                    "1862-01-01", 650000, new BigDecimal("9.4"), 1, 5100000L, 1200000L),
            new ClassicBook("巴黎圣母院", 3L, "维克多·雨果", "圣母院敲钟人卡西莫多与吉卜赛少女艾丝美拉达的故事。",
                    "1831-01-16", 300000, new BigDecimal("9.0"), 1, 4200000L, 980000L)
    );

    private static final String[] CHAPTER_TITLES = {
            "第一章 序幕", "第二章 初遇", "第三章 转折", "第四章 危机",
            "第五章 真相", "第六章 抉择", "第七章 高潮", "第八章 结局"
    };

    private static final String[][] CONTENT_TEMPLATES = {
            {
                    "故事始于一个平凡的日子，但命运的齿轮已经开始转动。%s的世界即将迎来翻天覆地的变化。",
                    "当第一缕晨光穿透云层，主人公踏上了未知的旅程。前路漫漫，吉凶未卜。"
            },
            {
                    "命运的安排让两人相遇在那个难忘的时刻。%s的故事，从此翻开了新的篇章。",
                    "初次相见的瞬间，时间仿佛静止。没有人知道，这次相遇将改变一切。"
            },
            {
                    "平静的表面下暗流涌动，一个意外的发现揭开了尘封的秘密。%s的世界开始动摇。",
                    "转折点悄然来临，当真相浮出水面，所有人都将面临艰难的选择。"
            },
            {
                    "危机突如其来，考验着每个人的意志与信念。在%s的关键时刻，谁能挺身而出？",
                    "风暴来临，黑云压城。是屈服还是反抗？答案将决定所有人的命运。"
            },
            {
                    "层层迷雾逐渐散去，真相终于大白于天下。%s的背后，隐藏着怎样的惊人秘密？",
                    "当所有线索串联起来，一个令人震惊的真相展现在世人面前。"
            },
            {
                    "站在人生的十字路口，每个选择都将通向不同的结局。%s将何去何从？",
                    "命运的天平摇摆不定，一个决定可能改变一切。选择，从来都不容易。"
            },
            {
                    "决战时刻终于来临，所有的铺垫在此刻凝聚。%s的高潮即将上演。",
                    "巅峰对决，一触即发。胜者为王，败者为寇，这是最后的较量。"
            },
            {
                    "尘埃落定，故事迎来了结局。但%s的传奇，将永远铭刻在人们心中。",
                    "夕阳西下，一切归于平静。然而，新的故事正在远方悄然开始。"
            }
    };

    @PostConstruct
    @Transactional
    public void initWorldClassics() {
        log.info("=== 开始更新书籍数据为世界名著 ===");

        List<Book> existingBooks = bookMapper.selectList(null);
        log.info("当前数据库中有 {} 本书籍", existingBooks.size());

        LambdaQueryWrapper<Chapter> chapterWrapper = new LambdaQueryWrapper<>();
        long chapterCount = chapterMapper.selectCount(chapterWrapper);
        log.info("当前数据库中有 {} 章内容，将全部清除", chapterCount);

        chapterMapper.delete(null);

        List<Book> booksToUpdate = new ArrayList<>();
        List<Book> booksToInsert = new ArrayList<>();

        int classicIndex = 0;
        Random random = new Random();

        for (Book existingBook : existingBooks) {
            if (classicIndex >= CLASSIC_BOOKS.size()) break;

            ClassicBook classic = CLASSIC_BOOKS.get(classicIndex);
            updateBookFromClassic(existingBook, classic);
            booksToUpdate.add(existingBook);

            generateChapters(existingBook.getId(), classic.bookName, random);
            classicIndex++;
        }

        for (int i = classicIndex; i < CLASSIC_BOOKS.size(); i++) {
            ClassicBook classic = CLASSIC_BOOKS.get(i);
            Book newBook = createBookFromClassic(classic);
            booksToInsert.add(newBook);
        }

        if (!booksToUpdate.isEmpty()) {
            booksToUpdate.forEach(bookMapper::updateById);
            log.info("更新了 {} 本书籍", booksToUpdate.size());
        }

        if (!booksToInsert.isEmpty()) {
            booksToInsert.forEach(bookMapper::insert);
            log.info("插入了 {} 本新书籍", booksToInsert.size());

            for (Book newBook : booksToInsert) {
                generateChapters(newBook.getId(), newBook.getBookName(), random);
            }
        }

        log.info("=== 世界名著数据更新完成 ===");
    }

    private void updateBookFromClassic(Book book, ClassicBook classic) {
        book.setBookName(classic.bookName);
        book.setCategoryId(classic.categoryId);
        book.setAuthorName(classic.authorName);
        book.setDescription(classic.description);
        book.setPublicationDate(LocalDate.parse(classic.publicationDate));
        book.setTotalWords(classic.totalWords);
        book.setRating(classic.rating);
        book.setStatus(classic.status);
        book.setVisitCount(classic.visitCount);
        book.setFavoriteCount(classic.favoriteCount);
        book.setLastChapterId(null);
        book.setLastChapterName(null);
    }

    private Book createBookFromClassic(ClassicBook classic) {
        Book book = new Book();
        book.setBookName(classic.bookName);
        book.setCategoryId(classic.categoryId);
        book.setAuthorId(3L);
        book.setAuthorName(classic.authorName);
        book.setDescription(classic.description);
        book.setPublicationDate(LocalDate.parse(classic.publicationDate));
        book.setTotalWords(classic.totalWords);
        book.setRating(classic.rating);
        book.setStatus(classic.status);
        book.setVisitCount(classic.visitCount);
        book.setFavoriteCount(classic.favoriteCount);
        return book;
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
}