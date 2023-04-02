package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.YamlConfigurationDescriptor;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import me.metallicgoat.tweaksaddon.Console;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.tweaks.spawners.GenTierLevel;
import me.metallicgoat.tweaksaddon.tweaks.spawners.TierAction;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class GenTiersConfig {

  public static HashMap<Integer, GenTierLevel> gen_tier_levels = new HashMap<>();

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
      for (String levelNumString : tiersSection.getKeys(false)) {
        final Integer levelNum = Helper.get().parseInt(levelNumString);

        if (levelNum == null) {
          Console.printConfigWarn("Failed to load tier with level num of: " + levelNumString, "gen-tiers");
          continue;
        }

        final String configKey = "Gen-Tiers." + levelNumString + ".";

        final String tierName = config.getString(configKey + "Tier-Name");
        final String tierLevel = config.getString(configKey + "Tier-Level");
        final String actionString = config.getString(configKey + "Action");
        final String typeString = config.getString(configKey + "Drop-Type");
        final double speed = config.getDouble(configKey + "Drop-Speed");
        final int limit = config.getInt(configKey + "Max-Nearby-Items");
        final long time = config.getLong(configKey + "Time");
        final String message = config.getString(configKey + "Message");
        final String soundString = config.getString(configKey + "Earn-Sound");

        // TODO Validate other values not null
        if (actionString == null) {
          Console.printConfigWarn("Failed to load tier: [" + tierName + "]. Action is null", "gen-tiers");
          continue;
        }

        TierAction action = null;

        for (TierAction action1 : TierAction.values()) {
          final String actionId = action1.getId();

          if (actionId != null && action1.getId().equalsIgnoreCase(actionString)) {
            action = action1;
          }
        }

        if (action == null) {
          Console.printConfigWarn("Failed to load tier: [" + tierName + "]. Action '" + actionString + "' is invalid.", "gen-tiers");
          continue;
        }

        Sound earnSound = null;

        if (soundString != null)
          earnSound = Helper.get().getSoundByName(soundString);

        if (action == TierAction.GEN_UPGRADE) {

          final GenTierLevel genTierLevel = new GenTierLevel(
              tierName,
              tierLevel,
              typeString,
              action,
              time,
              speed,
              limit,
              message,
              earnSound
          );

          gen_tier_levels.put(levelNum, genTierLevel);
        } else {

          final GenTierLevel genTierLevel = new GenTierLevel(
              tierName,
              action == TierAction.BED_DESTROY ? "Bed Gone" : "Game Over",
              action,
              time,
              action == TierAction.BED_DESTROY ? "All beds have been broken" : "Game Ended",
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
    config.addEmptyLine();

    config.addComment("MAKE SURE Gen-Tiers are enabled in the config.yml!");

    config.addEmptyLine();

    config.addComment("Tier-Name (Used in PAPI Placeholders)");
    config.addComment("Tier-Levels (Used in Holos)");
    config.addComment("Action (gen-upgrade, bed-destroy, game-over)");
    config.addComment("Type (Type of spawner that will be updated)");
    config.addComment("Speed (How often an item drops (in seconds))");
    config.addComment("Time (time until action - NOTE time starts after the last action)");
    config.addComment("Message (chat message sent on trigger)");
    config.addComment("Earn-Sound (sound played on trigger) (You have to add this config if you want it)");

    config.addEmptyLine();

    for (Map.Entry<Integer, GenTierLevel> entry : gen_tier_levels.entrySet()) {

      if (entry.getKey() == null || entry.getValue() == null)
        continue;

      final GenTierLevel level = entry.getValue();
      final String configKey = "Gen-Tiers." + entry.getKey() + ".";

      config.set(configKey + "Tier-Name", level.getTierName());
      config.set(configKey + "Tier-Level", level.getTierLevel());
      config.set(configKey + "Action", level.getAction().getId());

      if (level.getAction() == TierAction.GEN_UPGRADE) {
        config.set(configKey + "Drop-Type", level.getTypeId());
        config.set(configKey + "Drop-Speed", level.getSpeed());

        if (level.getLimit() != null)
          config.set(configKey + "Max-Nearby-Items", level.getLimit());
      }

      config.set(configKey + "Time", level.getTime());
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
