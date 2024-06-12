package com.hyh.mallchat.oss.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OssReq {
    @ApiModelProperty("文件存储路径")
    private String filePath;
    @ApiModelProperty("文件名")
    private String fileName;
    @ApiModelProperty("请求的uid")
    private Long uid;
    @ApiModelProperty("自动生成地址")
    @Builder.Default
    private boolean autoPath = true;
}
