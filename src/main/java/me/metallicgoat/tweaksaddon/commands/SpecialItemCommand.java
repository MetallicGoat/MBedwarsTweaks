package me.metallicgoat.tweaksaddon.commands;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.command.CommandHandler;
import de.marcely.bedwars.api.command.SubCommand;
import java.util.ArrayList;
import java.util.List;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class SpecialItemCommand implements CommandHandler {


  @Override
  public Plugin getPlugin() {
    return MBedwarsTweaksPlugin.getInstance();
  }

  @Override
  public void onRegister(SubCommand subCommand) {

  }

  @Override
  public void onFire(CommandSender commandSender, String s, String[] strings) {
    final StringBuilder builder = new StringBuilder();

    // Send him a list of special items.
    if (strings.length > 0)
      return;

    GameAPI.get().getSpecialItems().forEach(specialItem -> builder.append(specialItem.getId()).append(", "));
    commandSender.sendMessage(builder.toString());

  }

  @Override
  public @Nullable List<String> onAutocomplete(CommandSender commandSender, String[] strings) {
    return new ArrayList<>();
  }
}
