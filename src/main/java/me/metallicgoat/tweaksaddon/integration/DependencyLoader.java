package me.metallicgoat.tweaksaddon.integration;

import me.metallicgoat.tweaksaddon.Console;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class DependencyLoader implements Listener {

  public static void loadAll() {
    if (Bukkit.getPluginManager().isPluginEnabled("FireBallKnockback")) {
      Console.printInfo("I noticed you are using my Fireball jumping addon. " +
          "As of 5.0.13, you do not need it anymore! Fireball jumping " +
          "is now built into core MBedwars. Features such as throw cooldown and throw " +
          "effects have been added to this addon (MBedwarsTweaks). - MetallicGoat"
      );
    }

    for (DependType type : DependType.values()) {
      type.tryLoad();

      if (type == DependType.PLACEHOLDER_API) {
        if (type.isEnabled())
          new Placeholders(MBedwarsTweaksPlugin.getInstance()).register();
        else
          Console.printInfo("PlaceholderAPI was not Found! PAPI placeholders won't work!");
      }
    }
  }

  private void updateLoadedState(String pluginName, boolean enabled) {
    final DependType type = DependType.getTypeByName(pluginName);

    // Is this one of our soft depends?
    if (type == null)
      return;

    type.tryLoad();
  }

  @EventHandler
  public void onDependLoad(PluginEnableEvent event) {
    updateLoadedState(event.getPlugin().getName(), true);
  }

  @EventHandler
  public void onDependUnload(PluginDisableEvent event) {
    updateLoadedState(event.getPlugin().getName(), false);
  }
}
