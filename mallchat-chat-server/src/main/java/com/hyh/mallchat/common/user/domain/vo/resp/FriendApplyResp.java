package com.hyh.mallchat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyResp {
    @ApiModelProperty("申请id")
    private Long applyId;
    @ApiModelProperty("申请人uid")
    private Long uid;
    @ApiModelProperty("昵称")
    private String name;
    @ApiModelProperty("头像")
    private String avatar;
    @ApiModelProperty("申请类型")
    private Integer type;
    @ApiModelProperty("申请消息")
    private String msg;
    @ApiModelProperty("申请状态")
    private Integer status;
}
