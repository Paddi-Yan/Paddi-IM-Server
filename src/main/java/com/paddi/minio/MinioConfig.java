package com.paddi.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Project: minio-demo
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月30日 15:39:20
 */
@Configuration
@EnableConfigurationProperties(MinioProp.class)
public class MinioConfig {
    @Autowired
    private MinioProp minioProp;

    @Bean
    public MinioClient minioClient()  {
        return MinioClient.builder()
                          .endpoint(minioProp.getEndpoint())
                          .credentials(minioProp.getAccessKey(), minioProp.getSecretKey()).build();
    }
}
