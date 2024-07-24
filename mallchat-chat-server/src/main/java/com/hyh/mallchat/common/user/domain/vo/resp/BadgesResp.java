package com.hyh.mallchat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiOperation("可选徽章预览")
public class BadgesResp {
    @ApiModelProperty("徽章id")
    private Long id;
    @ApiModelProperty("徽章图标")
    private String img;
    @ApiModelProperty("徽章说明")
    private String describe;
    @ApiModelProperty("是否拥有 0否 1是")
    private Integer obtain;
    @ApiModelProperty("是否佩戴 0否 1是")
    private Integer wearing;
}
