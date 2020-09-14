package com.hlx.vbblog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文章
 **/
@ApiModel("文章")
@Data
@TableName("t_article")
@JsonIgnoreProperties("handler")
public class Article implements Serializable {
    @ApiModelProperty("主键:ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("标题")
    @NotBlank(message = "文章标题不能为空")
    @Length(max = 100, message = "文章标题长度不能超过100")
    private String title;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("HTML内容")
    @NotBlank(message = "文章内容不能为空")
    private String content;

    @ApiModelProperty("markdown内容")
    private String textContent;

    @ApiModelProperty("封面URL")
    private String cover;

    @ApiModelProperty("浏览量")
    private Integer views;

    @ApiModelProperty("点赞量")
    private Integer likes;

    @ApiModelProperty("评论量")
    private Integer comments;

    @ApiModelProperty("是否发布")
    private Boolean published;

    @ApiModelProperty("外键:作者ID")
    private Long authorId;

    @ApiModelProperty("外键:分类ID")
    @NotNull(message = "请选择一个分类")
    private Long categoryId;

    @ApiModelProperty("类型[1:原创, 2:转载]")
    private Integer type;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("审核状态[0:审核未过, 2:等待审核, 3:审核通过]")
    private Integer status;

    @ApiModelProperty("分类")
    @TableField(exist = false)
    private Category category;

    @ApiModelProperty("标签ID列表")
    @TableField(exist = false)
    private List<Tag> tagList;

    @ApiModelProperty("作者")
    @TableField(exist = false)
    private User author;

    public interface Table {
        String ID = "id";
        String TITLE = "title";
        String SUMMARY = "summary";
        String CONTENT = "content";
        String TEXT_CONTENT = "text_content";
        String COVER = "cover";
        String TYPE = "type";
        String VIEWS = "views";
        String LIKES = "likes";
        String COMMENTS = "comments";
        String PUBLISHED = "published";
        String AUTHOR_ID = "author_id";
        String CATEGORY_ID = "category_id";
        String STATUS = "status";
        String CREATE_TIME = "create_time";
        String UPDATE_TIME = "update_time";
    }
}
