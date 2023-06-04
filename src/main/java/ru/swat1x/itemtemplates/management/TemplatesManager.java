package ru.swat1x.itemtemplates.management;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface TemplatesManager {

  Map<String, ItemTemplate> getTemplateMap();

  void update(ItemTemplate template);

  CreationResult create(String id, ItemStack itemStack);

  void remove(ItemTemplate template);

}
