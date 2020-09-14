package com.hlx.vbblog.repository;

import com.hlx.vbblog.dto.ArticleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleDocumentRepository extends ElasticsearchCrudRepository<ArticleDocument, Long> {

    List<ArticleDocument> findDistinctByTitleLikeOrSummaryLikeOrContentLike(String title, String summary, String content);
}
