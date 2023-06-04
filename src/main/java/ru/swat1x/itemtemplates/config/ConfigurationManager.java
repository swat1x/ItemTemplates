package ru.swat1x.itemtemplates.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.var;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import ru.swat1x.itemtemplates.storage.Storage;
import ru.swat1x.itemtemplates.storage.StorageType;
import ru.swat1x.itemtemplates.storage.imp.YamlStorage;

import java.io.File;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigurationManager {

  // Static data
  final Plugin plugin;

  // Bukkit's configuration object
  FileConfiguration configuration;

  StorageType storageType;
  Storage storage;

  public ConfigurationManager(Plugin plugin) {
    this.plugin = plugin;

    reload();
  }

  // Reload config.yml file
  public void reload() {
    plugin.reloadConfig();
    this.configuration = plugin.getConfig();

    // Update storage type
    var storageTypeString = configuration.getString("storage.type");
    try {
      storageType = StorageType.valueOf(storageTypeString.toUpperCase());
    } catch (Exception e) {
      throw new ConfigReloadException("storage type");
    }

    try {
      // Unload current storage
      if (storage != null) {
        storage.onUnload();
        storage = null;
      }

      // Create new storage
      if (storageType == StorageType.YAML) {
        storage = new YamlStorage(plugin.getLogger(), new File(plugin.getDataFolder(), configuration.getString(
                "yaml-storage.path",
                "storage.yml")));
      } else {
        throw new RuntimeException();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new ConfigReloadException(storageType.name() + " storage");
    }

  }

}
