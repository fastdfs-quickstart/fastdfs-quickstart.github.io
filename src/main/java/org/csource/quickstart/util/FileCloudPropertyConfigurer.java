package org.csource.quickstart.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;


/**
 * @author SongJian email:1738042258@QQ.COM 你慢慢发掘,会发现这个类很神奇
 */
public class FileCloudPropertyConfigurer extends PropertyPlaceholderConfigurer {

  private static Map<String, String> properties = new HashMap<String, String>();

  // 过滤应用内部使用的配置的前缀
  private String appPropertiesPrefix;
  // 应用内部使用的配置
  private static Map<String, String> appProperties = new HashMap<String, String>();

  protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
    PropertyPlaceholderHelper helper =
        new PropertyPlaceholderHelper(DEFAULT_PLACEHOLDER_PREFIX, DEFAULT_PLACEHOLDER_SUFFIX, DEFAULT_VALUE_SEPARATOR, false);
    for (Entry<Object, Object> entry : props.entrySet()) {
      String stringKey = String.valueOf(entry.getKey());
      String stringValue = String.valueOf(entry.getValue());
      if ((stringKey.contains(DEFAULT_PLACEHOLDER_PREFIX) && stringKey.contains(DEFAULT_PLACEHOLDER_SUFFIX)
          && (stringKey.indexOf(DEFAULT_PLACEHOLDER_PREFIX) < stringKey.indexOf(DEFAULT_PLACEHOLDER_SUFFIX)))) {
        stringKey = getReplacedStringKey(stringKey, helper, props);
      }
      stringValue = helper.replacePlaceholders(stringValue, props);
      //
      if (stringKey.startsWith(this.appPropertiesPrefix)) {
        FileCloudPropertyConfigurer.appProperties.put(stringKey, stringValue);
      }
      properties.put(stringKey, stringValue);
    }
    super.processProperties(beanFactoryToProcess, props);
  }

  public static Map<String, String> getProperties() {
    return properties;
  }

  public static String getProperty(String key) {
    return properties.get(key);
  }

  public String getReplacedStringKey(String stringKey, PropertyPlaceholderHelper helper, Properties props) {
    if (stringKey.contains(DEFAULT_PLACEHOLDER_PREFIX) && stringKey.contains(DEFAULT_PLACEHOLDER_SUFFIX)
        && (stringKey.indexOf(DEFAULT_PLACEHOLDER_PREFIX) < stringKey.indexOf(DEFAULT_PLACEHOLDER_SUFFIX))) {
      // before of target
      String prefix = stringKey.substring(0, stringKey.indexOf(DEFAULT_PLACEHOLDER_PREFIX));
      // replace
      String refString =
          stringKey.substring(stringKey.indexOf(DEFAULT_PLACEHOLDER_PREFIX), stringKey.indexOf(DEFAULT_PLACEHOLDER_SUFFIX) + 1);
      // after of target
      String suffix = stringKey.substring(stringKey.indexOf(DEFAULT_PLACEHOLDER_SUFFIX) + 1);
      // get target from props data
      String refStringValue = helper.replacePlaceholders(refString, props);
      // build a new local key
      String newKey = prefix + refStringValue + suffix;
      // while until the key not contains of ${}
      return getReplacedStringKey(newKey, helper, props);
    }
    return stringKey;
  }

  public void setAppPropertiesPrefix(String appPropertiesPrefix) {
    this.appPropertiesPrefix = appPropertiesPrefix;
  }

  public static Map<String, String> getAppProperties() {
    return Collections.unmodifiableMap(FileCloudPropertyConfigurer.appProperties);
  }


}
