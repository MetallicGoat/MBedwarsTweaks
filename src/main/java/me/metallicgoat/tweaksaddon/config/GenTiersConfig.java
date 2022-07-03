package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.YamlConfigurationDescriptor;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.Util;
import me.metallicgoat.tweaksaddon.tweaks.gentiers.GenTierLevel;
import me.metallicgoat.tweaksaddon.tweaks.gentiers.TierAction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

public class GenTiersConfig {

    private static File getFile(){
        return new File(MBedwarsTweaksPlugin.getAddon().getDataFolder(), "gen-tiers.yml");
    }

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
        ConfigValue.gen_tier_levels.clear();

        final ConfigurationSection tiersSection = config.getConfigurationSection("Gen-Tiers");

        if (tiersSection != null) {
            for (String levelNumString : tiersSection.getKeys(false)) {
                final Integer levelNum = Helper.get().parseInt(levelNumString);

                if (levelNum == null){
                    // TODO Log issue
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

                // TODO Validate other values not null
                if(actionString == null) {
                    // TODO Log issues
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
                    // TODO Log issues
                    continue;
                }

                if(action == TierAction.GEN_UPGRADE) {

                    final DropType dropType = Util.getDropType(typeString);

                    if (dropType == null){
                        // TODO Log Issues
                        continue;
                    }

                    final GenTierLevel genTierLevel = new GenTierLevel(
                            tierName,
                            tierLevel,
                            dropType,
                            action,
                            time,
                            speed,
                            message
                    );

                    ConfigValue.gen_tier_levels.put(levelNum, genTierLevel);
                } else {

                    final GenTierLevel genTierLevel = new GenTierLevel(
                            tierName,
                            action == TierAction.BED_DESTROY ? "Bed Gone" : "Game Over",
                            action,
                            time,
                            action == TierAction.BED_DESTROY ? "All beds have been broken" : "Game Ended"
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
        }

        config.save(getFile());

    }

    private static void updateV2Configs(FileConfiguration config){
        // No updates yet :)
    }

    // TODO fix the major issue
    private static void updateV1Configs(FileConfiguration config){
        final ConfigurationSection tiersSection = config.getConfigurationSection("Gen-Tiers");

        int lastVal = -1;

        if (tiersSection != null) {
            for (String levelNumString : tiersSection.getKeys(false)) {

                final String configKey = "Gen-Tiers." + levelNumString + ".";

                final String tierName = config.getString(configKey + "TierName");
                final String tierLevel = config.getString(configKey + "TierLevel");
                final String typeString = config.getString(configKey + "Type");
                final long time = config.getLong(configKey + "Time");
                final double speed = config.getDouble(configKey + "Speed");
                final String message = config.getString(configKey + "Chat");

                if(tierName == null){
                    // TODO Log issues
                    continue;
                }

                if(levelNumString.equalsIgnoreCase("bed-break")){
                    lastVal++;

                    if(ConfigValue.gen_tier_levels.containsKey(lastVal)){
                        // TODO log issues
                        continue;
                    }

                    ConfigValue.gen_tier_levels.put(lastVal, new GenTierLevel(tierName, tierLevel, TierAction.BED_DESTROY, time, message));
                }

                if(levelNumString.equalsIgnoreCase("game-over")){
                    lastVal++;

                    if(ConfigValue.gen_tier_levels.containsKey(lastVal)){
                        // TODO log issues
                        continue;
                    }

                    ConfigValue.gen_tier_levels.put(lastVal, new GenTierLevel(tierName, tierLevel, TierAction.GAME_OVER, time, message));
                }

                final Integer levelNum = Helper.get().parseInt(levelNumString);

                if (levelNum == null){
                    // TODO Log issue
                    continue;
                }

                final DropType dropType = Util.getDropType(typeString);

                if (dropType == null){
                    // TODO Log Issues
                    continue;
                }

                final GenTierLevel genTierLevel = new GenTierLevel(
                        tierName,
                        tierLevel,
                        dropType,
                        TierAction.GEN_UPGRADE,
                        time,
                        speed,
                        message
                );

                ConfigValue.gen_tier_levels.put(levelNum, genTierLevel);
            }
        }
    }
}