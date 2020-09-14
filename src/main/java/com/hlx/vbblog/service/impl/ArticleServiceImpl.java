package com.hlx.vbblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.common.Constant;
import com.hlx.vbblog.common.TableConstant;
import com.hlx.vbblog.dao.ArticleMapper;
import com.hlx.vbblog.dao.ArticleTagMapper;
import com.hlx.vbblog.dao.TagMapper;
import com.hlx.vbblog.dto.ArticleDocument;
import com.hlx.vbblog.model.Article;
import com.hlx.vbblog.model.ArticleTag;
import com.hlx.vbblog.model.Tag;
import com.hlx.vbblog.query.ArchivesQuery;
import com.hlx.vbblog.query.ArticleQuery;
import com.hlx.vbblog.repository.ArticleDocumentRepository;
import com.hlx.vbblog.service.ArticleService;
import com.hlx.vbblog.utils.HighLightUtil;
import com.hlx.vbblog.utils.RedisUtils;
import com.hlx.vbblog.utils.UserInfoUtil;
import com.hlx.vbblog.vo.ArticleDateVO;
import com.hlx.vbblog.vo.AuditVO;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "article")
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ArticleDocumentRepository articleDocumentRepository;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseLikes(Long id) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.select(Article.Table.LIKES).eq(Article.Table.ID, id);
        Article article = articleMapper.selectOne(wrapper);
        article.setId(id);
        article.setLikes(article.getLikes() + 1);
        articleMapper.updateById(article);
    }

    @Override
    @Cacheable
    public Page<Article> listPreviewPageByDate(Integer current, Integer size, ArchivesQuery archivesQuery) {
        Page<Article> articlePage = new Page<>(current, size);
        return articleMapper.listPreviewPageByDate(articlePage);
    }

    @Override
    public List<ArticleDocument> listByKeyword(String keyword) throws IOException {
        SearchRequest searchRequest = new SearchRequest(TableConstant.ARTICLE_DOCUMENT);
        //匹配查询
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, ArticleDocument.Table.TITLE, ArticleDocument.Table.SUMMARY, ArticleDocument.Table.CONTENT);
        TermQueryBuilder termQueryBuilder1 = QueryBuilders.termQuery(ArticleDocument.Table.PUBLISHED, true);
        TermQueryBuilder termQueryBuilder2 = QueryBuilders.termQuery(ArticleDocument.Table.STATUS, Constant.AUDIT_PASS);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(multiMatchQueryBuilder).must(termQueryBuilder1).must(termQueryBuilder2);
        sourceBuilder.query(boolQueryBuilder);
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(ArticleDocument.Table.TITLE).field(ArticleDocument.Table.SUMMARY).field(ArticleDocument.Table.CONTENT);
        highlightBuilder.preTags(Constant.HIGH_LIGHT_PRE_TAGS);
        highlightBuilder.postTags(Constant.HIGH_LIGHT_POST_TAGS);
        sourceBuilder.highlighter(highlightBuilder);
        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //解析结果
        List<ArticleDocument> articleDocuments = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> map = hit.getSourceAsMap();//原来的结果
            //解析高亮的字段
            HighLightUtil.parseField(hit, ArticleDocument.Table.TITLE);
            HighLightUtil.parseField(hit, ArticleDocument.Table.SUMMARY);
            HighLightUtil.parseField(hit, ArticleDocument.Table.CONTENT);

            ArticleDocument articleDocument = new ArticleDocument();
            articleDocument.setId(Long.valueOf((Integer) map.get(ArticleDocument.Table.ID)));
            articleDocument.setTitle((String) map.get(ArticleDocument.Table.TITLE));
            articleDocument.setSummary((String) map.get(ArticleDocument.Table.SUMMARY));
            articleDocument.setContent((String) map.get(ArticleDocument.Table.CONTENT));
            articleDocuments.add(articleDocument);
        }
        return articleDocuments;
    }

    @Override
    @Cacheable
    public List<ArticleDateVO> countByDate(Integer dateFilterType) {
        if (dateFilterType == null) {
            dateFilterType = Constant.FILTER_BY_DAY;
        }
        return articleMapper.countByDate(dateFilterType);
    }

    @Override
    @Cacheable
    public Page<Article> listPreviewPageByTagId(Integer current, Integer size, Long tagId) {
        Page<Article> articlePage = new Page<>(current, size);
        return articleMapper.listPreviewPageByTagId(articlePage, tagId);
    }

    @Override
    @Cacheable
    public Page<Article> listPreviewPageByCategoryId(Integer current, Integer size, Long categoryId) {
        Page<Article> articlePage = new Page<>(current, size);
        return articleMapper.listPreviewPageByCategoryId(articlePage, categoryId);
    }

    @Override
    @Cacheable
    public Article getNextPreviewById(Long id) {
        return articleMapper.selectNextPreviewById(id);
    }

    @Override
    @Cacheable
    public Article getPrevPreviewById(Long id) {
        return articleMapper.selectPrevPreviewById(id);
    }

    @Override
    public Article getDetailById(Long id) {
        //浏览次数加1
        increaseViews(id);
        return articleMapper.selectDetailById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseViews(Long id) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.select(Article.Table.VIEWS).eq(Article.Table.ID, id);
        Article article = articleMapper.selectOne(wrapper);
        article.setId(id);
        article.setViews(article.getViews() + 1);
        articleMapper.updateById(article);
    }

    @Override
    @Cacheable
    public Article getById(Long id) {
        return articleMapper.selectById(id);
    }

    @Override
    @Cacheable
    public long countAll() {
        return articleMapper.selectCount(null);
    }

    @Override
    @Cacheable
    public List<Article> listTop() {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.select(Article.Table.ID, Article.Table.TITLE, Article.Table.SUMMARY, Article.Table.COVER)
                .eq(Article.Table.PUBLISHED, true)
                .eq(Article.Table.STATUS, Constant.AUDIT_PASS)
                .orderByDesc(Article.Table.VIEWS)
                .last(TableConstant.LIMIT + Constant.MAX_TOP_ARTICLES);
        return articleMapper.selectList(wrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdList(List<Long> idList) {
        articleMapper.deleteBatchIds(idList);
        //从ElasticSearch中删除
        ArrayList<ArticleDocument> articleDocuments = new ArrayList<>();
        for (Long id : idList) {
            ArticleDocument articleDocument = new ArticleDocument();
            articleDocument.setId(id);
            articleDocuments.add(articleDocument);
        }
        articleDocumentRepository.deleteAll(articleDocuments);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        articleMapper.deleteById(id);
        //从ElasticSearch中删除
        articleDocumentRepository.deleteById(id);
    }

    @Override
    @Cacheable
    public Page<Article> listPreviewByPage(Integer current, Integer size) {
        Page<Article> articlePage = new Page<>(current, size);
        return articleMapper.listPreviewByPage(articlePage);
    }

    @Override
    @Cacheable
    public List<Article> listUpdate() {
        return articleMapper.listUpdate(Constant.MAX_Update_ARTICLES);
    }

    @Override
    @Cacheable
    public Page<Article> listTableByPage(Integer current, Integer size, ArticleQuery articleQuery) {
        Page<Article> page = new Page<>(current, size);
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(articleQuery.getTitle())) {
            wrapper.like(Article.Table.TITLE, articleQuery.getTitle());
        }
        if (articleQuery.getType() != null) {
            wrapper.eq(Article.Table.TYPE, articleQuery.getType());
        }
        if (articleQuery.getCategoryId() != null) {
            wrapper.eq(Article.Table.CATEGORY_ID, articleQuery.getCategoryId());
        }
        if (articleQuery.getPublished() != null) {
            wrapper.eq(Article.Table.PUBLISHED, articleQuery.getPublished());
        }
        if (articleQuery.getStatus() != null) {
            wrapper.eq(TableConstant.ARTICLE_ALIAS+Article.Table.STATUS, articleQuery.getStatus());
        }
        if (articleQuery.getStartDate() != null && articleQuery.getEndDate() != null) {
            wrapper.between(TableConstant.ARTICLE_ALIAS + Article.Table.CREATE_TIME, articleQuery.getStartDate(), articleQuery.getEndDate());
        }
        return articleMapper.listTableByPage(page, wrapper);
    }

    @Override
    @Cacheable
    public List<Article> listNewest() {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.select(Article.Table.ID, Article.Table.TITLE, Article.Table.SUMMARY, Article.Table.CREATE_TIME)
                .orderByDesc(Article.Table.CREATE_TIME)
                .last(TableConstant.LIMIT + Constant.NEWEST_PAGE_SIZE);
        return articleMapper.selectList(wrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void audit(AuditVO auditVO) {
        Article article = new Article();
        article.setId(auditVO.getId());
        article.setStatus(auditVO.getStatus());
        articleMapper.updateById(article);
        //从ElasticSearch中删除
        articleDocumentRepository.deleteById(article.getId());
        //重新添加
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.select(Article.Table.ID, Article.Table.TITLE, Article.Table.SUMMARY, Article.Table.CONTENT, Article.Table.PUBLISHED, Article.Table.STATUS)
                .eq(Article.Table.ID, auditVO.getId());
        Article art = articleMapper.selectOne(wrapper);
        saveToElasticSearch(art);
    }

    @Override
    public Integer countByThirtyDays() {
        String username = UserInfoUtil.getUsername();
        if (StringUtils.isEmpty(username)) {
            return 0;
        }
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.between(Article.Table.CREATE_TIME, DateUtils.addDays(new Date(),-30), new Date());
        return articleMapper.selectCount(wrapper);
    }

    public void saveToElasticSearch(Article article) {
        ArticleDocument articleDocument = new ArticleDocument();
        BeanUtils.copyProperties(article, articleDocument);
        if (articleDocument.getPublished() == null) {
            QueryWrapper<Article> wrapper = new QueryWrapper<>();
            wrapper.select(Article.Table.PUBLISHED).eq(Article.Table.ID, article.getId());
            Article temp = articleMapper.selectOne(wrapper);
            articleDocument.setPublished(temp.getPublished());
        }
        if (articleDocument.getStatus() == null) {
            QueryWrapper<Article> wrapper = new QueryWrapper<>();
            wrapper.select(Article.Table.STATUS).eq(Article.Table.ID, article.getId());
            Article temp = articleMapper.selectOne(wrapper);
            articleDocument.setStatus(temp.getStatus());
        }
        articleDocumentRepository.save(articleDocument);
    }

    // 添加或者保存文章
    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Article article) {
        //存在新标签则添加新标签
        List<Tag> newTagList = article.getTagList().stream().filter(t -> (t.getId() == null)).collect(Collectors.toList());
        for (Tag newTag : newTagList) {
            //添加标签
            newTag.setColor(Constant.DEFAULT_COLOR);
            newTag.setCreateTime(new Date());
            newTag.setUpdateTime(newTag.getCreateTime());
            tagMapper.insert(newTag);
        }
        if (article.getId() == null) {
            //新增
            articleMapper.insert(article);
        } else {
            //更新
            //更新文章信息
            articleMapper.updateById(article);
            //删除原有标签
            QueryWrapper<ArticleTag> articleTagWrapper = new QueryWrapper<>();
            articleTagWrapper.eq(ArticleTag.Table.ARTICLE_ID, article.getId());
            articleTagMapper.delete(articleTagWrapper);
            //从ElasticSearch中删除
            articleDocumentRepository.deleteById(article.getId());
        }
        //添加新标签
        List<Long> tagIdList = article.getTagList().stream().map(Tag::getId).collect(Collectors.toList());
        articleTagMapper.insertBatch(article.getId(), tagIdList);
        //手动清空标签缓存
        List<String> list = redisUtils.scan("tag*");
        if (!CollectionUtils.isEmpty(list)) {
            String[] keys = new String[list.size()];
            list.toArray(keys);
            redisUtils.del(keys);
        }
        //添加到ElasticSearch中
        saveToElasticSearch(article);
    }

}
