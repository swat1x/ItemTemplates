package ru.swat1x.itemtemplates.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class TextUtil {

  private final Pattern HEX_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");

  public Component colorize(String message) {
    String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    int subVersion = Integer.parseInt(version.replace("1_", "").replaceAll("_R\\d", "").replace("v", ""));

    if (subVersion >= 16) {
      Matcher matcher = HEX_PATTERN.matcher(message);

      while (matcher.find()) {
        String color = message.substring(matcher.start() + 1, matcher.end());
        message = message.replace("&" + color, String.valueOf(ChatColor.of(color)));
        matcher = HEX_PATTERN.matcher(message);
      }
    }
    message = ChatColor.translateAlternateColorCodes('&', message);
    return LegacyComponentSerializer.legacySection().deserialize(message)
            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
  }

}
