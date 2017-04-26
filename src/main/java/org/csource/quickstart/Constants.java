package org.csource.quickstart;

import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.csource.quickstart.util.FileCloudPropertyConfigurer;

public class Constants {
  
  public static Map<String, String> configure = FileCloudPropertyConfigurer.getAppProperties();
  // 10分钟
  public static int M_10 = 600;
  // 30分钟
  public static int M_30 = 1800;
  // 1小时
  public static int H_1 = 3600;

  // 是否为单机
  static Boolean standAlone = null;

  public static final Boolean isStandAlone() {
    if (standAlone == null) {
      String standAloneValue = configure.get("filecloud.stand-alone");
      if (StringUtils.isNotBlank(standAloneValue)) {
        standAlone = BooleanUtils.toBoolean(standAloneValue.trim());
      } else {
        standAlone = false;
      }
    }
    return standAlone;
  }

  // 默认判断大文件的参数,字节数
  static final long DEFAULT_BIG_FILE_THRESHOLD = 1024 * 1024 * 2;
  static Long bigFileThreshold = null;

  public static final Long getBigFileThreshold() {
    if (bigFileThreshold == null) {
      String bigThresholdValue = configure.get("filecloud.bigfile-threshold");
      if (StringUtils.isNotBlank(bigThresholdValue) && NumberUtils.isNumber(bigThresholdValue.trim())) {
        bigFileThreshold = NumberUtils.toLong(bigThresholdValue.trim());
      } else {
        bigFileThreshold = DEFAULT_BIG_FILE_THRESHOLD;
      }
    }
    return bigFileThreshold;
  }

  // 默认的文件过期时间,到期删除,单位秒
  static final long DEFAULT_FILE_EXPIRE_SECONDS = 3600;

  static Long fileExpireTimeLength = null;

  public static final Long getFileExpireTimeLength() {
    if (fileExpireTimeLength == null) {
      String fileExpireTimeValue = configure.get("filecloud.file-expire-time-length");
      if (StringUtils.isNotBlank(fileExpireTimeValue) && NumberUtils.isNumber(fileExpireTimeValue.trim())) {
        fileExpireTimeLength = NumberUtils.toLong(fileExpireTimeValue.trim());
      } else {
        fileExpireTimeLength = DEFAULT_FILE_EXPIRE_SECONDS;
      }
    }
    return fileExpireTimeLength;
  }

  // 默认报文最大长度,1M
  static final long DEFAULT_PROTOCOL_MAXSIZE = 1024 * 1024 * 1;
  static Long protocolMaxSize = null;

  public static final Long getProtocolMaxSize() {
    if (protocolMaxSize == null) {
      String protocolMaxsizeValue = configure.get("filecloud.protocol-maxsize");
      if (StringUtils.isNotBlank(protocolMaxsizeValue) && NumberUtils.isNumber(protocolMaxsizeValue.trim())) {
        protocolMaxSize = NumberUtils.toLong(protocolMaxsizeValue.trim());
      } else {
        protocolMaxSize = DEFAULT_PROTOCOL_MAXSIZE;
      }
    }
    return protocolMaxSize;
  }

  // 上传到FastDFS最大允许失败次数
  static final int DEFAULT_MAX_RETRY_UPLOAD_TIMES = 3;
  static Integer maxRetryUploadFastDFSTimes = null;

  public static final Integer getMaxRetryUploadFastDFSTimes() {
    if (maxRetryUploadFastDFSTimes == null) {
      String maxRetryUploadFastDFSTimesValue = configure.get("filecloud.fdfs-max-retry-times");
      if (StringUtils.isNotBlank(maxRetryUploadFastDFSTimesValue) && NumberUtils.isNumber(maxRetryUploadFastDFSTimesValue.trim())) {
        maxRetryUploadFastDFSTimes = NumberUtils.toInt(maxRetryUploadFastDFSTimesValue.trim());
      } else {
        maxRetryUploadFastDFSTimes = DEFAULT_MAX_RETRY_UPLOAD_TIMES;
      }
    }
    return maxRetryUploadFastDFSTimes;
  }

  // 实时删除文件线程扫描周期
  static final int REALTIME_DELFILE_SECOND_PERIOD = 5000;
  // 默认支持上传的文件后缀
  static final String[] DEFAULT_SUPPORT_EXTENSION = {"txt"};
  static String[] supportFileSuffix = null;

  public static String[] getSupportFileSuffix() {
    if (supportFileSuffix == null) {
      String supportFileSuffixValue = configure.get("filecloud.support-file-suffix");
      if (StringUtils.isNotBlank(supportFileSuffixValue)) {
        supportFileSuffix = supportFileSuffixValue.split("\\s");
      } else {
        supportFileSuffix = DEFAULT_SUPPORT_EXTENSION;
      }
    }
    return supportFileSuffix;
  }

  static Boolean limitSuffix = null;

  public static Boolean limitSuffix() {
    if (limitSuffix == null) {
      String limitSuffixValue = configure.get("filecloud.limit-file-suffix");
      if (StringUtils.isNotBlank(limitSuffixValue) && BooleanUtils.toBoolean(limitSuffixValue) == true) {
        limitSuffix = true;
      } else {
        limitSuffix = false;
      }
    }
    return limitSuffix;
  }

  // 临时文件保存时间长度
  static Long minExpireTimeLength = null;

  public static Long getMinExpireTimeLength() {
    if (minExpireTimeLength == null) {
      String minExpireTime = configure.get("filecloud.min-expire-time-length");
      if (StringUtils.isNotBlank(minExpireTime) && NumberUtils.isNumber(minExpireTime)) {
        minExpireTimeLength = NumberUtils.toLong(minExpireTime);
        if (minExpireTimeLength < 600) {
          minExpireTimeLength = 600L;
        }
      } else {
        minExpireTimeLength = 600L;
      }
    }
    return minExpireTimeLength;
  }

}
