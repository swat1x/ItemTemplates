package ru.swat1x.itemtemplates.management.imp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.ItemStack;
import ru.swat1x.itemtemplates.management.ItemTemplate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BaseTemplate implements ItemTemplate {

  final String id;
  ItemStack base;

}
