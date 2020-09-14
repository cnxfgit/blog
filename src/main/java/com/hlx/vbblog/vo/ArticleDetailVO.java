package com.hlx.vbblog.vo;

import com.hlx.vbblog.model.Article;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("文章详情页面数据")
@Data
public class ArticleDetailVO implements Serializable {
    @ApiModelProperty("文章详情")
    private Article article;

    @ApiModelProperty("上一篇文章概览")
    private Article prevPreview;

    @ApiModelProperty("下一篇文章概览")
    private Article nextPreview;
}
