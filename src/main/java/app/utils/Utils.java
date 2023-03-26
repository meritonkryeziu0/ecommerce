package app.utils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
  public static boolean isNull(Object value) {
    return value == null;
  }

  public static boolean isBlank(String value) {
    return isNull(value) || value.trim().length() == 0;
  }

  public static boolean isBlank(Map<?, ?> map) {
    return isNull(map) || map.isEmpty();
  }

  public static boolean notBlank(Map<?, ?> map) {
    return !isBlank(map);
  }

  public static boolean isBlank(Object[] list) {
    return isNull(list) || list.length == 0;
  }

  public static boolean isBlank(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  public static boolean notNull(Object value) {
    return value != null;
  }


  public static boolean notBlank(String value) {
    return value != null && value.trim().length() > 0;
  }

  public static boolean notBlank(Collection<?> collection) {
    return collection != null && !collection.isEmpty();
  }

  public static String toLowerCaseAndTrim(String s) {
    if (s == null) return null;
    return s.trim().toLowerCase();
  }

  public static String removeSpecialChars(String text) {
    return isNull(text) ? null : text.replaceAll("[^a-zA-Z0-9 @]", "");
  }

  public static boolean isValidEmail(String email) {
    if (isBlank(email)) return false;
    Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
    Matcher mat = pattern.matcher(email);
    return mat.matches();
  }

  private static String toStringUtf8(byte[] bytes) {
    return new String(bytes, StandardCharsets.UTF_8);
  }

}
