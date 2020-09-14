package com.hlx.vbblog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@ApiModel("公告")
@Data
@TableName("sys_notice")
public class Notice implements Serializable {
    @ApiModelProperty("主键:ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("标题")
    @NotNull(message = "标题不能为空")
    @Length(max = 30, message = "标题长度在30个字符以内")
    private String title;

    @ApiModelProperty("内容")
    @NotNull(message = "内容不能为空")
    @Length(max = 100, message = "内容长度在100个字符以内")
    private String content;

    @ApiModelProperty("是否显示")
    private Boolean display;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    public interface Table {
        String ID = "id";
        String TITLE = "TITLE";
        String CONTENT = "content";
        String DISPLAY = "display";
        String CREATE_TIME = "create_time";
        String UPDATE_TIME = "update_time";
    }
}
