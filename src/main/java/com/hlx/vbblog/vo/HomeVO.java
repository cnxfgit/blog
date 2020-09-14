package com.hlx.vbblog.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.model.Article;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("前台主页数据")
@Data
public class HomeVO {
    @ApiModelProperty("置顶文章列表")
    private List<Article> topArticles;  // views最多的文章

    @ApiModelProperty("最新文章列表")
    private List<Article> recommendArticles;

    @ApiModelProperty("文章分页")
    private Page<Article> pageInfo;
}
