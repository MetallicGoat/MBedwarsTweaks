package me.metallicgoat.tweaksaddon.gentiers.dragons;

import de.marcely.bedwars.api.event.player.PlayerTriggerUpgradeEvent;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandler;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.gentiers.GenTiers;
import org.bukkit.ChatColor;

public class SuddenDeathUpgrade extends UpgradeTriggerHandler {
  public SuddenDeathUpgrade() {
    super("sudden-death", false, MBedwarsTweaksPlugin.getInstance());
  }

  @Override
  public void onTrigger(PlayerTriggerUpgradeEvent event) {
    if (event.isCancelled())
      return;

    if (!MainConfig.sudden_death_dragons_enabled){
      event.getPlayer().sendMessage(ChatColor.RED + "WARNING: Sudden death dragons are disabled in the tweaks config! Report this issue to the server admin!");
      return;
    }

    GenTiers.getState(event.getArena()).addDragonTeam(event.getTeam());
  }
}
