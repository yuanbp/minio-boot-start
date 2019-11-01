package org.chieftain.minio.service;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidArgumentException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidExpiresRangeException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.NoResponseException;
import io.minio.errors.RegionConflictException;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.chieftain.minio.vo.MinioItem;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Deprecated
public class MinioTemplate {

    private static String endpoint, accessKey, secretKey;

    public MinioTemplate() {
    }

    /**
     * Create new instance of the {@link MinioTemplate} with the access key and secret key.
     * @param endpoint minio URL, it should be a  URL, domain name, IPv4 address or IPv6 address
     * @param accessKey uniquely identifies a minio account.
     * @param secretKey the password to a minio account.
     */
    public MinioTemplate(String endpoint, String accessKey, String secretKey) {
        MinioTemplate.endpoint = endpoint;
        MinioTemplate.accessKey = accessKey;
        MinioTemplate.secretKey = secretKey;
    }

    /**
     * Bucket Operations
     */

    public void createBucket(String bucketName) throws XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, IOException, InvalidPortException, InvalidEndpointException, RegionConflictException, NoResponseException, InternalException, ErrorResponseException, InsufficientDataException, InvalidBucketNameException, InvalidResponseException {
        MinioClient client = getMinioClient();
        if(!client.bucketExists(bucketName)){
            client.makeBucket(bucketName);
        }
    }

    public List<Bucket> getAllBuckets() throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidResponseException {
        return getMinioClient().listBuckets();
    }

    public Optional<Bucket> getBucket(String bucketName) throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidResponseException {
        return getMinioClient().listBuckets().stream().filter( b -> b.name().equals(bucketName)).findFirst();
    }

    public void removeBucket(String bucketName) throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidResponseException {
        getMinioClient().removeBucket(bucketName);
    }

    public List<MinioItem> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) throws InvalidPortException, InvalidEndpointException{
        List objectList = new ArrayList();
        Iterable<Result<Item>> objectsIterator = getMinioClient().listObjects(bucketName, prefix, recursive);
        objectsIterator.forEach(i -> {
            try {
                objectList.add(new MinioItem(i.get()));
            } catch (Exception e) {
                new Exception(e);
            }
        });
        return objectList;
    }

    /**
     * Object operations
     */

    public String getObjectURL(String bucketName, String objectName, Integer expires) throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidExpiresRangeException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidResponseException {
        return getMinioClient().presignedGetObject(bucketName, objectName, expires);
    }

    public void putFile(String bucketName, String fileName, InputStream stream) throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidResponseException {
        getMinioClient().putObject(bucketName, fileName, stream, (long) stream.available(), null, null, "application/octet-stream");
    }

    public void putObject(String bucketName, String objectName, InputStream stream, long size, String contentType) throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidResponseException {
        getMinioClient().putObject(bucketName, objectName, stream, size, null, null, contentType);
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
        return getMinioClient().getObject(bucketName, objectName);
    }

    public ObjectStat getObjectInfo(String bucketName, String objectName) throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidResponseException, InvalidArgumentException {
        return getMinioClient().statObject(bucketName, objectName);
    }

    public void removeObject(String bucketName, String objectName ) throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidArgumentException, InvalidResponseException {
        getMinioClient().removeObject(bucketName, objectName);
    }

    private static class MinioClientHolder {
        private static  MinioClient minioClient;
        static {
            try {
                minioClient = new MinioClient(endpoint, accessKey, secretKey);
            } catch (InvalidEndpointException | InvalidPortException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets a Minio client
     */
    public MinioClient getMinioClient() {
        return MinioClientHolder.minioClient;
    }
}