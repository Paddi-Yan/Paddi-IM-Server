package com.paddi.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Project: minio-demo
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月30日 15:37:16
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProp {
    private String endpoint;

    private String accessKey;

    private String secretKey;
}
