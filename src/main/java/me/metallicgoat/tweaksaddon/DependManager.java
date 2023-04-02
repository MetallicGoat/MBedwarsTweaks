package me.metallicgoat.tweaksaddon;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class DependManager implements Listener {

  private static final HashMap<DependType, Boolean> loadedDepends = new HashMap<>();

  public static void load() {
    if (Bukkit.getPluginManager().isPluginEnabled("FireBallKnockback")) {
      Console.printInfo("I noticed you are using my Fireball jumping addon. " +
          "As of 5.0.13, you do not need it anymore! Fireball jumping " +
          "is now built into core MBedwars. Features such as throw cooldown and throw " +
          "effects have been added to this addon (MBedwarsTweaks). - MetallicGoat"
      );
    }

    for (DependType type : DependType.values()) {
      final boolean enabled = Bukkit.getPluginManager().isPluginEnabled(type.getName());

      setLoaded(type, enabled);

      if (type == DependType.PLACEHOLDER_API) {
        if (enabled)
          new Placeholders(MBedwarsTweaksPlugin.getInstance()).register();
        else
          Console.printInfo("PlaceholderAPI was not Found! PAPI placeholders won't work!");
      }
    }
  }

  public static boolean isPresent(DependType type) {
    return loadedDepends.get(type);
  }

  private static void setLoaded(String typeName, boolean enabled) {
    setLoaded(DependType.getTypeByName(typeName), enabled);
  }

  private static void setLoaded(DependType type, boolean enabled) {
    // Check if this is even a dependency
    if (type == null)
      return;

    loadedDepends.put(type, enabled);
  }

  @EventHandler
  public void onDependLoad(PluginEnableEvent event) {
    setLoaded(event.getPlugin().getName(), true);
  }

  @EventHandler
  public void onDependUnload(PluginDisableEvent event) {
    setLoaded(event.getPlugin().getName(), false);
  }
}
