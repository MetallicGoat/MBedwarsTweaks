package me.metallicgoat.tweaksaddon.gentiers.dragons;

import de.marcely.bedwars.api.event.player.PlayerTriggerUpgradeEvent;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandler;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.gentiers.GenTiers;
import me.metallicgoat.tweaksaddon.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SuddenDeathUpgrade extends UpgradeTriggerHandler {
  public SuddenDeathUpgrade() {
    super("sudden-death", false, MBedwarsTweaksPlugin.getInstance());
  }

  @Override
  public void onTrigger(PlayerTriggerUpgradeEvent event) {
    if (event.isCancelled())
      return;

    if (!MainConfig.sudden_death_dragons_enabled) {
      event.getPlayer().sendMessage(ChatColor.RED + "WARNING: Sudden death dragons are disabled in the tweaks config! Report this issue to the server admin!");
      return;
    }

    GenTiers.getState(event.getArena()).addDragonTeam(event.getTeam());
  }

  @Override
  public String getName(CommandSender sender) {
    return Message.buildByKey("Tweaks_SuddenDeath_Upgrade_Name").done(sender, false);
  }

  @Override
  public List<String> getDescriptionLines(CommandSender sender) {
    final String msg = Message.buildByKey("Tweaks_SuddenDeath_Upgrade_Description").done(sender, false);

    return msg != null ? Util.getLines(msg) : new ArrayList<>();
  }
}
