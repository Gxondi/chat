package com.hyh.mallchat.oss;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Data
public class OssFile {
    @ApiModelProperty("OSS 存储时文件路径")
    private String ossFilePath;
    @ApiModelProperty("原始文件名")
    private String originalFileName;
}
