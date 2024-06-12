package com.hyh.mallchat.common.user.service;

import com.hyh.mallchat.common.common.domain.vo.req.oss.req.UploadUrlReq;
import com.hyh.mallchat.oss.domain.resp.OssResp;


public interface OssService {
    OssResp getUploadUrl(Long uid, UploadUrlReq req);
}
