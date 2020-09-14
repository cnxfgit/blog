package com.hlx.vbblog.vo;

import com.hlx.vbblog.model.Article;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;


@ApiModel("文章VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleVO extends Article implements Serializable {
    @ApiModelProperty("文章标签列表")
    @Size(min = 1,message = "请选择至少一个标签")
    private List<Long> tagIdList;
}
