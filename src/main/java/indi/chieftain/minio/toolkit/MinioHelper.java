package indi.chieftain.minio.toolkit;

import indi.chieftain.minio.component.MinioTemplate;
import indi.chieftain.minio.vo.MinioObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chieftain
 */
public class MinioHelper {

    private final String bucketNamePrefix;

    private String separator;

    private final MinioTemplate template;

    public MinioHelper(MinioTemplate template, String bucketNamePrefix, String systemPlatform) {
        this.template = template;
        this.bucketNamePrefix = bucketNamePrefix;
        SystemPlatformEnum systemPlatformEnum = SystemPlatformEnum.findByCode(systemPlatform);
        switch (systemPlatformEnum) {
            case UNIX:
                this.separator = "/";
                break;
            case WINDOWS:
                this.separator = "\\";
                break;
            default:
        }
    }

    public static MinioHelper init(MinioTemplate template, String bucketNamePrefix, String systemPlatform) {
        return new MinioHelper(template, bucketNamePrefix, systemPlatform);
    }

    /**
     * 文件上传
     *
     * @param multipartFile
     * @return
     * @throws Exception
     */
    public MinioObject uploadFile(MultipartFile multipartFile) throws Exception {
        String fileName = multipartFile.getOriginalFilename();
        return this.putMultipartFile(fileName, multipartFile);
    }

    /**
     * 文件上传自定义文件名
     *
     * @param multipartFile
     * @param fileName
     * @return
     * @throws Exception
     */
    public MinioObject uploadFile(MultipartFile multipartFile, String fileName) throws Exception {
        return this.putMultipartFile(fileName, multipartFile);
    }

    public MinioObject putMultipartFile(String fileName, MultipartFile multipartFile) throws Exception {
        assert fileName != null;
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String bucketName = bucketNamePrefix;
        template.createBucket(bucketName);
        fileName = fileSuffix.concat(this.separator).concat(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"))).concat(this.separator).concat(fileName);
        template.putFile(bucketName, fileName, multipartFile.getInputStream(), multipartFile.getContentType());
        return new MinioObject(bucketName, fileName, new Date(), multipartFile.getInputStream().available(), null, multipartFile.getContentType());
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     * @throws Exception
     */
    public MinioObject uploadFile(File file) throws Exception {
        String fileName = file.getName();
        return this.putFile(file, fileName);
    }

    /**
     * 文件上传自定义文件名
     *
     * @param file
     * @param fileName
     * @return
     * @throws Exception
     */
    public MinioObject uploadFile(File file, String fileName) throws Exception {
        return this.putFile(file, fileName);
    }

    private MinioObject putFile(File file, String fileName) throws Exception {
        assert fileName != null;
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String bucketName = bucketNamePrefix;
        template.createBucket(bucketName);
        InputStream stream = new FileInputStream(file);
        fileName = this.concatFileName(fileName, fileSuffix);
        template.putFile(bucketName, fileName, stream);
        return new MinioObject(bucketName, fileName, new Date(), stream.available(), null, null);
    }

    public MinioObject uploadFile(InputStream stream, String fileName) throws Exception {
        assert fileName != null;
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String bucketName = bucketNamePrefix;
        template.createBucket(bucketName);
        fileName = this.concatFileName(fileName, fileSuffix);
        template.putFile(bucketName, fileName, stream);
        return new MinioObject(bucketName, fileName, new Date(), stream.available(), null, null);
    }

    private String concatFileName (String fileName, String fileSuffix) {
        return fileSuffix.concat(this.separator).concat(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"))).concat(this.separator).concat(fileName);
    }

    /**
     * 获取文件外链并设置有效时长,不设置默认3天
     *
     * @param bucketName 桶名称
     * @param fileName   文件名称
     * @param expires    有效时长(秒)
     * @return
     * @throws Exception
     */
    public String getFileUrl(String bucketName, String fileName, Integer expires) throws Exception {
        assert bucketName != null;
        assert fileName != null;
        if (null == expires) {
            expires = 432000;
        }
        return template.getObjectURL(bucketName, fileName, expires);
    }

    /**
     * 获取文件外链并设置有效时长3天
     *
     * @param bucketName
     * @param fileName
     * @return
     * @throws Exception
     */
    public String getFileUrl(String bucketName, String fileName) throws Exception {
        return template.getObjectURL(bucketName, fileName);
    }

    /**
     * 获取文件流
     *
     * @param bucketName
     * @param fileName
     * @return
     */
    public InputStream getFileInputStream(String bucketName, String fileName) {
        assert bucketName != null;
        assert fileName != null;
        return template.getObject(bucketName, fileName);
    }
    
    /**
     * 移除文件
     *
     * @param bucketName
     * @param fileName
     * @throws Exception
     */
    public void removeFile(String bucketName, String fileName) throws Exception {
        template.removeObject(bucketName, fileName);
    }

    /**
     * 上传通过连接共享的文件
     */
    public MinioObject uploadShareLink(String shareLink) throws Exception {
        InputStream urlInputStream = null;
        InputStream resultStream = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(shareLink).openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
            urlInputStream = conn.getInputStream();
            String fileSuffix = contentTypeMap.get(conn.getContentType());
            byte[] getData = readInputStream(urlInputStream);
            resultStream = new ByteArrayInputStream(getData);
            return this.uploadFile(resultStream, UUID.timeOrderedIdWithMac() + fileSuffix);
        } finally {
            if (null != resultStream) {
                resultStream.close();
            }
            if (null != urlInputStream) {
                urlInputStream.close();
            }
        }
    }

    private static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * content-value 与 文件类型的映射
     */
    private static Map<String, String> contentTypeMap = new HashMap<>();

    static {
        contentTypeMap.put("audio/aac",".aac");
        contentTypeMap.put("application/x-abiword",".abw");
        contentTypeMap.put("application/x-freearc",".arc");
        contentTypeMap.put("video/x-msvideo",".avi");
        contentTypeMap.put("application/vnd.amazon.ebook",".azw");
        contentTypeMap.put("application/octet-stream",".bin");
        contentTypeMap.put("image/bmp",".bmp");
        contentTypeMap.put("application/x-bzip",".bz");
        contentTypeMap.put("application/x-bzip2",".bz2");
        contentTypeMap.put("application/x-csh",".csh");
        contentTypeMap.put("text/css",".css");
        contentTypeMap.put("text/csv",".csv");
        contentTypeMap.put("application/msword",".doc");
        contentTypeMap.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document",".docx");
        contentTypeMap.put("application/vnd.ms-fontobject",".eot");
        contentTypeMap.put("application/epub+zip",".epub");
        contentTypeMap.put("image/gif",".gif");
        contentTypeMap.put("text/html",".htm");
        contentTypeMap.put("image/vnd.microsoft.icon",".ico");
        contentTypeMap.put("text/calendar",".ics");
        contentTypeMap.put("application/java-archive",".jar");
        contentTypeMap.put("image/jpeg",".jpeg");
        contentTypeMap.put("text/javascript",".js");
        contentTypeMap.put("application/json",".json");
        contentTypeMap.put("application/ld+json",".jsonld");
        contentTypeMap.put("audio/midi",".mid");
        contentTypeMap.put("audio/x-midi",".mid");
        contentTypeMap.put("audio/mpeg",".mp3");
        contentTypeMap.put("video/mpeg",".mpeg");
        contentTypeMap.put("application/vnd.apple.installer+xml",".mpkg");
        contentTypeMap.put("application/vnd.oasis.opendocument.presentation",".odp");
        contentTypeMap.put("application/vnd.oasis.opendocument.spreadsheet",".ods");
        contentTypeMap.put("application/vnd.oasis.opendocument.text",".odt");
        contentTypeMap.put("audio/ogg",".oga");
        contentTypeMap.put("video/ogg",".ogv");
        contentTypeMap.put("application/ogg",".ogx");
        contentTypeMap.put("font/otf",".otf");
        contentTypeMap.put("image/png",".png");
        contentTypeMap.put("application/pdf",".pdf");
        contentTypeMap.put("application/vnd.ms-powerpoint",".ppt");
        contentTypeMap.put("application/vnd.openxmlformats-officedocument.presentationml.presentation",".pptx");
        contentTypeMap.put("application/x-rar-compressed",".rar");
        contentTypeMap.put("application/rtf",".rtf");
        contentTypeMap.put("application/x-sh",".sh");
        contentTypeMap.put("image/svg+xml",".svg");
        contentTypeMap.put("application/x-shockwave-flash",".swf");
        contentTypeMap.put("application/x-tar",".tar");
        contentTypeMap.put("image/tiff","tiff");
        contentTypeMap.put("font/ttf",".ttf");
        contentTypeMap.put("text/plain",".txt");
        contentTypeMap.put("application/vnd.visio",".vsd");
        contentTypeMap.put("audio/wav",".wav");
        contentTypeMap.put("audio/webm",".weba");
        contentTypeMap.put("video/webm",".webm");
        contentTypeMap.put("image/webp",".webp");
        contentTypeMap.put("font/woff",".woff");
        contentTypeMap.put("font/woff2",".woff2");
        contentTypeMap.put("application/xhtml+xml",".xhtml");
        contentTypeMap.put("application/vnd.ms-excel",".xls");
        contentTypeMap.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",".xlsx");
        contentTypeMap.put("application/xml",".xml");
        contentTypeMap.put("text/xml",".xml");
        contentTypeMap.put("application/vnd.mozilla.xul+xml",".xul");
        contentTypeMap.put("application/zip",".zip");
        contentTypeMap.put("video/3gpp",".3gp");
        contentTypeMap.put("audio/3gpp",".3gp");
        contentTypeMap.put("video/3gpp2",".3g2");
        contentTypeMap.put("audio/3gpp2",".3g2");
        contentTypeMap.put("application/x-7z-compressed",".7z");
    }
}
