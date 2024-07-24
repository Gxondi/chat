package com.hyh.mallchat.common.user.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoDTO {
    @ApiModelProperty("用户id")
    private Long itemId;
    @ApiModelProperty("是否需要刷新")
    private boolean isNeedRefresh = Boolean.TRUE;
    @ApiModelProperty("徽章图像")
    private String img;
    @ApiModelProperty("徽章说明")
    private String describe;

    public static ItemInfoDTO skip(Long itemId){
        ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
        itemInfoDTO.setNeedRefresh(Boolean.FALSE);
        itemInfoDTO.setItemId(itemId);
        return itemInfoDTO;
    }
}
