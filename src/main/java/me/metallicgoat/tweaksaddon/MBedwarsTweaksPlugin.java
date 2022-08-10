package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.BedwarsAPI;
import lombok.Getter;
import lombok.Setter;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.ToolSwordHelper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class MBedwarsTweaksPlugin extends JavaPlugin {

    public static final int MIN_MBEDWARS_API_VER = 11;
    public static final String MIN_MBEDWARS_VER_NAME = "5.0.10";

    @Getter
    private static MBedwarsTweaksPlugin instance;
    @Getter
    private static MBedwarsTweaksAddon addon;
    @Getter @Setter
    private boolean hotbarManagerEnabled = false;

    public static boolean papiEnabled = false;

    public void onEnable() {

        instance = this;

        if (!checkMBedwars()) return;
        if (!registerAddon()) return;


        new Metrics(this, 11928);

        ToolSwordHelper.load();
        MBedwarsTweaksAddon.registerEvents();

        PluginDescriptionFile pdf = this.getDescription();

        Console.printInfo(
                "------------------------------",
                pdf.getName() + " For MBedwars",
                "By: " + pdf.getAuthors(),
                "Version: " + pdf.getVersion(),
                "------------------------------"
        );

        BedwarsAPI.onReady(() -> {

            LoadConfigs.loadTweaksConfigs();

            if(Bukkit.getPluginManager().isPluginEnabled("MBedwarsHotbarManager"))
                setHotbarManagerEnabled(true);

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new Placeholders().register();
                papiEnabled = true;
            } else {
                Console.printInfo("PlaceholderAPI was not Found! PAPI placeholders won't work!");
            }

            if (Bukkit.getPluginManager().isPluginEnabled("FireBallKnockback")) {
                Console.printInfo("I noticed you are using my Fireball jumping addon. " +
                        "As of 5.0.13, you do not need to anymore! Fireball jumping " +
                        "is now built into MBedwars, and cooldown + throw effect " +
                        "features have been added to this addon (MBedwarsTweaks)."
                );
            }
        });
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