package indi.chieftain.minio.vo;

import io.minio.StatObjectResponse;
import lombok.Data;

import java.util.Date;

@Data
public class MinioObject {

    private String bucketName;
    private String name;
    private Date lastModified;
    private long length;
    private String etag;
    private String contentType;

    public MinioObject(String bucketName, String name, Date lastModified, long length, String etag, String contentType) {
        this.bucketName = bucketName;
        this.name = name;
        this.lastModified = lastModified;
        this.length = length;
        this.etag = etag;
        this.contentType = contentType;
    }

    public MinioObject(StatObjectResponse os) {
        this.bucketName = os.bucket();
        this.name = os.object();
        this.lastModified = Date.from(os.lastModified().toInstant());
        this.length = os.size();
        this.etag = os.etag();
        this.contentType = os.contentType();
    }
}