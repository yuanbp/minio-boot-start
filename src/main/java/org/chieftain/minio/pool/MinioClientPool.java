package org.chieftain.minio.pool;

import io.minio.MinioClient;
import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.chieftain.minio.MinioProperties;

/**
 * @author chieftain
 * @date 2019-11-01 16:25
 */
@Data
public class MinioClientPool {

    private GenericObjectPool<MinioClient> minioClientPool;

    public MinioClientPool (MinioProperties minioProperties) {
        MinioClientPoolFactory factory = new MinioClientPoolFactory(minioProperties.getUrl(), minioProperties.getAccessKey(), minioProperties.getSecretKey());
        GenericObjectPoolConfig<MinioClient> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(minioProperties.getPool().getMaxTotal());
        config.setMaxIdle(minioProperties.getPool().getMaxIdle());
        config.setMinIdle(minioProperties.getPool().getMinIdle());
        config.setMaxWaitMillis(minioProperties.getPool().getMaxWaitMillis());
        config.setBlockWhenExhausted(minioProperties.getPool().isBlockWhenExhausted());
        minioClientPool = new GenericObjectPool<>(factory, config);
    }
}
