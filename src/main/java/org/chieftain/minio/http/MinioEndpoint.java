package org.chieftain.minio.http;

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
import org.chieftain.minio.service.MinioOptimizTemplate;
import org.chieftain.minio.vo.MinioItem;
import org.chieftain.minio.vo.MinioObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConditionalOnProperty(name = "minio.endpoint.enable", havingValue = "true")
@RestController
@RequestMapping("${minio.endpoint.name:/minio}")
public class MinioEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioEndpoint.class);

    public MinioEndpoint() {
        LOGGER.info("MinioEndpoint init");
    }

    @Autowired
    private MinioOptimizTemplate template;
    private static final Integer ONE_DAY = 60 * 60 * 24;

    /**
     * 创建 bucket
     * Bucket Endpoints
     */
    @PostMapping("/bucket/{bucketName}")
    public Bucket createBucker(@PathVariable String bucketName) throws Exception {
        template.createBucket(bucketName);
        return template.getBucket(bucketName).get();
    }

    /**
     * 获取所有bucket
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidPortException
     * @throws ErrorResponseException
     * @throws NoResponseException
     * @throws InvalidBucketNameException
     * @throws InsufficientDataException
     * @throws InvalidEndpointException
     * @throws InternalException
     * @throws InvalidResponseException
     */
    @GetMapping("/bucket")
    public List<Bucket> getBuckets() throws Exception {
            return template.getAllBuckets();
    }

    /**
     * 根据名称获取bucket
     * @param bucketName
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidPortException
     * @throws ErrorResponseException
     * @throws NoResponseException
     * @throws InvalidBucketNameException
     * @throws InsufficientDataException
     * @throws InvalidEndpointException
     * @throws InternalException
     * @throws InvalidResponseException
     */
    @GetMapping("/bucket/{bucketName}")
    public Bucket getBucket(@PathVariable String bucketName) throws Exception {
        return template.getBucket(bucketName).orElseThrow(() -> new IllegalArgumentException("Bucket Name not found!"));
    }

    /**
     * 删除bucket
     * @param bucketName
     * @throws IOException
     * @throws XmlPullParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidPortException
     * @throws ErrorResponseException
     * @throws NoResponseException
     * @throws InvalidBucketNameException
     * @throws InsufficientDataException
     * @throws InvalidEndpointException
     * @throws InternalException
     * @throws InvalidResponseException
     */
    @DeleteMapping("/bucket/{bucketName}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteBucket(@PathVariable String bucketName) throws Exception {
            template.removeBucket(bucketName);
    }

    /**
     * 存入对象到bucket
     * Object Endpoints
     */
    @PostMapping("/object/{bucketName}")
    public MinioObject putObject(@RequestBody MultipartFile object, @PathVariable String bucketName) throws Exception {
        String name = object.getOriginalFilename();
        template.putObject(bucketName, name, object.getInputStream(), object.getSize(), object.getContentType());
        return new MinioObject(template.getObjectInfo(bucketName, name));

    }

    /**
     * 存入对象到bucket并设置对象名称
     * @param object
     * @param bucketName
     * @param objectName
     * @return
     * @throws IOException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws XmlPullParserException
     * @throws InvalidPortException
     * @throws ErrorResponseException
     * @throws InternalException
     * @throws NoResponseException
     * @throws InvalidBucketNameException
     * @throws InsufficientDataException
     * @throws InvalidEndpointException
     * @throws RegionConflictException
     * @throws InvalidArgumentException
     * @throws InvalidResponseException
     */
    @PostMapping("/object/{bucketName}/{objectName}")
    public MinioObject putObject(@RequestBody MultipartFile object, @PathVariable String bucketName, @PathVariable String objectName) throws Exception {
        template.putObject(bucketName, objectName, object.getInputStream(), object.getSize(), object.getContentType());
        return new MinioObject(template.getObjectInfo(bucketName, objectName));
    }


    /**
     * 根据bucket名称和对象名称过滤所有对象
     * @param bucketName
     * @param objectName
     * @return
     * @throws InvalidPortException
     * @throws InvalidEndpointException
     */
    @GetMapping("/object/{bucketName}/{objectName}")
    public  List<MinioItem>  filterObject(@PathVariable String bucketName, @PathVariable String objectName) throws Exception {
        return template.getAllObjectsByPrefix(bucketName, objectName, true);
    }

    /**
     * 根据名称获取bucket下的对象并设置外链的过期时间
     * @param bucketName
     * @param objectName
     * @param expires
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidPortException
     * @throws ErrorResponseException
     * @throws NoResponseException
     * @throws InvalidBucketNameException
     * @throws InsufficientDataException
     * @throws InvalidEndpointException
     * @throws InternalException
     * @throws InvalidExpiresRangeException
     * @throws InvalidResponseException
     */
    @GetMapping("/object/{bucketName}/{objectName}/{expires}")
    public Map<String, Object> getObject( @PathVariable String bucketName, @PathVariable String objectName, @PathVariable Integer expires) throws Exception {
        Map<String,Object> responseBody = new HashMap<>();
        // Put Object info
        responseBody.put("bucket" , bucketName);
        responseBody.put("object" , objectName);
        responseBody.put("url" , template.getObjectURL(bucketName, objectName, expires));
        responseBody.put("expires" ,  expires);
        return  responseBody;
    }

    /**
     * 根据名称对指定bucket下的对象进行删除
     * @param bucketName
     * @param objectName
     * @throws IOException
     * @throws XmlPullParserException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidPortException
     * @throws ErrorResponseException
     * @throws NoResponseException
     * @throws InvalidBucketNameException
     * @throws InsufficientDataException
     * @throws InvalidEndpointException
     * @throws InternalException
     * @throws InvalidArgumentException
     * @throws InvalidResponseException
     */
    @DeleteMapping("/object/{bucketName}/{objectName}/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteObject(@PathVariable String bucketName, @PathVariable String objectName) throws Exception {
        template.removeObject(bucketName, objectName);
    }
}