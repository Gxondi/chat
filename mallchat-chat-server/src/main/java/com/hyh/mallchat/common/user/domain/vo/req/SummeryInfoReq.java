package com.hyh.mallchat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummeryInfoReq {
    @ApiModelProperty("用户入参列表")
    private List<infoReq> infoReq;

    @Data
    public static class infoReq {
        @ApiModelProperty("id")
        private Long uid;
        @ApiModelProperty("用户最后一次修改时间")
        private Long lastModifyTime;
    }
}

