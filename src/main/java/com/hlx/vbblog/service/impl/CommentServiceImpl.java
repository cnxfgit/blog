package com.hlx.vbblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.common.Constant;
import com.hlx.vbblog.common.TableConstant;
import com.hlx.vbblog.dao.ArticleMapper;
import com.hlx.vbblog.dao.CommentMapper;
import com.hlx.vbblog.model.Article;
import com.hlx.vbblog.model.Comment;
import com.hlx.vbblog.model.Message;
import com.hlx.vbblog.query.CommentQuery;
import com.hlx.vbblog.service.CommentService;
import com.hlx.vbblog.utils.LinkedListUtil;
import com.hlx.vbblog.utils.UserInfoUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "comment")
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public void save(Comment comment) {
        commentMapper.insert(comment);
        //评论量+1
        QueryWrapper<Article> articleWrapper = new QueryWrapper<>();
        articleWrapper.select(Article.Table.COMMENTS).eq(Article.Table.ID, comment.getAid());
        Article article = articleMapper.selectOne(articleWrapper);
        article.setComments(article.getComments() + 1);
        article.setId(comment.getAid());
        articleMapper.updateById(article);
    }

    /**
     * 根据评论ID找到对应文章，文章的评论量-1
     *
     * @param id 评论ID
     */
    @Override
    public void decreaseArticleComments(Long id) {
        QueryWrapper<Comment> commentWrapper = new QueryWrapper<>();
        commentWrapper.select(Comment.Table.AID).eq(Comment.Table.ID, id);
        Comment comment = commentMapper.selectOne(commentWrapper);
        QueryWrapper<Article> articleWrapper = new QueryWrapper<>();
        articleWrapper.select(Article.Table.COMMENTS).eq(Article.Table.ID, comment.getAid());
        Article article = articleMapper.selectOne(articleWrapper);
        article.setComments(article.getComments() - 1);
        article.setId(comment.getAid());
        articleMapper.updateById(article);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdList(List<Long> idList) {
        for (Long id : idList) {
            decreaseArticleComments(id);
        }
        commentMapper.deleteBatchIds(idList);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        //评论量-1
        decreaseArticleComments(id);
        commentMapper.deleteById(id);
    }

   @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void reply(Comment comment) {
        commentMapper.insert(comment);
    }

    @Override
    public Integer countByThirtyDays() {
        String username = UserInfoUtil.getUsername();
        if (StringUtils.isEmpty(username)) {
            return 0;
        }
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.between(Comment.Table.CREATE_TIME, DateUtils.addDays(new Date(),-30), new Date());
        return commentMapper.selectCount(wrapper);
    }

    @Override
    @Cacheable
    public List<Comment> listNewest() {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.select(Comment.Table.ID, Comment.Table.NICKNAME, Comment.Table.CONTENT, Comment.Table.CREATE_TIME)
                .orderByDesc(Comment.Table.CREATE_TIME)
                .last(TableConstant.LIMIT + Constant.NEWEST_PAGE_SIZE);
        return commentMapper.selectList(wrapper);
    }

    @Override
    @Cacheable
    public Integer countAll() {
        return commentMapper.selectCount(null);
    }

    @Override
    @Cacheable
    public Page<Comment> listTableByPage(Integer current, Integer size, CommentQuery commentQuery) {
        Page<Comment> page = new Page<>(current, size);
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.select(Comment.Table.ID, Comment.Table.AID, Comment.Table.NICKNAME, Comment.Table.PID,
                Comment.Table.CONTENT, Comment.Table.CREATE_TIME, Comment.Table.REQUEST_IP, Comment.Table.ADDRESS);
        if (!com.hlx.vbblog.utils.StringUtils.isEmpty(commentQuery.getNickname())) {
            wrapper.like(Message.Table.NICKNAME, commentQuery.getNickname());
        }
        if (commentQuery.getStartDate() != null && commentQuery.getEndDate() != null) {
            wrapper.between(Comment.Table.CREATE_TIME, commentQuery.getStartDate(), commentQuery.getEndDate());
        }
        wrapper.orderByDesc(Comment.Table.CREATE_TIME);
        return commentMapper.selectPage(page, wrapper);
    }

    @Override
    @Cacheable
    public Page<Comment> listByArticleId(Long articleId, Integer current, Integer size) {
        Page<Comment> page = new Page<>(current, size);
        Page<Comment> pageInfo = commentMapper.listRootPageByArticleId(page, articleId);
        List<Comment> comments = commentMapper.listByArticleId(articleId);
        LinkedListUtil.toCommentLinkedList(pageInfo.getRecords(), comments);
        return pageInfo;
    }
}
