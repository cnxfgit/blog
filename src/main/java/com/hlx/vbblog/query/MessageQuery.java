package com.hlx.vbblog.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@ApiModel("留言查询条件")
@Data
public class MessageQuery implements Serializable {
    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("开始创建日期")
    private String startDate;

    @ApiModelProperty("结束创建日期")
    private String endDate;

}
