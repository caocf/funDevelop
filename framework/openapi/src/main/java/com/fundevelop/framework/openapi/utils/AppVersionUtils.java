package com.fundevelop.framework.openapi.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * APP版本号工具类.
 * <a href="mailto:yangmujiang@sohu.com">Reamy(杨木江)</a> 创建于 2016/4/11 21:46
 */
public class AppVersionUtils {
    /**
     * 判断APP版本的设备类型是否为iPhone.
     * @param appVersion 版本号 appname_iphone_1.0.1.1120_dailybuild
     */
    public static boolean isIPHONE(String appVersion) {
        return StringUtils.contains(appVersion, "iphone");
    }

    /**
     * 判断APP版本的设备类型Android.
     * @param appVersion 版本号 appname_android_1.0.1.1120_dailybuild
     */
    public static boolean isANDROID(String appVersion) {
        return StringUtils.contains(appVersion, "android");
    }

    /**
     * 判断是否为PC端请求.
     * @param appVersion 版本号 appname_pc_1.0.1.1120_dailybuild
     */
    public static boolean isPC(String appVersion) {
        return StringUtils.contains(appVersion, "pc");
    }

    /**
     * 判断是否为HTML5（手机版）端请求.
     * @param appVersion 版本号 appname_h5_1.0.1.1120_dailybuild
     */
    public static boolean isHtml5(String appVersion) {
        return StringUtils.contains(appVersion, "h5");
    }

    /**
     * 判断是否为安卓或者ios设备.
     * @param appVersion 版本号 appname_iphone_1.0.1.1120_dailybuild
     */
    public static boolean isMobileDevice(String appVersion) {
        return isIPHONE(appVersion) || isANDROID(appVersion);
    }

    /**
     * 获取版本中的数字版本号.
     * @param appVersion 版本号 appname_iphone_1.0.1.1120_dailybuild
     */
    public static String getAppVersionNumber(String appVersion) {
        if (StringUtils.isNotBlank(appVersion)) {
            return appVersion.split("_")[2];
        }

        return null;
    }

    /**
     * 获取版本中的数字版本号，去除构建号.
     * @param appVersion 版本号 appname_iphone_1.0.1.1120_dailybuild
     */
    public static String getAppVersionNumberWithoutBuildNumber(String appVersion) {
        String appVersionNumber = getAppVersionNumber(appVersion);

        if (StringUtils.isNotBlank(appVersionNumber)) {
            String[] versions = appVersionNumber.split("\\.");
            if (versions.length == 4) {
                return StringUtils.substringBeforeLast(appVersionNumber, ".");
            }
        }

        return appVersionNumber;
    }

    /**
     * 获取版本的打包类型：dev、dailybuild、release(andriod)、appstore(ios).
     * @param appVersion 版本号 appname_iphone_1.0.1.1120_dailybuild
     */
    public static String getAppBuildVersionType(String appVersion) {
        if (StringUtils.isNotBlank(appVersion)) {
            return StringUtils.substringAfterLast(appVersion, "_");
        }

        return null;
    }

    /**
     * 获取版本中的数字版本号并按照每段5位格式化成数值.
     * @param appVersion 版本号 appname_iphone_1.0.1.1120_dailybuild
     */
    public static Long getAppVersionNumber2Long(String appVersion) {
        Long appVersionLong = 0L;

        if (StringUtils.isNotBlank(appVersion)) {
            String appVersionNumber = appVersion;

            if (appVersion.indexOf("_") != -1) {
                appVersionNumber = getAppVersionNumberWithoutBuildNumber(appVersion);
            }

            if (StringUtils.isNotBlank(appVersionNumber)) {
                String[] appVersions = appVersionNumber.split("\\.");

                if (appVersions != null) {
                    int i = 1;
                    String version = appVersions[0];

                    for (; i < appVersions.length; i++) {
                        String subVersion = appVersions[i];

                        while (subVersion.length() < SUBVERSION_LENGTH) {
                            subVersion = "0" + subVersion;
                        }

                        version += subVersion;
                    }

                    for (; i < 4; i++) {
                        String subVersion = "";

                        while (subVersion.length() < SUBVERSION_LENGTH) {
                            subVersion = "0" + subVersion;
                        }

                        version += subVersion;
                    }

                    appVersionLong = Long.valueOf(version, 10);
                }
            }
        }

        return appVersionLong;
    }

    /**
     * 获取设备类型.
     * @param appVersion 版本号 appname_iphone_1.0.1.1120_dailybuild
     */
    public static String getClientType(String appVersion) {
        if (StringUtils.isBlank(appVersion)) {
            return "other";
        }
        if (isIPHONE(appVersion)) {
            return "iphone";
        }
        if (isANDROID(appVersion)) {
            return "android";
        }
        if (isPC(appVersion)) {
            return "pc";
        }
        if (isHtml5(appVersion)) {
            return "h5";
        }

        return "other";
    }

    /**
     * 版本号比较.
     * @param appVersion 版本号 appname_iphone_1.0.1.1120_dailybuild
     * @param diffAppVersion 要比较的版本号 1.0
     * @return
     */
    public static boolean greaterThan(String appVersion, String diffAppVersion) {
        String appVersionNumber = getAppVersionNumberWithoutBuildNumber(appVersion);

        if (appVersionNumber != null && diffAppVersion != null) {
            String[] appVersions = appVersionNumber.split("\\.");
            String[] diffVersions = diffAppVersion.split("\\.");

            if (appVersions != null && appVersions.length >= 2 && diffVersions != null && diffVersions.length >= 2) {
                // 第一、二级
                for (int i = 0; i < 2; i++) {
                    String appVersionTemp = appVersions[i];
                    String diffVersionLevelTemp = diffVersions[i];
                    if (new Integer(appVersionTemp) > new Integer(diffVersionLevelTemp)) {
                        return true;
                    } else if (new Integer(appVersionTemp) < new Integer(diffVersionLevelTemp)) {
                        return false;
                    }
                }

                // 第三级
                if (diffVersions.length == 3 && appVersions.length >= 3) {
                    String appVersionLevel3 = appVersions[2];
                    String diffVersionLevel3 = diffVersions[2];
                    if (new Integer(appVersionLevel3) >= new Integer(diffVersionLevel3)) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    /**
     * 每段版本号长度.
     */
    private static final int SUBVERSION_LENGTH = 5;

    private AppVersionUtils(){}
}
