package indi.chieftain.minio.toolkit;

import com.github.f4b6a3.uuid.UuidCreator;

/**
 * @author chieftain
 */
public class UUID {

    public static String timeOrderedIdWithMac() {
        return UuidCreator.getSequentialWithMac().toString().replaceAll("-", "");
    }

    public static String timeOrderedIdWithFingerprint() {
        return UuidCreator.getSequentialWithFingerprint().toString().replaceAll("-", "");
    }

    public static String timeOrderedId() {
        return UuidCreator.getSequential().toString().replaceAll("-", "");
    }

    public static String simpleId() {
        return java.util.UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String fullId() {
        return java.util.UUID.randomUUID().toString();
    }
}
