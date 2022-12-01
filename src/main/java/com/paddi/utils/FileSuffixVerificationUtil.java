package com.paddi.utils;

import java.util.HashSet;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月01日 21:18:42
 */
public class FileSuffixVerificationUtil {
    private static HashSet<String> photoSuffix = new HashSet<>();

    static  {
        photoSuffix.add("jpg");
        photoSuffix.add("jpeg");
        photoSuffix.add("png");
    }

    public static Boolean isPhoto(String suffix) {
        return photoSuffix.contains(suffix);
    }
}
