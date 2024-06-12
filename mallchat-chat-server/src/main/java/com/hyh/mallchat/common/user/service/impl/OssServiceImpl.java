package com.hyh.mallchat.common.user.service.impl;

import com.hyh.mallchat.common.common.domain.enums.OssSceneEnum;
import com.hyh.mallchat.common.common.utils.AssertUtil;
import com.hyh.mallchat.common.user.service.OssService;
import com.hyh.mallchat.oss.MinIOTemplate;
import com.hyh.mallchat.common.common.domain.vo.req.oss.req.UploadUrlReq;
import com.hyh.mallchat.oss.domain.OssReq;
import com.hyh.mallchat.oss.domain.resp.OssResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OssServiceImpl implements OssService {
    @Autowired
    private MinIOTemplate minIOTemplate;
    @Override
    public OssResp getUploadUrl(Long uid, UploadUrlReq req) {
        OssSceneEnum scene = OssSceneEnum.of(req.getScene());
        AssertUtil.isNotEmpty(scene, "场景有误");
        OssReq ossReq = OssReq.builder()
                .fileName(req.getFileName())
                .filePath(scene.getPath())
                .uid(uid)
                .build();
        return minIOTemplate.getPreSignedObjectUrl(ossReq);
    }
}
