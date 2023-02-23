package me.metallicgoat.tweaksaddon;

import de.marcely.bedwars.api.BedwarsAPI;
import lombok.Getter;
import lombok.Setter;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import me.metallicgoat.tweaksaddon.config.MainConfig;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.ToolSwordHelper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class MBedwarsTweaksPlugin extends JavaPlugin {

    public static final int MIN_MBEDWARS_API_VER = 15;
    public static final String MIN_MBEDWARS_VER_NAME = "5.1";

    @Getter private static MBedwarsTweaksPlugin instance;
    @Getter private static MBedwarsTweaksAddon addon;
    @Getter @Setter private boolean hotbarManagerEnabled = false;
    @Getter @Setter private boolean prestigesAddonEnabled = false;

    public static boolean papiEnabled = false;

    public void onEnable() {

        instance = this;

        if (!checkMBedwars()) return;
        if (!registerAddon()) return;

        new Metrics(this, 11928);

        MBedwarsTweaksAddon.registerEvents();

        final PluginDescriptionFile pdf = this.getDescription();

        Console.printInfo(
                "------------------------------",
                pdf.getName() + " For MBedwars",
                "By: " + pdf.getAuthors(),
                "Version: " + pdf.getVersion(),
                "------------------------------"
        );

        BedwarsAPI.onReady(() -> {

            LoadConfigs.loadTweaksConfigs();
            ToolSwordHelper.load();

            // TODO - Move to a single depend manager class
            {
                if (isEnabled("MBedwarsHotbarManager"))
                    setHotbarManagerEnabled(true);

                if (isEnabled("PrestigeAddon"))
                    setPrestigesAddonEnabled(true);
                if (isEnabled("PrivateGamesAddon"))
                    ConfigValue.private_game_addon_support = true;

                if (isEnabled("PlaceholderAPI")) {
                    new Placeholders().register();
                    papiEnabled = true;
                } else {
                    Console.printInfo("PlaceholderAPI was not Found! PAPI placeholders won't work!");
                }
            }

            if (Bukkit.getPluginManager().isPluginEnabled("FireBallKnockback")) {
                Console.printInfo("I noticed you are using my Fireball jumping addon. " +
                        "As of 5.0.13, you do not need it anymore! Fireball jumping " +
                        "is now built into core MBedwars. Features such as throw cooldown and throw " +
                        "effects have been added to this addon (MBedwarsTweaks). - MetallicGoat"
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

    private boolean isEnabled(String plugin){
        return Bukkit.getPluginManager().isPluginEnabled(plugin);
    }
}