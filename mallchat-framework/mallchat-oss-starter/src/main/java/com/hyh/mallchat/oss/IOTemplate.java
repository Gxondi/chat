package com.hyh.mallchat.oss;

import io.minio.messages.Bucket;
import lombok.SneakyThrows;

import java.util.List;

public interface IOTemplate {
    //查询所以桶
    @SneakyThrows
    List<Bucket> listBuckets();

    //桶是否存在
    @SneakyThrows
    boolean isBucketExist(String bucketName);

    //创建桶
    @SneakyThrows
    void makeBucket(String bucketName);

    //删除桶
    @SneakyThrows
    void removeBucket(String bucketName);
}
