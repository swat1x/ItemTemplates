package ru.swat1x.itemtemplates.config;

public class ConfigReloadException extends RuntimeException {

  public ConfigReloadException(String reason) {
    super("Can't reload plugin configuration. Problem place: " + reason);
  }
}
