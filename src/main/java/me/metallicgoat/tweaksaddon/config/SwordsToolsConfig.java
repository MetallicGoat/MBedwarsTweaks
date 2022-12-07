package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.YamlConfigurationDescriptor;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwordsToolsConfig {

    private static File getFile(){
        return new File(MBedwarsTweaksPlugin.getAddon().getDataFolder(), "swords-tools.yml");
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
        ConfigValue.anti_chest_enabled = config.getBoolean("Anti-Chest.Enabled");
        {
            if(config.contains("Anti-Chest.Materials")) {

                final List<Material> antiChestMaterials = new ArrayList<>();

                for (String materialName : config.getStringList("Anti-Chest.Materials")) {
                    final Material material = Helper.get().getMaterialByName(materialName);

                    if (material != null)
                        antiChestMaterials.add(material);

                }

                ConfigValue.anti_chest_materials = antiChestMaterials;
            }
        }

        ConfigValue.anti_drop_enabled = config.getBoolean("Anti-Drop.Enabled");
        {
            if(config.contains("Anti-Drop.Materials")) {

                final List<Material> antiDropMaterials = new ArrayList<>();

                for (String materialName : config.getStringList("Anti-Drop.Materials")) {
                    final Material material = Helper.get().getMaterialByName(materialName);

                    if (material != null)
                        antiDropMaterials.add(material);

                }

                ConfigValue.anti_drop_materials = antiDropMaterials;
            }
        }

        ConfigValue.degrading_buygroups_enabled = config.getBoolean("Degrading-BuyGroups.Enabled", false);
        if(config.contains("Degrading-BuyGroups.BuyGroups"))
            ConfigValue.degrading_buygroups = config.getStringList("Degrading-BuyGroups.BuyGroups");


        ConfigValue.advanced_tool_replacement_enabled = config.getBoolean("Advanced-Tool-Replacement.Enabled", false);
        ConfigValue.advanced_tool_replacement_force_ordered = config.getBoolean("Advanced-Tool-Replacement.Force-Ordered", false);
        ConfigValue.advanced_tool_replacement_force_ordered_problem = config.getString("Advanced-Tool-Replacement.Force-Ordered-Problem", "&cMissing Problem");
        if(config.contains("Advanced-Tool-Replacement.BuyGroups"))
            ConfigValue.degrading_buygroups = config.getStringList("Advanced-Tool-Replacement.BuyGroups");

        ConfigValue.replace_sword_on_buy_enabled = config.getBoolean("Replace-Sword-On-Buy.Enabled", false);
        ConfigValue.replace_sword_on_buy_all_type = config.getBoolean("Replace-Sword-On-Buy.All-Type", false);

        ConfigValue.always_sword_chest_enabled = config.getBoolean("Always-Sword.Chest-Deposit", false);
        ConfigValue.always_sword_drop_enabled = config.getBoolean("Always-Sword.On-Drop", false);

        if(config.contains("Do-Not-Effect"))
            ConfigValue.tools_swords_do_not_effect = config.getStringList("Do-Not-Effect");

        // auto update file if newer version
        {
            if(MainConfig.CURRENT_CONFIG_VERSION == null) {
                updateV1Configs(config);
                save();
                return;
            }

            if(!MainConfig.CURRENT_CONFIG_VERSION.equals(MainConfig.ADDON_VERSION)) {
                updateV2Configs(config);
                save();
            }
        }
    }

    private static void save() throws Exception{
        final YamlConfigurationDescriptor config = new YamlConfigurationDescriptor();

        config.addComment("Items that cannot be placed inside chests");
        config.set("Anti-Chest.Enabled", ConfigValue.anti_chest_enabled);
        {
            final List<String> materials = new ArrayList<>();

            for(Material mat : ConfigValue.anti_chest_materials){
                materials.add(mat.name());
            }

            config.set("Anti-Chest.Materials", materials);
        }

        config.addEmptyLine();

        config.addComment("Items that cannot be dropped on the ground");
        config.set("Anti-Drop.Enabled", ConfigValue.anti_drop_enabled);
        {
            final List<String> materials = new ArrayList<>();

            for(Material mat : ConfigValue.anti_drop_materials){
                materials.add(mat.name());
            }

            config.set("Anti-Drop.Materials", materials);
        }

        config.addEmptyLine();

        config.addComment("When you die with a tool, you will get one tier lower when you respawn");
        config.set("Degrading-BuyGroups.Enabled", ConfigValue.degrading_buygroups_enabled);
        config.set("Degrading-BuyGroups.BuyGroups", ConfigValue.degrading_buygroups);

        config.addEmptyLine();

        config.addComment("With this feature enabled players can only have one tool of a specific type at a time");
        config.addComment("Players will not be able to downgrade their tool if the is enabled");
        config.addComment("If force ordered is true, players MUST buy every tier in order");
        config.set("Advanced-Tool-Replacement.Enabled", ConfigValue.advanced_tool_replacement_enabled);
        config.set("Advanced-Tool-Replacement.BuyGroups", ConfigValue.advanced_tool_replacement_buygroups);

        config.addEmptyLine();

        config.addComment("Removes a Wooden-Sword if you buy a better sword");
        config.addComment("If 'all-type' is set to TRUE, ALL sword types will get replaced.");
        config.addComment("If false, only wooden swords will get replaced (Like Hypixel)");
        config.set("Replace-Sword-On-Buy.Enabled", ConfigValue.replace_sword_on_buy_enabled);
        config.set("Replace-Sword-On-Buy.All-Type", ConfigValue.replace_sword_on_buy_all_type);

        config.addEmptyLine();

        config.addComment("Chest-Deposit: If you add your sword to a chest, and have no other sword, you will be given a wooden sword");
        config.addComment("On-Drop: Gives players a Wooden Sword if no sword is detected, after they drop a sword");
        config.set("Always-Sword.Chest-Deposit", ConfigValue.always_sword_chest_enabled);
        config.set("Always-Sword.On-Drop", ConfigValue.always_sword_drop_enabled);

        config.addEmptyLine();

        config.addComment("Add Items here that you do not want to be effected by Advanced Swords and Tools");
        config.addComment("For example, A CUSTOM special item that is Golden Sword");
        config.addComment("ADD ITEMS BY THEIR DISPLAY NAME (DO NOT include color codes)");
        config.set("Do-Not-Effect", ConfigValue.tools_swords_do_not_effect);

        config.save(getFile());

    }

    private static void updateV2Configs(FileConfiguration config){
        ConfigValue.degrading_buygroups_enabled = config.getBoolean("Degraded-Tool-BuyGroups", false);

        ConfigValue.always_sword_chest_enabled = config.getBoolean("Always-Sword", false);
        ConfigValue.always_sword_drop_enabled = config.getBoolean("Advanced-Sword-Drop.Enabled", false);
    }

    private static void updateV1Configs(FileConfiguration config){

        if(!isV1Config(config))
            return;

        {
            if(config.contains("Anti-Chest")) {
                ConfigValue.anti_chest_materials.clear();

                for (String materialName : config.getStringList("Anti-Chest")) {
                    final Material material = Helper.get().getMaterialByName(materialName);

                    if (material != null)
                        ConfigValue.anti_chest_materials.add(material);
                }
            }
        }

        {
            if(config.contains("Anti-Drop.List")) {
                ConfigValue.anti_drop_materials.clear();

                for (String materialName : config.getStringList("Anti-Drop.List")) {
                    final Material material = Helper.get().getMaterialByName(materialName);

                    if (material != null)
                        ConfigValue.anti_drop_materials.add(material);
                }
            }
        }
    }

    /*
     *
     * We are doing this because if the
     * main config fails to load the config
     * version will not be set, and therefor
     * we will try and update it as a v1 config
     * when we should not.
     *
     */
    private static boolean isV1Config(FileConfiguration config){

        if(config.contains("Anti-Chest.Enabled"))
            return true;

        if(config.contains("Anti-Drop.List"))
            return true;

        if(config.contains("Advanced-Sword-Drop.List"))
            return true;

        return false;
    }
}
