package me.metallicgoat.tweaksaddon.integration;

import lombok.Getter;
import me.metallicgoat.tweaksaddon.utils.Console;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.integration.hooks.PrestigesLevelOnExperienceBar;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

@Getter
public enum DependType {
  PLACEHOLDER_API("PlaceholderAPI"),
  HOTBAR_MANAGER("MBedwarsHotbarManager"),
  PRESTIGE_ADDON("PrestigeAddon", PrestigesLevelOnExperienceBar.class);

  @Getter
  private boolean isEnabled = false;
  private final String name;
  private final Class<?>[] listenerClasses;

  DependType(String name) {
    this.name = name;
    this.listenerClasses = null;
  }

  DependType(String name, Class<?>... listener) {
    this.name = name;
    this.listenerClasses = listener;
  }

  // Sets isEnabled state, and registers hooks if they are there
  // TODO Do we need to unregister listeners if plugin is disabled?
  public void tryLoad() {
    final PluginManager manager = Bukkit.getServer().getPluginManager();

    isEnabled = manager.isPluginEnabled(name);

    if (isEnabled && listenerClasses != null) {
      try {
        for (Class<?> listenerClass : listenerClasses)
          manager.registerEvents((Listener) listenerClass.getDeclaredConstructors()[0].newInstance(), MBedwarsTweaksPlugin.getInstance());

      } catch (Exception e) {
        Console.printError("Failed to hook load soft dependency '" + name + "'. This is a bug!");
      }
    }
  }

  public static DependType getTypeByName(String name) {
    for (DependType type : DependType.values())
      if (type.name.equalsIgnoreCase(name))
        return type;

    return null;
  }
}
