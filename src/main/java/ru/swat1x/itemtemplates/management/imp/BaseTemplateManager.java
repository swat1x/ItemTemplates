package ru.swat1x.itemtemplates.management.imp;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.var;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.swat1x.itemtemplates.config.ConfigurationManager;
import ru.swat1x.itemtemplates.management.CreationResult;
import ru.swat1x.itemtemplates.management.ItemTemplate;
import ru.swat1x.itemtemplates.management.TemplatesManager;

import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BaseTemplateManager implements TemplatesManager {

  Plugin plugin;
  ConfigurationManager configuration;
  Map<String, ItemTemplate> templateMap;

  public BaseTemplateManager(Plugin plugin, ConfigurationManager configuration) {
    this.plugin = plugin;
    this.configuration = configuration;
    this.templateMap = configuration.getStorage().loadAll();
  }

  @Override
  public void update(ItemTemplate template) {
    configuration.getStorage().saveTemplate(template);
    templateMap.put(template.getId(), template);
  }

  @Override
  public CreationResult create(String id, ItemStack itemStack) {
    try {
      var template = new BaseTemplate(id, itemStack);
      configuration.getStorage().saveTemplate(template);
      templateMap.put(id, template);
      return CreationResult.success(template);
    } catch (Exception e) {
      e.printStackTrace();
      return CreationResult.failed();
    }
  }

  @Override
  public void remove(ItemTemplate template) {
    templateMap.remove(template.getId());
    configuration.getStorage().deleteTemplate(template);
  }

}
