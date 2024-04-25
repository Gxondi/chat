package com.hyh.mallchat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.Data;

@Data
@ApiOperation("获取用户信息")
public class UserInfoResp {
    @ApiModelProperty("用户ID")
    private Long userId;
    @ApiModelProperty("用户名称")
    private String userName;
    @ApiModelProperty("用户头像")
    private String avatar;
    @ApiModelProperty("用户性别")
    private Integer sex;
    @ApiModelProperty("用户手机号")
    private Integer modifyNameTimes;
}
