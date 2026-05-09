package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.entity.AttachmentFile;
import com.novel.entity.BookCoverBlob;
import com.novel.entity.ChapterContentLob;
import com.novel.mapper.BookCoverBlobMapper;
import com.novel.mapper.ChapterContentLobMapper;
import com.novel.mapper.AttachmentFileMapper;
import com.novel.service.LobStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 大对象存储服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LobStorageServiceImpl implements LobStorageService {
    
    private final BookCoverBlobMapper bookCoverBlobMapper;
    private final ChapterContentLobMapper chapterContentLobMapper;
    private final AttachmentFileMapper attachmentFileMapper;
    
    // 支持的图片类型
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/webp", "image/gif"};
    // 最大图片大小 5MB
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    // 最大附件大小 50MB
    private static final long MAX_ATTACHMENT_SIZE = 50 * 1024 * 1024;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookCoverBlob saveBookCover(Long bookId, MultipartFile file) throws IOException {
        // 校验文件
        validateImageFile(file);
        
        // 读取图片数据
        byte[] imageData = file.getBytes();
        
        // 获取图片尺寸
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        int width = image != null ? image.getWidth() : 0;
        int height = image != null ? image.getHeight() : 0;
        
        // 查询是否已存在封面
        LambdaQueryWrapper<BookCoverBlob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookCoverBlob::getBookId, bookId);
        BookCoverBlob existingCover = bookCoverBlobMapper.selectOne(wrapper);
        
        BookCoverBlob cover = new BookCoverBlob();
        cover.setBookId(bookId);
        cover.setCoverData(imageData);
        cover.setCoverType(file.getContentType());
        cover.setFileSize(imageData.length);
        cover.setWidth(width);
        cover.setHeight(height);
        
        if (existingCover != null) {
            cover.setId(existingCover.getId());
            bookCoverBlobMapper.updateById(cover);
            log.info("Updated book cover for bookId: {}", bookId);
        } else {
            bookCoverBlobMapper.insert(cover);
            log.info("Saved new book cover for bookId: {}", bookId);
        }
        
        return cover;
    }
    
    @Override
    public byte[] getBookCoverData(Long bookId) {
        LambdaQueryWrapper<BookCoverBlob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookCoverBlob::getBookId, bookId);
        BookCoverBlob cover = bookCoverBlobMapper.selectOne(wrapper);
        return cover != null ? cover.getCoverData() : null;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBookCover(Long bookId) {
        LambdaQueryWrapper<BookCoverBlob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookCoverBlob::getBookId, bookId);
        int result = bookCoverBlobMapper.delete(wrapper);
        log.info("Deleted book cover for bookId: {}, result: {}", bookId, result);
        return result > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChapterContentLob saveChapterContent(Long chapterId, String content) {
        // 计算字数
        int wordCount = countWords(content);
        int contentLength = content.length();
        
        ChapterContentLob contentLob = new ChapterContentLob();
        contentLob.setChapterId(chapterId);
        contentLob.setContent(content);
        contentLob.setContentLength(contentLength);
        contentLob.setWordCount(wordCount);
        
        chapterContentLobMapper.insert(contentLob);
        log.info("Saved chapter content for chapterId: {}, wordCount: {}", chapterId, wordCount);
        
        return contentLob;
    }
    
    @Override
    public String getChapterContent(Long chapterId) {
        LambdaQueryWrapper<ChapterContentLob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChapterContentLob::getChapterId, chapterId);
        ChapterContentLob contentLob = chapterContentLobMapper.selectOne(wrapper);
        return contentLob != null ? contentLob.getContent() : "";
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChapterContentLob updateChapterContent(Long chapterId, String content) {
        LambdaQueryWrapper<ChapterContentLob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChapterContentLob::getChapterId, chapterId);
        ChapterContentLob existingContent = chapterContentLobMapper.selectOne(wrapper);
        
        int wordCount = countWords(content);
        int contentLength = content.length();
        
        ChapterContentLob contentLob = new ChapterContentLob();
        contentLob.setChapterId(chapterId);
        contentLob.setContent(content);
        contentLob.setContentLength(contentLength);
        contentLob.setWordCount(wordCount);
        
        if (existingContent != null) {
            contentLob.setId(existingContent.getId());
            chapterContentLobMapper.updateById(contentLob);
            log.info("Updated chapter content for chapterId: {}", chapterId);
        } else {
            chapterContentLobMapper.insert(contentLob);
            log.info("Saved new chapter content for chapterId: {}", chapterId);
        }
        
        return contentLob;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteChapterContent(Long chapterId) {
        LambdaQueryWrapper<ChapterContentLob> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChapterContentLob::getChapterId, chapterId);
        int result = chapterContentLobMapper.delete(wrapper);
        log.info("Deleted chapter content for chapterId: {}, result: {}", chapterId, result);
        return result > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttachmentFile uploadAttachment(MultipartFile file, String refType, Long refId, Long userId) throws IOException {
        // 校验文件
        if (file.getSize() > MAX_ATTACHMENT_SIZE) {
            throw new IllegalArgumentException("文件大小超过限制（最大50MB）");
        }
        
        byte[] fileData = file.getBytes();
        String fileHash = calculateFileHash(fileData);
        
        // 检查文件是否已存在
        LambdaQueryWrapper<AttachmentFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttachmentFile::getFileHash, fileHash);
        AttachmentFile existingFile = attachmentFileMapper.selectOne(wrapper);
        
        if (existingFile != null) {
            log.info("File already exists with hash: {}", fileHash);
            return existingFile;
        }
        
        AttachmentFile attachment = new AttachmentFile();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setFileData(fileData);
        attachment.setFileHash(fileHash);
        attachment.setRefType(refType);
        attachment.setRefId(refId);
        attachment.setUploadUserId(userId);
        attachment.setStorageType(1); // 数据库存储
        attachment.setDownloadCount(0);
        
        attachmentFileMapper.insert(attachment);
        log.info("Uploaded attachment: {}, size: {}", file.getOriginalFilename(), formatFileSize(file.getSize()));
        
        return attachment;
    }
    
    @Override
    public byte[] getAttachmentData(Long fileId) {
        AttachmentFile attachment = attachmentFileMapper.selectById(fileId);
        if (attachment != null) {
            incrementDownloadCount(fileId);
            return attachment.getFileData();
        }
        return null;
    }
    
    @Override
    public AttachmentFile getAttachmentInfo(Long fileId) {
        return attachmentFileMapper.selectById(fileId);
    }
    
    @Override
    public List<AttachmentFile> getAttachmentsByRef(String refType, Long refId) {
        LambdaQueryWrapper<AttachmentFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttachmentFile::getRefType, refType)
               .eq(AttachmentFile::getRefId, refId)
               .orderByDesc(AttachmentFile::getCreateTime);
        return attachmentFileMapper.selectList(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAttachment(Long fileId) {
        int result = attachmentFileMapper.deleteById(fileId);
        log.info("Deleted attachment: {}, result: {}", fileId, result);
        return result > 0;
    }
    
    @Override
    public void incrementDownloadCount(Long fileId) {
        AttachmentFile attachment = attachmentFileMapper.selectById(fileId);
        if (attachment != null) {
            attachment.setDownloadCount(attachment.getDownloadCount() + 1);
            attachmentFileMapper.updateById(attachment);
        }
    }
    
    @Override
    public String calculateFileHash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(data);
            BigInteger bigInt = new BigInteger(1, hashBytes);
            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to calculate file hash", e);
            return "";
        }
    }
    
    @Override
    public boolean isFileExists(String fileHash) {
        LambdaQueryWrapper<AttachmentFile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttachmentFile::getFileHash, fileHash);
        return attachmentFileMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public String formatFileSize(Long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
    
    /**
     * 校验图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("图片文件不能为空");
        }
        
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("图片大小超过限制（最大5MB）");
        }
        
        String contentType = file.getContentType();
        boolean isValidType = false;
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equals(contentType)) {
                isValidType = true;
                break;
            }
        }
        
        if (!isValidType) {
            throw new IllegalArgumentException("不支持的图片类型，仅支持: JPEG, PNG, WEBP, GIF");
        }
    }
    
    /**
     * 统计中文字数（去除标点符号和空白）
     */
    private int countWords(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        
        // 去除HTML标签
        String text = content.replaceAll("<[^>]+>", "");
        
        // 匹配中文字符
        Pattern chinesePattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher matcher = chinesePattern.matcher(text);
        
        int chineseCount = 0;
        while (matcher.find()) {
            chineseCount++;
        }
        
        // 匹配英文单词
        Pattern englishPattern = Pattern.compile("[a-zA-Z]+");
        Matcher englishMatcher = englishPattern.matcher(text);
        
        int englishCount = 0;
        while (englishMatcher.find()) {
            englishCount++;
        }
        
        return chineseCount + englishCount;
    }
}
