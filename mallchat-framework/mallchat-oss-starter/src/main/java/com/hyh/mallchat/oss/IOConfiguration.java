package com.hyh.mallchat.oss;

import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public interface IOConfiguration<T, U> {
    T ossClient(OssProperties ossProperties);

    U ossTemplate(T ossClient, OssProperties ossProperties);
}
