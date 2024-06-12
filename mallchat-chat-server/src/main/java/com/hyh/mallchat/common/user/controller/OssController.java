package com.hyh.mallchat.common.user.controller;

import com.hyh.mallchat.common.common.domain.vo.resp.ApiResult;
import com.hyh.mallchat.common.common.utils.RequestHolder;
import com.hyh.mallchat.common.user.service.OssService;
import com.hyh.mallchat.common.common.domain.vo.req.oss.req.UploadUrlReq;
import com.hyh.mallchat.oss.domain.resp.OssResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController()
@RequestMapping("capi/oss")
public class OssController {
    @Autowired
    private OssService ossService;
    /**
     * 获取临时上传链接
     * @param req
     * @return
     */
    @GetMapping("/upload/url")
    public ApiResult<OssResp> getUploadUrl(@Valid UploadUrlReq req) {
        OssResp ossResp = ossService.getUploadUrl(RequestHolder.getRequestInfo().getUid(),req);
        return ApiResult.success(ossResp);
    }
}
