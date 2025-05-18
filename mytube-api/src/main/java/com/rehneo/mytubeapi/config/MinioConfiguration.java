package com.rehneo.mytubeapi.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    @Value("${minio.host}")
    private String host;
    @Value("${minio.apiPort}")
    private String port;
    @Value("${minio.username}")
    private String username;
    @Value("${minio.password}")
    private String password;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(host + ":" + port)
                .credentials(username, password)
                .build();
    }

}