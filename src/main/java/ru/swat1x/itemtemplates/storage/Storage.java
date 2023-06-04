package ru.swat1x.itemtemplates.storage;

import ru.swat1x.itemtemplates.management.ItemTemplate;

import java.util.Map;
import java.util.Optional;

public interface Storage {

  default void onUnload() {

  }

  Map<String, ItemTemplate> loadAll();

  void saveTemplate(ItemTemplate template);

  void deleteTemplate(ItemTemplate template);

  Optional<ItemTemplate> loadById(String id);

}
