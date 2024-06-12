package com.hyh.mallchat.oss.domain.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OssResp {
    @ApiModelProperty("上传的临时url")
    private String uploadUrl;
    @ApiModelProperty("成功后能够下载的url")
    private String downloadUrl;
}
