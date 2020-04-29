package org.olf.general

public class StringUtils {
  private static String truncate(String str, int maxLength) {
    ( str?.length() ?: 0) > maxLength ? str.take(maxLength - 3) + "..." : str
  }
}