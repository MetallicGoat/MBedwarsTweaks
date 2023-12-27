package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.YamlConfigurationDescriptor;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import me.metallicgoat.tweaksaddon.utils.Console;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.gentiers.TierAction;
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

      int i = 1;
      for (String levelNumString : tiersSection.getKeys(false)) {
        final Integer levelNum = i++;

        final ConfigurationSection section = tiersSection.getConfigurationSection(levelNumString);

        final String tierName = section.getString("Tier-Name");
        final String actionString = section.getString("Action");
        final String typeString = section.getString("Drop-Type");
        final double speed = section.getDouble("Drop-Speed");
        final int limit = section.getInt("Max-Nearby-Items", -1);
        final double time = section.getDouble("Time");
        final String message = section.getString("Message");
        final String soundString = section.getString("Earn-Sound");
        String tierLevel = section.getString("Holo-Usage");

        {
          // OLD VALUES
          if (tierLevel == null)
            tierLevel = section.getString("Tier-Level");
        }

        // TODO Validate other values not null
        if (actionString == null) {
          Console.printConfigWarn("Failed to load tier: [" + tierName + "]. Action is null", "gen-tiers");
          continue;
        }

        TierAction action = null;

        for (TierAction cAction : TierAction.values()) {
          final String actionId = cAction.getId();

          if (actionId.equalsIgnoreCase(actionString))
            action = cAction;
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
              limit > 0 ? limit : null,
              message,
              earnSound
          );

          gen_tier_levels.put(levelNum, genTierLevel);
        } else {

          final GenTierLevel genTierLevel = new GenTierLevel(
              tierName,
              action,
              time,
              null,
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
    config.addComment("Action (gen-upgrade, bed-destroy, game-over)");
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

      config.set(configKey + "Action", level.getAction().getId());
      config.set(configKey + "Time", level.getTime());
      config.set(configKey + "Tier-Name", level.getTierName());

      if (level.getAction() == TierAction.GEN_UPGRADE) {
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
