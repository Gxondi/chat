package com.hyh.mallchat.oss;

import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * MinIO配置
 * 注册MinioClient和MinIOTemplate到Spring容器
 */
@Configuration(proxyBeanMethods = false)//(proxyBeanMethods = false)表示不使用CGLIB代理
@EnableConfigurationProperties(OssProperties.class)//开启配置属性支持
@ConditionalOnExpression("${oss.enabled}")//当oss.enabled为true时，才会实例化该类
@ConditionalOnProperty(value = "oss.type", havingValue = "minio")//当oss.type=minio时，才会实例化该类
public class MinIOConfiguration implements IOConfiguration<MinioClient, MinIOTemplate>{
    /**
     * 创建MinioClient实例
     * @param ossProperties
     * @return
     */
    @Bean
    @SneakyThrows
    @ConditionalOnMissingBean(MinioClient.class)
    @Override
    public MinioClient ossClient(OssProperties ossProperties) {
        return MinioClient.builder()
                .endpoint(ossProperties.getEndpoint())
                .credentials(ossProperties.getAccessKey(), ossProperties.getSecretKey())
                .build();
    }
    @Bean
    @ConditionalOnBean({MinioClient.class})
    @ConditionalOnMissingBean(MinIOTemplate.class)//当容器中没有MinIOTemplate实例时，才会实例化该类

    @Override
    public MinIOTemplate ossTemplate(MinioClient minioClient, OssProperties ossProperties) {
        return new MinIOTemplate(minioClient, ossProperties);
    }
}
