package com.hlx.vbblog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *  文章评论
 **/
@ApiModel("评论")
@Data
@TableName("t_comment")
public class Comment implements Serializable {
    @ApiModelProperty("主键:ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("外键:父级ID")
    private Long pid;

    @ApiModelProperty("外键:用户ID")
    private Long userId;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("内容")
    @NotBlank(message = "评论内容不能为空")
    @Length(max = 80, message = "评论内容不能超过80个字符")
    private String content;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("客户端浏览器")
    private String browser;

    @ApiModelProperty("客户端系统")
    private String os;

    @ApiModelProperty("IP来源")
    private String address;

    @ApiModelProperty("请求IP")
    private String requestIp;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @TableField(exist = false)
    private List<Comment> children;

    @TableField(exist = false)
    private Comment parentComment;

    @ApiModelProperty("外键:文章ID")
    private Long aid;

    public interface Table {
        String ID = "id";
        String PID = "pid";
        String USER_ID = "user_id";
        String CONTENT = "content";
        String NICKNAME = "nickname";
        String AVATAR = "avatar";
        String CREATE_TIME = "create_time";
        String BROWSER = "browser";
        String OS = "os";
        String ADDRESS = "address";
        String REQUEST_IP = "request_ip";
        String AID = "aid";
    }
}
