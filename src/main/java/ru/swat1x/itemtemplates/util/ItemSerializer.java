package ru.swat1x.itemtemplates.util;

import lombok.experimental.UtilityClass;
import lombok.var;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@UtilityClass
public class ItemSerializer {

  public String encode(ItemStack item) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      BukkitObjectOutputStream bukkitOutput = new BukkitObjectOutputStream(outputStream);

      bukkitOutput.writeObject(item);

      bukkitOutput.close();
      return Base64Coder.encodeLines(outputStream.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public ItemStack decode(String baseString) {
    try {
      var outputStream = new ByteArrayInputStream(Base64Coder.decodeLines(baseString));
      var bukkitInput = new BukkitObjectInputStream(outputStream);

      var item = bukkitInput.readObject();

      bukkitInput.close();
      return (ItemStack) item;
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

}
