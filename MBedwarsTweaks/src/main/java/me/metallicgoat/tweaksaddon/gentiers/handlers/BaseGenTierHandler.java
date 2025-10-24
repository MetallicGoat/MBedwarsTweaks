package me.metallicgoat.tweaksaddon.gentiers.handlers;

import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierHandler;
import org.bukkit.plugin.Plugin;

public abstract class BaseGenTierHandler extends GenTierHandler {

  @Override
  public Plugin getPlugin() {
    return MBedwarsTweaksPlugin.getInstance();
  }
}
