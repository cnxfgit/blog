package com.hlx.vbblog.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("角色查询条件")
@Data
public class RoleQuery implements Serializable {
    @ApiModelProperty("名称")
    private String roleName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("开始创建日期")
    private String startDate;

    @ApiModelProperty("结束创建日期")
    private String endDate;
}
