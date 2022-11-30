package com.paddi.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月30日 19:42:48
 */
public class FileSizeUnitUtil {
    /**
     * 文件大小智能转换
     * 会将文件大小转换为最大满足单位
     * @param size（文件大小，单位为B）
     * @return
     */
    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,###.##").format(size / Math.pow(1024, digitGroups)) + units[digitGroups];
    }

    /**
     * 文件大小智能转换
     * 会将文件大小转换为当前单位到最大满足单位（满足GB: 包含B,KB,MB,GB）
     * @param size（文件大小，单位为B）
     * @return
     */
    public static Map<String, BigDecimal> readableFileSizeMap(long size) {
        Map<String,BigDecimal> map = new HashMap<>();
        if (size <= 0) {
            return map;
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        for(int i=0;i<=digitGroups;i++){
            map.put(units[i],new BigDecimal(size / Math.pow(1024, i)));
        }
        return map;
    }


}
