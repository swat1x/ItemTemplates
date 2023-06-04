package ru.swat1x.itemtemplates;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.var;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import ru.swat1x.itemtemplates.bstat.Metrics;
import ru.swat1x.itemtemplates.command.TemplateCommand;
import ru.swat1x.itemtemplates.config.ConfigurationManager;
import ru.swat1x.itemtemplates.management.ItemTemplate;
import ru.swat1x.itemtemplates.management.TemplatesManager;
import ru.swat1x.itemtemplates.management.imp.BaseTemplateManager;

import java.util.Arrays;
import java.util.stream.Collectors;

@Plugin(
        name = "ItemTemplates",
        version = "1.0"
)
@Author("swat1x")
@ApiVersion(ApiVersion.Target.v1_16)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemTemplatesPlugin extends JavaPlugin {

  @Getter
  private static ItemTemplatesPlugin instance;

  Metrics metrics;
  ConfigurationManager configurationManager;
  TemplatesManager templatesManager;

  PaperCommandManager commandManager;

  @Override
  public void onLoad() {
    instance = this;
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();

    this.metrics = new Metrics(this, 18549);
    this.configurationManager = new ConfigurationManager(this);
    this.templatesManager = new BaseTemplateManager(this, configurationManager);

    this.commandManager = new PaperCommandManager(this);
    registerCompletionsAndContexts();
    this.commandManager.registerCommand(new TemplateCommand(templatesManager));

    metrics.addCustomChart(new Metrics.SimplePie("storage_type", () -> configurationManager.getStorageType().name()));
  }

  private void registerCompletionsAndContexts() {
    commandManager.getCommandContexts().registerContext(ItemTemplate.class, cr -> {
      var id = cr.popFirstArg().toLowerCase();
      var item = templatesManager.getTemplateMap().get(id);
      if (item == null) throw new InvalidCommandArgument("§cItem with id " + id + " is not present", false);
      return item;
    });
    commandManager.getCommandContexts().registerContext(Enchantment.class, cr -> {
      var name = cr.popFirstArg().toUpperCase();
      var enchantment = Enchantment.getByName(name);
      if (enchantment == null) throw new InvalidCommandArgument("§cUnknown enchantment!", false);
      return enchantment;
    });

    commandManager.getCommandCompletions().registerAsyncCompletion("@enchantments",
            cr -> Arrays.stream(Enchantment.values()).map(Enchantment::getName).collect(Collectors.toList()));
    commandManager.getCommandCompletions().registerAsyncCompletion("@items",
            cr -> templatesManager.getTemplateMap().keySet());
  }

}
