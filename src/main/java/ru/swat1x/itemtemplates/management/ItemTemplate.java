package ru.swat1x.itemtemplates.management;

import lombok.var;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.swat1x.itemtemplates.ItemTemplatesPlugin;
import ru.swat1x.itemtemplates.util.TextUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface ItemTemplate {

  String getId();

  ItemStack getBase();

  void setBase(ItemStack baseItem);

  default void editMeta(Consumer<ItemMeta> itemMetaConsumer) {
    var base = getBase();
    var meta = base.getItemMeta();
    itemMetaConsumer.accept(meta);
    base.setItemMeta(meta);
    setBase(base);

    ItemTemplatesPlugin.getInstance().getConfigurationManager().getStorage().saveTemplate(this);
  }

  default Component changeNameAsString(String name) {
    if (name == null) {
      changeName(null);
      return null;
    } else {
      var cn = TextUtil.colorize(name);
      changeName(cn);
      return cn;
    }
  }

  default void changeName(Component name) {
    editMeta(m -> m.displayName(name));
  }

  default void changeLoreAsString(List<String> lore) {
    changeLore(lore.stream().map(TextUtil::colorize).collect(Collectors.toList()));
  }

  default void changeLore(List<Component> lore) {
    editMeta(m -> m.lore(lore));
  }

}
