package com.hlx.vbblog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@ApiModel("用户信息")
@Data
public class UserInfoVO implements Serializable {
    @ApiModelProperty("用户ID")
    @NotNull
    private Long id;

    @ApiModelProperty("昵称")
    @NotBlank(message = "昵称不能为空")
    @Length(min = 3, max = 16, message = "昵称长度为3-16个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5\\s·]+$", message = "\"昵称不能有特殊字符")
    private String nickname;

    @ApiModelProperty("邮箱")
    @Email
    private String email;

}
