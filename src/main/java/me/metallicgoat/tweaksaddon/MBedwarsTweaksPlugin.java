package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.BedwarsAPI;
import lombok.Getter;
import me.metallicgoat.tweaksaddon.config.ConfigLoader;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.gentiers.dragons.DragonUtil;
import me.metallicgoat.tweaksaddon.integration.DependencyLoader;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.ToolSwordHelper;
import me.metallicgoat.tweaksaddon.utils.Console;
import me.metallicgoat.tweaksaddon.utils.Metrics;
import me.metallicgoat.tweaksaddon.utils.NMSClass;
import me.metallicgoat.tweaksaddon.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class MBedwarsTweaksPlugin extends JavaPlugin {

  public static final int MIN_MBEDWARS_API_VER = 200;
  public static final String MIN_MBEDWARS_VER_NAME = "5.5";

  @Getter
  private static MBedwarsTweaksPlugin instance;
  @Getter
  private static MBedwarsTweaksAddon addon;

  @Override
  public void onEnable() {
    instance = this;

    if (!checkMBedwars())
      return;
    if (!registerAddon())
      return;

    new Metrics(this, 11928);

    addon.registerMessageMappings();
    addon.registerEvents();
    addon.registerUpgrades();
    addon.registerCommands();

    final PluginDescriptionFile pdf = this.getDescription();

    Console.printInfo(
        "------------------------------",
        pdf.getName() + " For MBedwars",
        "By: " + pdf.getAuthors(),
        "Version: " + pdf.getVersion(),
        "------------------------------"
    );

    loadTweaks();
  }

  public void loadTweaks() {
    BedwarsAPI.onReady(() -> {
      DependencyLoader.loadAll();
      ConfigLoader.loadTweaksConfigs(this);
      ToolSwordHelper.load();

      if (MainConfig.disable_dragon_death_sound) {
        try {
          NMSClass.disableDragonSound();
        } catch (Exception e) {
          Console.printWarn(
              "============================================",
              "Failed to disable dragon death sounds! You may want to disable them in your spigot.yml",
              "============================================"
          );
        }
      }

      // Check Update Async
      if (MainConfig.check_update_on_load)
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> UpdateChecker.checkForUpdate(this.getDescription().getVersion()));

    });
  }

  @Override
  public void onDisable() {
    DragonUtil.killAllDragons();
  }

  private boolean checkMBedwars() {
    try {
      final Class<?> apiClass = Class.forName("de.marcely.bedwars.api.BedwarsAPI");
      final int apiVersion = (int) apiClass.getMethod("getAPIVersion").invoke(null);

      if (apiVersion < MIN_MBEDWARS_API_VER)
        throw new IllegalStateException();
    } catch (Exception e) {
      getLogger().warning("Sorry, your installed version of MBedwars is not supported. Please install at least v" + MIN_MBEDWARS_VER_NAME);
      Bukkit.getPluginManager().disablePlugin(this);

      return false;
    }

    return true;
  }

  private boolean registerAddon() {
    addon = new MBedwarsTweaksAddon(this);

    if (!addon.register()) {
      getLogger().warning("It seems like this addon has already been loaded. Please delete duplicates and try again.");
      Bukkit.getPluginManager().disablePlugin(this);

      return false;
    }

    return true;
  }
}