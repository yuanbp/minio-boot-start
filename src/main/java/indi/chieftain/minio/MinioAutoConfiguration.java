package indi.chieftain.minio;

import indi.chieftain.minio.component.MinioTemplate;
import indi.chieftain.minio.pool.MinioClientPool;
import indi.chieftain.minio.toolkit.MinioHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@EnableConfigurationProperties({MinioProperties.class})
public class MinioAutoConfiguration {

    @Autowired
    private MinioProperties properties;

    @Bean
    public MinioClientPool minioClientPool() {
        return new MinioClientPool(properties);
    }

    @Bean
    @DependsOn("minioClientPool")
    public MinioTemplate minioTemplate(@Qualifier("minioClientPool") MinioClientPool minioClientPool) {
        return new MinioTemplate(minioClientPool);
    }

    @Bean
    @DependsOn({"minioTemplate"})
    public MinioHelper minioHelper(@Qualifier("minioTemplate") MinioTemplate template, @Value("${minio.bucket-name}") String bucketNamePrefix, @Value("${minio.system-platform}") String systemPlatform) {
        return MinioHelper.init(template, bucketNamePrefix, systemPlatform);
    }
}