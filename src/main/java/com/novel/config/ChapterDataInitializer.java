package com.novel.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.entity.Book;
import com.novel.entity.Chapter;
import com.novel.mapper.BookMapper;
import com.novel.mapper.ChapterMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChapterDataInitializer {

    private final BookMapper bookMapper;
    private final ChapterMapper chapterMapper;

    private static final String[] CHAPTER_TITLES = {
            "第一章 初入江湖",
            "第二章 神秘奇遇",
            "第三章 暗流涌动",
            "第四章 危机四伏",
            "第五章 绝处逢生",
            "第六章 真相大白",
            "第七章 新的征程",
            "第八章 生死对决",
            "第九章 巅峰之路",
            "第十章 尘埃落定"
    };

    private static final String[] CONTENT_TEMPLATES = {
            "夕阳的余晖洒落在古老的街道上，%s独自漫步在这陌生的城市中。他不知道前方等待着他的是什么，但命运的齿轮已经开始转动。\n\n街道两旁的店铺灯火通明，叫卖声此起彼伏。他穿过人群，目光在各种摊位间游移，似乎在寻找着什么。",
            "就在这时，一阵奇异的光芒从街角的小巷中传出。%s好奇心起，顺着光芒走去。只见一位白发老者坐在石凳上，手中捧着一本泛黄的古籍。\n\n\"年轻人，你来了。\"老者缓缓开口，声音仿佛穿越了千年时光。",
            "老者将古籍递给%s，眼中闪烁着神秘的光芒。\"这本书记载着一段被遗忘的历史，只有有缘人才能开启其中的秘密。\" %s接过书，只觉得书页间似乎有某种力量在涌动。",
            "离开老者后，%s打开古籍。书页上的文字开始发出淡淡的光芒，一幅幅画面在他眼前浮现。他看到了远古的战场，看到了神秘的遗迹，看到了自己从未想象过的世界。\n\n就在他沉浸其中时，危险正在悄然逼近。",
            "一群黑衣人突然从暗处涌出，将%s团团围住。\"把书交出来！\"为首的黑衣人冷冷说道。%s握紧手中的古籍，知道一场恶战在所难免。\n\n剑光闪烁，人影交错。%s凭借着过人的身手，勉强抵挡着敌人的攻击。",
            "激战中，%s发现这些黑衣人竟然是冲着古籍而来。他意识到这本书远比自己想象的更加重要。就在危急关头，一道身影从天而降，挡在了%ss身前。\n\n\"你是谁？\"%s惊讶地问道。来人微微一笑：\"我是来帮你的。\"",
            "在神秘人的帮助下，黑衣人被击退。神秘人告诉%s，古籍中隐藏着一个巨大的秘密，关系到整个大陆的命运。\n\n\"现在，你必须做出选择。\"神秘人说道，\"是选择平凡的生活，还是踏上这条充满危险的道路？\"",
            "%s毅然选择了后者。他知道，从接过那本书的那一刻起，自己的命运就已经改变。\n\n踏上新的征程，%s开始了一段惊心动魄的冒险之旅。他将面对强大的敌人，解开古老的谜题，最终揭开隐藏在历史背后的真相。",
            "历经无数艰难险阻，%s终于来到了传说中的圣地。在这里，他将面对最终的挑战。\n\n强大的敌人出现在面前，%s握紧手中的武器，眼中闪烁着坚定的光芒。这是一场决定命运的战斗，只有战胜对手，才能守护自己所珍视的一切。",
            "决战结束，尘埃落定。%s站在废墟之上，望着远方的朝阳。他完成了使命，守护了大陆的和平。\n\n然而，这并不是结束，而是新的开始。%s知道，在这片广袤的大陆上，还有无数的故事等待着被书写，无数的冒险等待着被开启。"
    };

    @PostConstruct
    @Transactional
    public void initChapterData() {
        log.info("=== 开始检查并生成章节数据 ===");
        
        LambdaQueryWrapper<Book> bookWrapper = new LambdaQueryWrapper<>();
        bookWrapper.eq(Book::getStatus, 0);
        List<Book> books = bookMapper.selectList(bookWrapper);
        
        log.info("共找到 {} 本连载中的书籍", books.size());
        
        Random random = new Random();
        
        for (Book book : books) {
            LambdaQueryWrapper<Chapter> chapterWrapper = new LambdaQueryWrapper<>();
            chapterWrapper.eq(Chapter::getBookId, book.getId());
            long chapterCount = chapterMapper.selectCount(chapterWrapper);
            
            if (chapterCount > 0) {
                log.info("书籍 [{}] 已有 {} 章，跳过", book.getBookName(), chapterCount);
                continue;
            }
            
            log.info("书籍 [{}] 暂无章节，开始生成...", book.getBookName());
            
            int chapterNum = random.nextInt(5) + 6;
            for (int i = 1; i <= chapterNum; i++) {
                Chapter chapter = new Chapter();
                chapter.setBookId(book.getId());
                chapter.setChapterNum(i);
                chapter.setChapterName(CHAPTER_TITLES[i - 1]);
                
                String content = String.format(CONTENT_TEMPLATES[i - 1], 
                        book.getBookName(), book.getBookName(), book.getBookName(),
                        book.getBookName(), book.getBookName(), book.getBookName(),
                        book.getBookName(), book.getBookName());
                chapter.setContent(content);
                chapter.setWordCount(content.length());
                chapter.setStatus(1);
                
                chapterMapper.insert(chapter);
                
                if (i == chapterNum) {
                    book.setLastChapterId(chapter.getId());
                    book.setLastChapterName(chapter.getChapterName());
                    bookMapper.updateById(book);
                }
            }
            
            log.info("书籍 [{}] 生成完成，共 {} 章", book.getBookName(), chapterNum);
        }
        
        log.info("=== 章节数据检查和生成完成 ===");
    }
}