package com.hlx.vbblog.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("用户菜单关联")
@Data
@TableName("sys_role_menu")
public class RoleMenu implements Serializable {
    @ApiModelProperty("主键:角色ID")
    @TableId
    private Long roleId;

    @ApiModelProperty("主键:菜单ID")
    @TableId
    private Long menuId;

    public interface Table {
        String ROLE_ID = "role_id";
        String MENU_ID = "menu_id";
    }
}
