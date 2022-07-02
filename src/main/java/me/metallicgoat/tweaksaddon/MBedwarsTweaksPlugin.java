package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.BedwarsAPI;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.config.SwordsToolsConfig;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.ToolSwordHelper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class MBedwarsTweaksPlugin extends JavaPlugin {

    public static final int MIN_MBEDWARS_API_VER = 11;
    public static final String MIN_MBEDWARS_VER_NAME = "5.0.10";

    private static MBedwarsTweaksPlugin instance;
    private static MBedwarsTweaksAddon addon;

    public static boolean papiEnabled = false;

    public void onEnable() {

        instance = this;

        if(!checkMBedwars()) return;
        if(!registerAddon()) return;


        new Metrics(this, 11928);

        ToolSwordHelper.load();
        MBedwarsTweaksAddon.registerEvents();

        MainConfig.load();
        SwordsToolsConfig.load();
        // TODO Gen-Tiers

        PluginDescriptionFile pdf = this.getDescription();

        log(
                "------------------------------",
                pdf.getName() + " For MBedwars",
                "By: " + pdf.getAuthors(),
                "Version: " + pdf.getVersion(),
                "------------------------------"
        );

        BedwarsAPI.onReady(() -> {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new Placeholders().register();
                papiEnabled = true;
            } else {
                log("PlaceholderAPI Was not Found! PAPI placeholders won't work!");
            }
        });
    }

    public static MBedwarsTweaksPlugin getInstance() {
        return instance;
    }

    public static MBedwarsTweaksAddon getAddon() {
        return addon;
    }

    private boolean checkMBedwars(){
        try{
            final Class<?> apiClass = Class.forName("de.marcely.bedwars.api.BedwarsAPI");
            final int apiVersion = (int) apiClass.getMethod("getAPIVersion").invoke(null);

            if(apiVersion < MIN_MBEDWARS_API_VER)
                throw new IllegalStateException();
        }catch(Exception e){
            getLogger().warning("Sorry, your installed version of MBedwars is not supported. Please install at least v" + MIN_MBEDWARS_VER_NAME);
            Bukkit.getPluginManager().disablePlugin(this);

            return false;
        }

        return true;
    }

    private boolean registerAddon(){
        addon = new MBedwarsTweaksAddon(this);

        if(!addon.register()){
            getLogger().warning("It seems like this addon has already been loaded. Please delete duplicates and try again.");
            Bukkit.getPluginManager().disablePlugin(this);

            return false;
        }

        return true;
    }

    /*
    public boolean copyResource(String internalPath, File out) throws IOException {
        if(!out.exists() || out.length() == 0){
            try(InputStream is = getResource(internalPath)){
                if(is == null){
                    getLogger().warning("Your plugin seems to be broken (Failed to find internal file " + internalPath + ")");
                    return false;
                }

                out.createNewFile();

                try(FileOutputStream os = new FileOutputStream(out)){
                    Helper.get().copy(is, os);
                }

                return true;
            }
        }
        return false;
    }
     */

    private void log(String ...args) {
        for(String s : args)
            getLogger().info(s);
    }
}