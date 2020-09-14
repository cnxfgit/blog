package com.hlx.vbblog.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("文章查询条件")
@Data
public class ArticleQuery implements Serializable {
    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("类型[1:原创, 2:转载]")
    private Integer type;

    @ApiModelProperty("分类ID")
    private Long categoryId;

    @ApiModelProperty("是否发布")
    private Boolean published;

    @ApiModelProperty("审核状态[0:审核未过, 1:等待审核, 2:审核通过]")
    private Integer status;

    @ApiModelProperty("开始创建日期")
    private String startDate;

    @ApiModelProperty("结束创建日期")
    private String endDate;
}
