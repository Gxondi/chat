package com.hyh.mallchat.oss;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.hyh.mallchat.oss.domain.OssReq;
import com.hyh.mallchat.oss.domain.resp.OssResp;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class MinIOTemplate implements IOTemplate {

    public static final int EXPIRY = 60 * 60 * 24;
    MinioClient minioClient;
    OssProperties ossProperties;

    @Override
    @SneakyThrows
    public List<Bucket> listBuckets() {
        return minioClient.listBuckets();
    }

    @Override
    @SneakyThrows
    public boolean isBucketExist(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    @Override
    @SneakyThrows
    public void makeBucket(String bucketName) {
        if (!isBucketExist(bucketName)) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    @Override
    @SneakyThrows
    public void removeBucket(String bucketName) {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 获取预签名的url
     *
     * @param ossReq
     * @return
     */
    @SneakyThrows
    public OssResp getPreSignedObjectUrl(OssReq ossReq) {
        String absolutePath = ossReq.isAutoPath() ? generateAutoPath(ossReq) : ossReq.getFilePath() + StrUtil.SLASH + ossReq.getFileName();
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(ossProperties.getBucketName())
                        .object(absolutePath)
                        .expiry(EXPIRY)
                        .build());
        return OssResp.builder()
                .uploadUrl(url)
                .downloadUrl(downloadUrl(absolutePath, ossProperties.getBucketName()))
                .build();
    }
    private String downloadUrl(String absolutePath, String bucketName) {
        return ossProperties.getEndpoint() + StrUtil.SLASH + bucketName + absolutePath;
    }

    /**
     * 生成自动路径
     * @param ossReq
     * @return
     */
    private String generateAutoPath(OssReq ossReq) {
        String uid = Optional.ofNullable(ossReq.getUid()).map(String::valueOf).orElse("000000");
        UUID uuid = UUID.fastUUID();
        String suffix = FileNameUtil.getSuffix(ossReq.getFileName());
        String date = DateUtil.format(new Date(), "yyyy/MM/dd");
        return ossReq.getFileName() + StrUtil.SLASH + date + StrUtil.SLASH + uid + StrUtil.SLASH + uuid + StrUtil.DOT + suffix;
    }
}
