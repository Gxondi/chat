package com.hyh.mallchat.common.user.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryInfoDTO {
    @ApiModelProperty("用户id")
    private Long uid;
    @ApiModelProperty("用户名称")
    private String name;
    @ApiModelProperty("用户头像")
    private String avatar;
    @ApiModelProperty("是否需要刷新")
    private boolean isNeedRefresh = Boolean.TRUE;
    @ApiModelProperty("ip归属地")
    private String locPlace;
    @ApiModelProperty("佩戴中的徽章")
    private List<Long> wearingItemId;
    @ApiModelProperty("用户拥有徽章")
    private List<Long> itemIds;

    public static SummaryInfoDTO skip(Long uid){
        SummaryInfoDTO summaryInfoDTO = new SummaryInfoDTO();
        summaryInfoDTO.setNeedRefresh(Boolean.FALSE);
        summaryInfoDTO.setUid(uid);
        return summaryInfoDTO;
    }
}
