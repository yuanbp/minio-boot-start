package indi.chieftain.minio.pool;

import io.minio.MinioClient;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author chieftain
 */
public class MinioClientPoolFactory extends BasePooledObjectFactory<MinioClient> {

    private String endpoint, accessKey, secretKey;

    public MinioClientPoolFactory (String endpoint, String accessKey, String secretKey) {
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Override
    public MinioClient create() {
        return MinioClient.builder().endpoint(this.endpoint).credentials(this.accessKey, this.secretKey).build();
    }

    @Override
    public PooledObject<MinioClient> makeObject() throws Exception {
        return super.makeObject();
    }

    @Override
    public PooledObject<MinioClient> wrap(MinioClient minioClient) {
        return new DefaultPooledObject<>(minioClient);
    }
}
