package me.metallicgoat.tweaksaddon.config;

import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.updater.ConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class Config {

    public static File getFile(){
        return new File(MBedwarsTweaksPlugin.getAddon().getDataFolder(), "config.yml");
    }

    public static void save(){
        MBedwarsTweaksPlugin.getAddon().getDataFolder().mkdirs();

        synchronized(Config.class){
            try{
                saveUnchecked();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void saveUnchecked() throws IOException {
        final MBedwarsTweaksPlugin plugin = MBedwarsTweaksPlugin.getInstance();

        final File file = getFile();

        if(!file.exists())
            plugin.copyResource("config.yml", file);

        try {
            ConfigUpdater.update(plugin, "config.yml", file, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        load();
    }

    public static FileConfiguration getConfig(){
        final FileConfiguration config = new YamlConfiguration();

        try{
            config.load(getFile());
        }catch(Exception e){
            e.printStackTrace();
        }

        return config;
    }

    public static void load() {

        final FileConfiguration mainConfig = getConfig();

    }
}
