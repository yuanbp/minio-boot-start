package indi.chieftain.minio.component;

import indi.chieftain.minio.pool.MinioClientPool;
import indi.chieftain.minio.vo.MinioItem;
import io.minio.BucketExistsArgs;
import io.minio.DownloadObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author chieftain
 */
public class MinioTemplate {

    private final MinioClientPool pool;

    public MinioTemplate(MinioClientPool pool) {
        this.pool = pool;
    }

    /**
     * Bucket Operations
     */
    public void createBucket(String bucketName) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    public List<Bucket> getAllBuckets() throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            return client.listBuckets();
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    public Optional<Bucket> getBucket(String bucketName) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            return client.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    public void removeBucket(String bucketName) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            client.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    public List<MinioItem> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            List<MinioItem> objectList = new ArrayList<MinioItem>();
            Iterable<Result<Item>> objectsIterator = client.listObjects(ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(recursive).build());
            objectsIterator.forEach(i -> {
                try {
                    objectList.add(new MinioItem(i.get()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return objectList;
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    /**
     * Object operations
     */

    public String getObjectURL(String bucketName, String objectName, Integer expires) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            return client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(objectName).expiry(expires).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    public String getObjectURL(String bucketName, String objectName) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            return client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(objectName).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    public void putFile(String bucketName, String fileName, InputStream stream) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            client.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(stream, stream.available(), -1).contentType("application/octet-stream").build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    public void putFile(String bucketName, String fileName, InputStream stream, String contentType) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            client.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(stream, stream.available(), -1).contentType(contentType).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    public void putObject(String bucketName, String objectName, InputStream stream, long size, String contentType) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            client.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(stream, size, -1).contentType(contentType).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    /**
     * 获取文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     */
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            return client.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    public StatObjectResponse getObjectInfo(String bucketName, String objectName) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            return client.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    public void removeObject(String bucketName, String objectName) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

    public void downloadObject(String bucketName, String objectName, String fileName) throws Exception {
        MinioClient client = pool.getMinioClientPool().borrowObject();
        try {
            client.downloadObject(DownloadObjectArgs.builder().bucket(bucketName).object(objectName).filename(fileName).build());
        } finally {
            if (null != client) {
                pool.getMinioClientPool().returnObject(client);
            }
        }
    }

}
