package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.YamlConfigurationDescriptor;
import me.metallicgoat.tweaksaddon.Console;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.Util;
import me.metallicgoat.tweaksaddon.tweaks.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.tweaks.gentiers.TierAction;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

public class GenTiersConfig {

    private static File getFile(){
        return new File(MBedwarsTweaksPlugin.getAddon().getDataFolder(), "gen-tiers.yml");
    }

    // TODO ensure tiers are numbered correctly

    public static void load(){
        synchronized(MainConfig.class){
            try{
                loadUnchecked();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void loadUnchecked() throws Exception {

        final File file = getFile();

        if(!file.exists()){
            save();
            return;
        }

        // load it
        final FileConfiguration config = new YamlConfiguration();

        try{
            config.load(file);
        }catch(Exception e){
            e.printStackTrace();
        }

        // read it

        final ConfigurationSection tiersSection = config.getConfigurationSection("Gen-Tiers");

        if (tiersSection != null) {
            ConfigValue.gen_tier_levels.clear();
            for (String levelNumString : tiersSection.getKeys(false)) {
                final Integer levelNum = Helper.get().parseInt(levelNumString);

                if (levelNum == null){
                    Console.printConfigWarn("Failed to load tier with level num of: " + levelNumString, "gen-tiers");
                    continue;
                }

                final String configKey = "Gen-Tiers." + levelNumString + ".";

                final String tierName = config.getString(configKey + "Tier-Name");
                final String tierLevel = config.getString(configKey + "Tier-Level");
                final String actionString = config.getString(configKey + "Action");
                final String typeString = config.getString(configKey + "Drop-Type");
                final double speed = config.getDouble(configKey + "Drop-Speed");
                final long time = config.getLong(configKey + "Time");
                final String message = config.getString(configKey + "Message");
                final String soundString = config.getString(configKey + "Earn-Sound");

                // TODO Validate other values not null

                if(actionString == null) {
                    Console.printConfigWarn("Failed to load tier: [" + tierName + "]. Action is null", "gen-tiers");
                    continue;
                }

                TierAction action = null;

                for (TierAction action1 : TierAction.values()){
                    final String actionId = action1.getId();

                    if(actionId != null && action1.getId().equalsIgnoreCase(actionString)){
                        action = action1;
                    }
                }

                if(action == null){
                    Console.printConfigWarn("Failed to load tier: [" + tierName + "]. Action '" + actionString + "' is invalid.", "gen-tiers");
                    continue;
                }

                Sound earnSound = null;

                if(soundString != null)
                    earnSound = Helper.get().getSoundByName(soundString);

                if(action == TierAction.GEN_UPGRADE) {

                    final DropType dropType = Util.getDropType(typeString);

                    if (dropType == null){
                        Console.printConfigWarn("Failed to load gen-tier + [" + tierName + "]. '" + typeString + "' is not a valid DropType", "gen-tiers");
                        continue;
                    }

                    final GenTierLevel genTierLevel = new GenTierLevel(
                            tierName,
                            tierLevel,
                            dropType,
                            action,
                            time,
                            speed,
                            message,
                            earnSound
                    );

                    ConfigValue.gen_tier_levels.put(levelNum, genTierLevel);
                } else {

                    final GenTierLevel genTierLevel = new GenTierLevel(
                            tierName,
                            action == TierAction.BED_DESTROY ? "Bed Gone" : "Game Over",
                            action,
                            time,
                            action == TierAction.BED_DESTROY ? "All beds have been broken" : "Game Ended",
                            earnSound
                    );

                    ConfigValue.gen_tier_levels.put(levelNum, genTierLevel);
                }
            }
        }


        // auto update file if newer version
        {
            if(MainConfig.CURRENT_VERSION == -1) {
                updateV1Configs(config);
                save();
                return;
            }

            if(MainConfig.CURRENT_VERSION != MainConfig.VERSION) {
                updateV2Configs(config);
                save();
            }
        }
    }

    private static void save() throws Exception{
        final YamlConfigurationDescriptor config = new YamlConfigurationDescriptor();

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

        for(Map.Entry<Integer, GenTierLevel> entry : ConfigValue.gen_tier_levels.entrySet()){

            if(entry.getKey() == null || entry.getValue() == null)
                continue;

            final GenTierLevel level = entry.getValue();
            final String configKey = "Gen-Tiers." + entry.getKey() + ".";

            config.set(configKey + "Tier-Name", level.getTierName());
            config.set(configKey + "Tier-Level", level.getTierLevel());
            config.set(configKey + "Action", level.getAction().getId());

            if(level.getAction() == TierAction.GEN_UPGRADE) {
                config.set(configKey + "Drop-Type", level.getType().getId());
                config.set(configKey + "Drop-Speed", level.getSpeed());
            }

            config.set(configKey + "Time", level.getTime());
            config.set(configKey + "Message", level.getEarnMessage());

            if(level.getEarnSound() != null)
                config.set(configKey + "Earn-Sound", level.getEarnSound());
        }

        config.save(getFile());

    }

    private static void updateV2Configs(FileConfiguration config){
        // No updates yet :)
    }

    private static void updateV1Configs(FileConfiguration config){
        final ConfigurationSection tiersSection = config.getConfigurationSection("Gen-Tiers");

        int val = -1;
        GenTierLevel bedBreakTier = null;
        GenTierLevel gameOverTier = null;

        if (tiersSection != null) {
            for (String levelNumString : tiersSection.getKeys(false)) {

                final String configKey = "Gen-Tiers." + levelNumString + ".";

                final String tierName = config.getString(configKey + "TierName");
                final String tierLevel = config.getString(configKey + "TierLevel");
                final String typeString = config.getString(configKey + "Type");
                final long time = config.getLong(configKey + "Time", 8);
                final double speed = config.getDouble(configKey + "Speed", 8.0);
                final String message = config.getString(configKey + "Chat");

                if(tierName == null){
                    Console.printConfigWarn("Failed to load tier with level num of: " + levelNumString, "gen-tiers");
                    continue;
                }

                if(levelNumString.equalsIgnoreCase("bed-break"))
                    bedBreakTier = new GenTierLevel(tierName, tierLevel != null ? tierLevel : "Bed Gone", TierAction.BED_DESTROY, time, message != null ? message : "All beds have been broken", null);

                if(levelNumString.equalsIgnoreCase("game-over"))
                    gameOverTier = new GenTierLevel(tierName, tierLevel != null ? tierLevel : "Game Over", TierAction.GAME_OVER, time, message != null ? message : "Game Ended", null);

                final Integer levelNum = Helper.get().parseInt(levelNumString);

                if (levelNum == null){
                    Console.printConfigWarn("Failed to load tier: [" + tierName  + "]. Level number is null.", "gen-tiers");
                    continue;
                }

                if(levelNum > val)
                    val = levelNum;

                final DropType dropType = Util.getDropType(typeString);

                if (dropType == null){
                    Console.printConfigWarn("Failed to load gen-tier + [" + tierName + "]. '" + typeString + "' is not a valid DropType", "gen-tiers");
                    continue;
                }

                final GenTierLevel genTierLevel = new GenTierLevel(
                        tierName,
                        tierLevel,
                        dropType,
                        TierAction.GEN_UPGRADE,
                        time,
                        speed,
                        message,
                        null
                );

                ConfigValue.gen_tier_levels.put(levelNum, genTierLevel);
            }
        }

        if(bedBreakTier != null){
            val++;
            ConfigValue.gen_tier_levels.put(val, bedBreakTier);
        }

        if(gameOverTier != null){
            val++;
            ConfigValue.gen_tier_levels.put(val, gameOverTier);
        }
    }
}
