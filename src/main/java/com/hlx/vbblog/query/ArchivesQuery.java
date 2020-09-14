package com.hlx.vbblog.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("归档查询条件")
@Data
public class ArchivesQuery implements Serializable {
    @ApiModelProperty("日期统计类型[1:按日统计, 2:按月统计, 3:按年统计]")
    private Integer dateFilterType;
}
