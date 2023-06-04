package ru.swat1x.itemtemplates.storage.imp;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.var;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.swat1x.itemtemplates.management.ItemTemplate;
import ru.swat1x.itemtemplates.management.imp.BaseTemplate;
import ru.swat1x.itemtemplates.storage.Storage;
import ru.swat1x.itemtemplates.util.ItemSerializer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class YamlStorage implements Storage {

  private static final String LOG_PREFIX = "[TemplatesStorage | Yaml]";

  Logger logger;

  File file;
  FileConfiguration storageFile;

  public YamlStorage(Logger logger, File file) {
    this.file = file;
    this.logger = logger;
    storageFile = loadConfig(file);
  }

  private FileConfiguration loadConfig(File file) {
    if (!file.exists()) {
      logger.info(LOG_PREFIX + " Try to create storage file at " + file.getAbsolutePath());
      try {
        file.createNewFile();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return YamlConfiguration.loadConfiguration(file);
  }

  @Override
  public Map<String, ItemTemplate> loadAll() {
    var templateMap = new HashMap<String, ItemTemplate>();
    var section = storageFile.getConfigurationSection("data");
    if (section == null) return templateMap;
    for (String id : section.getKeys(false)) {
      templateMap.put(id, loadById(id).get());
    }
    return templateMap;
  }

  @Override
  public void saveTemplate(ItemTemplate template) {
    var path = "data." + template.getId();
    storageFile.set(path, ItemSerializer.encode(template.getBase()));
    save();
    logger.info(LOG_PREFIX + " Item '" + template.getId() + "' saved to storage");
  }

  @Override
  public void deleteTemplate(ItemTemplate template) {
    storageFile.set("data." + template.getId(), null);
    save();
    logger.info(LOG_PREFIX + " Item '" + template.getId() + "' removed from storage");
  }

  @Override
  public Optional<ItemTemplate> loadById(String id) {
    id = id.toLowerCase();
    var baseString = storageFile.getString("data." + id);
    if (baseString == null) return Optional.empty();
    var path = "data." + id;
    var itemStack = ItemSerializer.decode(storageFile.getString(path));
    logger.info(LOG_PREFIX + " Item '" + id + "' loaded from storage");
    return Optional.of(new BaseTemplate(id, itemStack));
  }

  private void save() {
    try {
      storageFile.save(file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
