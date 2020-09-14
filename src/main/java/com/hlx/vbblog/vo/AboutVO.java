package com.hlx.vbblog.vo;

import com.hlx.vbblog.model.Category;
import com.hlx.vbblog.model.Tag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("关于我页面数据")
@Data
public class AboutVO implements Serializable {
    @ApiModelProperty("文章数量")
    private Long articleCount;

    @ApiModelProperty("分类数量")
    private Long categoryCount;

    @ApiModelProperty("标签数量")
    private Long tagCount;

    @ApiModelProperty("分类列表")
    private List<Category> categories;

    @ApiModelProperty("标签列表")
    private List<Tag> tags;

    @ApiModelProperty("文章日期统计")
    private List<ArticleDateVO> articleDates;

}
