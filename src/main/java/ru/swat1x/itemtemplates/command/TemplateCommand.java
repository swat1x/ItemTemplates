package ru.swat1x.itemtemplates.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.var;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.swat1x.itemtemplates.management.ItemTemplate;
import ru.swat1x.itemtemplates.management.TemplatesManager;
import ru.swat1x.itemtemplates.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@CommandAlias("templates")
@CommandPermission("it.command")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TemplateCommand extends BaseCommand {

  TemplatesManager manager;

  @Default
  public void onDefault(CommandSender sender) {

    sender.sendMessage("§7[§aItemTemplates§7]");
    sender.sendMessage(" §6/templates create <id> §8- §7Create item template");
    sender.sendMessage(" §6/templates remove <id> §8- §7Remove item template");
    sender.sendMessage(" §6/templates update <id> §8- §7Update item template");
    sender.sendMessage(" §6/templates get <id> §8- §7Give item to yourself");
    sender.sendMessage(" §6/templates give <id> <player> (-s) §8- §7Give item to player");
    sender.sendMessage(" §6/templates edit <id> (...) §8- §7Edit item");

  }

  @Subcommand("create")
  @Syntax("<id>")
  public void onCreate(Player sender, String id) {
    var itemInHand = sender.getInventory().getItemInMainHand();

    if (itemInHand.getType().isAir()) {
      sender.sendMessage("§cYou have to take any item in hand");
      return;
    }

    var result = manager.create(id, itemInHand);
    if (result.isSuccess()) {
      var template = result.getTemplate();
      var templateId = template.getId();
      sender.sendMessage("§aItem with id '" + templateId + "' was successfully created! See /templates edit " + templateId);
    } else {
      sender.sendMessage("§cSomething went wrong! See more information in console");
    }
  }

  @Subcommand("remove")
  @Syntax("<id>")
  @CommandCompletion("@items")
  public void onRemove(CommandSender sender, ItemTemplate template) {
    manager.remove(template);
    sender.sendMessage("§aItem successfully removed!");
  }

  @Subcommand("update")
  @Syntax("<id>")
  @CommandCompletion("@items")
  public void onUpdate(Player sender, ItemTemplate template) {
    var itemInHand = sender.getInventory().getItemInMainHand();

    if (itemInHand.getType().isAir()) {
      sender.sendMessage("§cYou have to take any item in hand");
      return;
    }

    template.setBase(itemInHand);
    manager.update(template);
    sender.sendMessage("§aItem successfully updated!");
  }

  @Subcommand("give")
  @Syntax("<id> <player> (-s)")
  @CommandCompletion("@items @players -s")
  public void onGive(CommandSender sender, ItemTemplate template, OnlinePlayer player, @Optional String silentString) {
    boolean silent = silentString != null && silentString.equalsIgnoreCase("-s");
    player.getPlayer().getInventory().addItem(template.getBase());
    if (!silent) {
      sender.sendMessage("§aItem '" + template.getId() + "' given to player " + player.getPlayer().getName());
    }
  }

  @Subcommand("get")
  @Syntax("<id>")
  @CommandCompletion("@items")
  public void onGet(Player sender, ItemTemplate template) {
    sender.getInventory().addItem(template.getBase());
    sender.sendMessage("§aItem '" + template.getId() + "' given in your inventory");
  }

  @Subcommand("edit")
  public class EditCommand extends BaseCommand {

    @Default
    @CatchUnknown
    public void onDefault(Player sender) {
      sender.sendMessage("§cInsert item id");
    }

    @Subcommand("name")
    @Syntax("<id> (name)")
    @CommandCompletion("@items (name)")
    public void setName(CommandSender sender, ItemTemplate item, @Optional String name) {
      if (name == null) {
        item.changeName(null);
        sender.sendMessage("§eName of item '" + item.getId() + "' removed!");
      } else {
        var cn = item.changeNameAsString(name);
        sender.sendMessage(Component.text("§eChanged name of item " + item.getId() + " to ").append(cn));
      }
    }

    @Subcommand("addloreline")
    @Syntax("<id> <line>")
    @CommandCompletion("@items <line>")
    public void onAddLoreLine(CommandSender sender, ItemTemplate item, String line) {
      var lore = item.getBase().getItemMeta().lore();
      if (lore == null) {
        lore = new ArrayList<>();
      }

      var formatLine = TextUtil.colorize(line);
      lore.add(formatLine);
      item.changeLore(lore);

      sendUpdateMessage(sender, "Added line to lore", lore);
    }

    @Subcommand("removeloreline")
    @Syntax("<id> <line number>")
    @CommandCompletion("@items <line-number>")
    public void onAddLoreLine(CommandSender sender, ItemTemplate item, Integer lineNumber) {
      makeActionsWithLore(sender, item, lineNumber,
              lore -> lore.remove(lineNumber - 1),
              "Remove line " + lineNumber + " from lore");
    }

    @Subcommand("setloreline")
    @Syntax("<id> <line number> <line>")
    @CommandCompletion("@items <line-number> <line>")
    public void onSetLoreLine(CommandSender sender, ItemTemplate item, Integer lineNumber, String loreString) {
      makeActionsWithLore(sender, item, lineNumber,
              lore -> {
                var formatLine = TextUtil.colorize(loreString);
                lore.set(lineNumber - 1, formatLine);
              }, "Updated line number " + lineNumber);
    }

    private void makeActionsWithLore(CommandSender sender,
                                     ItemTemplate item,
                                     int lineNumber,
                                     Consumer<List<Component>> consumer,
                                     String message) {
      if (lineNumber <= 0) {
        sender.sendMessage("§cInsert correct line number");
        return;
      }

      var lore = item.getBase().getItemMeta().lore();
      if (lore == null || lore.size() < lineNumber) {
        sender.sendMessage("Lore is not present or line number too big");
        return;
      }

      consumer.accept(lore);
      item.changeLore(lore);

      sendUpdateMessage(sender, message, lore);
    }

    private void sendUpdateMessage(CommandSender sender, String message, List<Component> lore) {
      message = "§e" + message;
      if (lore.isEmpty()) {
        sender.sendMessage(message);
      } else {
        sender.sendMessage(message + ". Current lore:");
        lore.forEach(sender::sendMessage);
      }
    }

  }

}
