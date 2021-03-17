package indi.chieftain.minio.toolkit;

import java.util.EnumSet;

/**
 * @author chieftain
 */
public enum SystemPlatformEnum {
    WINDOWS ("windows"),
    UNIX ("unix")
    ;

    private String code;

    SystemPlatformEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static SystemPlatformEnum findByCode (String code) {
        return EnumSet.allOf(SystemPlatformEnum.class).stream().filter(e -> e.getCode().equals(code)).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported SystemPlatformEnum %s", code)));
    }
}
