package com.hyh.mallchat.oss;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OssType {
    /**
     * 阿里云
     */
    ALIYUN("aliyun", 1),
    /**
     * 腾讯云
     */
    TENCENT("tencent", 2),
    /**
     * 七牛云
     */
    QINIU("qiniu", 3),
    /**
     * MinIO
     */
    MINIO("minio", 4)
    ;

   final String name;
   final int type;
}
