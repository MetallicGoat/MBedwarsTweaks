package me.metallicgoat.tweaksaddon.tweaks.spawners.gentiers;

import de.marcely.bedwars.api.event.player.PlayerTriggerUpgradeEvent;
import de.marcely.bedwars.api.game.upgrade.UpgradeTriggerHandler;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;

public class SuddenDeathUpgrade extends UpgradeTriggerHandler {
  public SuddenDeathUpgrade() {
    super("sudden-death", false, MBedwarsTweaksPlugin.getInstance());
  }

  @Override
  public void onTrigger(PlayerTriggerUpgradeEvent playerTriggerUpgradeEvent) {

  }
}
