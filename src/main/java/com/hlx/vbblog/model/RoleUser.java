package com.hlx.vbblog.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("角色用户关联")
@Data
@TableName("sys_role_user")
public class RoleUser implements Serializable {
    @ApiModelProperty("主键:角色ID")
    @TableId
    private Long roleId;

    @ApiModelProperty("主键:用户ID")
    @TableId
    private Long userId;

    public interface Table {
        String ROLE_ID = "role_id";
        String USER_ID = "user_id";
    }
}
