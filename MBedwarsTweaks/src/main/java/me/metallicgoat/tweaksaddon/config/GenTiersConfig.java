package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.YamlConfigurationDescriptor;
import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierHandler;
import me.metallicgoat.tweaksaddon.utils.Console;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.api.gentiers.GenTierActionType;
import me.metallicgoat.tweaksaddon.utils.Util;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class GenTiersConfig {

  public static Map<Integer, GenTierLevel> gen_tier_levels = new HashMap<>();

  public static void load() {
    synchronized (ConfigLoader.class) {
      try {
        loadUnchecked();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private static void loadUnchecked() throws Exception {
    final String pluginVer = MBedwarsTweaksPlugin.getInstance().getDescription().getVersion();
    final File file = new File(MBedwarsTweaksPlugin.getAddon().getDataFolder(), "gen-tiers.yml");

    if (!file.exists()) {
      save(file, pluginVer);
      return;
    }

    // load it
    final FileConfiguration config = new YamlConfiguration();

    try {
      config.load(file);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // read it
    final String configVer = config.getString("version");
    final ConfigurationSection tiersSection = config.getConfigurationSection("Gen-Tiers");

    if (tiersSection != null) {
      gen_tier_levels.clear();

      int i = 1;
      for (String levelNumString : tiersSection.getKeys(false)) {
        final int levelNum = i++;

        final ConfigurationSection section = tiersSection.getConfigurationSection(levelNumString);

        final String tierName = section.getString("Tier-Name");
        String genTierHandlerId = section.getString("Handler-Id");
        final String typeString = section.getString("Drop-Type");
        final double speed = section.getDouble("Drop-Speed");
        final int limit = section.getInt("Max-Nearby-Items", -1);
        final double time = section.getDouble("Time");
        final String message = section.getString("Message");
        final String soundString = section.getString("Earn-Sound");
        String holoName = section.getString("Holo-Usage");

        {
          // OLD VALUES
          if (holoName == null)
            holoName = section.getString("Tier-Level");

          if (genTierHandlerId == null)
            genTierHandlerId = section.getString("Action");
        }

        // TODO Validate other values not null
        if (genTierHandlerId == null) {
          Console.printConfigWarn("Failed to load tier: [" + tierName + "]. Handler-Id is null", "gen-tiers");
          continue;
        }

        final GenTierHandler handler = Util.getGenTierHandlerById(genTierHandlerId.toLowerCase());

        if (handler == null) {
          Console.printConfigWarn("Failed to load tier: [" + tierName + "]. Handler-Id '" + genTierHandlerId + "' is invalid. Has it been registered?", "gen-tiers");
          continue;
        }

        Sound earnSound = null;

        if (soundString != null)
          earnSound = Helper.get().getSoundByName(soundString);

        if (handler.getActionType() == GenTierActionType.GEN_UPGRADE) {

          final GenTierLevel genTierLevel = new GenTierLevel(
              levelNum,
              tierName,
              holoName,
              typeString,
              handler,
              Duration.ofSeconds((long) (time * 60D)),
              speed,
              limit > 0 ? limit : null,
              message.isEmpty() ? null : message,
              earnSound
          );

          gen_tier_levels.put(levelNum, genTierLevel);

        } else {
          final GenTierLevel genTierLevel = new GenTierLevel(
              levelNum,
              tierName,
              handler,
              Duration.ofSeconds((long) (time * 60D)),
              message.isEmpty() ? null : message,
              earnSound
          );

          gen_tier_levels.put(levelNum, genTierLevel);
        }
      }
    }

    // auto update file if newer version
    if (!pluginVer.equals(configVer)) {
      updateOldConfigs(config);
      save(file, pluginVer);
    }
  }

  private static void save(File file, String pluginVer) throws Exception {
    final YamlConfigurationDescriptor config = new YamlConfigurationDescriptor();

    config.addComment("Used for Auto Updating. Do not Change! (Unless you know what you are doing)");
    config.set("version", pluginVer);

    config.addEmptyLine();
    config.addComment("MAKE SURE Gen-Tiers are enabled in the config.yml!");
    config.addEmptyLine();

    config.addComment("PAPI-Usage (The value used in our PAPI Placeholders during this tier)");
    config.addComment("Holo-Usage (The values used in holos during this tier)");
    config.addComment("Action (gen-upgrade, bed-destroy, sudden-death, game-over)");
    config.addComment("Time (time until action - NOTE time starts after the last action)");
    config.addComment("Message (chat message sent on trigger)");
    config.addComment("Earn-Sound (sound played on trigger) (You have to add this config if you want it)");
    config.addEmptyLine();
    config.addComment("--- Spawner Only --- ");
    config.addComment("Type (Type of spawner that will be updated)");
    config.addComment("Speed (How often an item drops (in seconds))");
    config.addComment("Max-Nearby-Items (how many items the spawner will produce before it goes on hold)");

    config.addEmptyLine();

    int i = 1;
    for (Map.Entry<Integer, GenTierLevel> entry : gen_tier_levels.entrySet()) {

      if (entry.getKey() == null || entry.getValue() == null)
        continue;

      final GenTierLevel level = entry.getValue();
      final String configKey = "Gen-Tiers." + (i++) + ".";

      config.set(configKey + "Handler-Id", level.getHandler().getId());
      config.set(configKey + "Time", level.getTime());
      config.set(configKey + "Tier-Name", level.getTierName());

      if (level.getHandler().getActionType() == GenTierActionType.GEN_UPGRADE) {
        config.set(configKey + "Holo-Usage", level.getHoloName());
        config.set(configKey + "Drop-Type", level.getTypeId());
        config.set(configKey + "Drop-Speed", level.getSpeed());

        if (level.getLimit() != null)
          config.set(configKey + "Max-Nearby-Items", level.getLimit());
      }

      if (level.getEarnMessage() != null)
        config.set(configKey + "Message", level.getEarnMessage());

      if (level.getEarnSound() != null)
        config.set(configKey + "Earn-Sound", level.getEarnSound().name());
    }

    config.save(file);
  }

  private static void updateOldConfigs(FileConfiguration config) {
    // No updates yet :)
  }
}
