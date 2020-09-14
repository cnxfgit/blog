package com.hlx.vbblog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@ApiModel("审核VO")
@Data
public class AuditVO implements Serializable {
    @ApiModelProperty("审核对象ID")
    @NotNull
    private Long id;

    @ApiModelProperty("审核状态[0:审核未过, 1:等待审核, 2:审核通过]")
    @NotNull
    private Integer status;
}
