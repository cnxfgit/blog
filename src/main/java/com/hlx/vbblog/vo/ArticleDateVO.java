package com.hlx.vbblog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@ApiModel("文章日期统计")
@Data
public class ArticleDateVO implements Serializable {
    @ApiModelProperty("年")
    private Integer year;

    @ApiModelProperty("月")
    private Integer month;

    @ApiModelProperty("日")
    private Integer day;

    @ApiModelProperty("文章数量")
    private Integer articleCount;

    @ApiModelProperty("日期")
    private String date;
}
