package com.novel.service;

import com.novel.entity.AttachmentFile;
import com.novel.entity.BookCoverBlob;
import com.novel.entity.ChapterContentLob;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 大对象存储服务接口
 * 用于处理LOB（Large Object）数据的存储和检索
 */
public interface LobStorageService {
    
    // ==================== 书籍封面图片操作 ====================
    
    /**
     * 保存书籍封面图片
     *
     * @param bookId 书籍ID
     * @param file 图片文件
     * @return 保存后的封面信息
     */
    BookCoverBlob saveBookCover(Long bookId, MultipartFile file) throws IOException;
    
    /**
     * 获取书籍封面图片数据
     *
     * @param bookId 书籍ID
     * @return 封面图片二进制数据
     */
    byte[] getBookCoverData(Long bookId);
    
    /**
     * 删除书籍封面图片
     *
     * @param bookId 书籍ID
     * @return 是否删除成功
     */
    boolean deleteBookCover(Long bookId);
    
    // ==================== 章节内容操作 ====================
    
    /**
     * 保存章节内容
     *
     * @param chapterId 章节ID
     * @param content 章节内容
     * @return 保存后的内容信息
     */
    ChapterContentLob saveChapterContent(Long chapterId, String content);
    
    /**
     * 获取章节内容
     *
     * @param chapterId 章节ID
     * @return 章节内容
     */
    String getChapterContent(Long chapterId);
    
    /**
     * 更新章节内容
     *
     * @param chapterId 章节ID
     * @param content 新内容
     * @return 更新后的内容信息
     */
    ChapterContentLob updateChapterContent(Long chapterId, String content);
    
    /**
     * 删除章节内容
     *
     * @param chapterId 章节ID
     * @return 是否删除成功
     */
    boolean deleteChapterContent(Long chapterId);
    
    // ==================== 附件文件操作 ====================
    
    /**
     * 上传附件文件
     *
     * @param file 文件
     * @param refType 关联类型
     * @param refId 关联ID
     * @param userId 上传用户ID
     * @return 保存后的文件信息
     */
    AttachmentFile uploadAttachment(MultipartFile file, String refType, Long refId, Long userId) throws IOException;
    
    /**
     * 获取附件文件数据
     *
     * @param fileId 文件ID
     * @return 文件二进制数据
     */
    byte[] getAttachmentData(Long fileId);
    
    /**
     * 获取附件信息
     *
     * @param fileId 文件ID
     * @return 文件信息
     */
    AttachmentFile getAttachmentInfo(Long fileId);
    
    /**
     * 获取关联的所有附件
     *
     * @param refType 关联类型
     * @param refId 关联ID
     * @return 附件列表
     */
    List<AttachmentFile> getAttachmentsByRef(String refType, Long refId);
    
    /**
     * 删除附件
     *
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    boolean deleteAttachment(Long fileId);
    
    /**
     * 增加下载次数
     *
     * @param fileId 文件ID
     */
    void incrementDownloadCount(Long fileId);
    
    // ==================== 文件校验操作 ====================
    
    /**
     * 计算文件MD5哈希值
     *
     * @param data 文件数据
     * @return MD5哈希值
     */
    String calculateFileHash(byte[] data);
    
    /**
     * 检查文件是否已存在（通过MD5）
     *
     * @param fileHash 文件MD5
     * @return 是否已存在
     */
    boolean isFileExists(String fileHash);
    
    /**
     * 获取文件大小描述
     *
     * @param size 文件大小（字节）
     * @return 格式化的大小描述
     */
    String formatFileSize(Long size);
}
