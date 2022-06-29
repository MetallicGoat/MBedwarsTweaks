package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.YamlConfigurationDescriptor;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.EntityDamageEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {

    private static final byte VERSION = 1;

    private static File getFile(){
        return new File(MBedwarsTweaksPlugin.getAddon().getDataFolder(), "configs.yml");
    }

    public static void load(MBedwarsTweaksPlugin plugin){
        synchronized(Config.class){
            try{
                loadUnchecked(plugin);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void loadUnchecked(MBedwarsTweaksPlugin plugin) throws Exception {
        final File file = getFile();

        if(!file.exists()){
            save(plugin);
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
        ConfigValue.gen_tiers_enabled = config.getBoolean("Gen-Tiers-Enabled", false);
        ConfigValue.gen_tiers_custom_holo_enabled = config.getBoolean("Gen-Tiers-Holos-Enabled", false);

        ConfigValue.gen_tiers_custom_holo_enabled = config.getBoolean("Gen-Tiers-Holos-Enabled", ConfigValue.gen_tiers_custom_holo_enabled);

        ConfigValue.gen_tiers_start_tier = config.getString("Tier-One-Titles.Tier-Name", ConfigValue.gen_tiers_start_tier);
        {
            for (String string : config.getStringList("Tier-One-Titles.Spawners")) {

                final DropType type = Util.getDropType(string);

                if (type != null)
                    ConfigValue.gen_tiers_start_spawners.add(type);

            }
        }

        {
            final List<String> titles = config.getStringList("Spawner-Title");

            if(titles != null){
                ConfigValue.gen_tiers_spawner_holo_titles = titles;
            }
        }

        ConfigValue.custom_action_bar_in_game = config.getBoolean("Action-Bar.Enabled-In-Lobby", false);
        ConfigValue.custom_action_bar_in_lobby = config.getBoolean("Action-Bar.Enabled-In-Game", false);
        ConfigValue.custom_action_bar_message = config.getString("Action-Bar.Message", ConfigValue.custom_action_bar_message);

        ConfigValue.final_kill_suffix_enabled = config.getBoolean("Final-Kill-Suffix.Enabled", true);
        ConfigValue.final_kill_suffix = config.getString("Final-Kill-Suffix.Enabled", ConfigValue.final_kill_suffix);

        ConfigValue.buy_message_enabled = config.getBoolean("Buy-Message.Enabled", false);
        ConfigValue.buy_message = config.getString("Buy-Message.Chat", ConfigValue.buy_message);

        ConfigValue.custom_bed_break_message_enabled = config.getBoolean("Player-Bed-Break-Message.Enabled", true);
        ConfigValue.custom_bed_break_message = config.getStringList("Player-Bed-Break-Message.Message");

        ConfigValue.auto_bed_break_message_enabled = config.getBoolean("Auto-Bed-Break-Message.Enabled", true);
        ConfigValue.auto_bed_break_message = config.getStringList("Auto-Bed-Break-Message.Enabled");

        ConfigValue.bed_destroy_title_enabled = config.getBoolean("Bed-Destroy-Title.Enabled", true);
        ConfigValue.bed_destroy_title = config.getString("Bed-Destroy-Title.BigTitle", ConfigValue.bed_destroy_title);
        ConfigValue.bed_destroy_subtitle = config.getString("Bed-Destroy-Title.SubTitle", ConfigValue.bed_destroy_subtitle);

        ConfigValue.team_eliminate_message_enabled = config.getBoolean("Team-Eliminate.Enabled", true);
        ConfigValue.team_eliminate_message = config.getStringList("Team-Eliminate.Message");

        // TODO Top Killer Message

        ConfigValue.papi_next_tier_lobby_waiting = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Waiting", ConfigValue.papi_next_tier_lobby_waiting);
        ConfigValue.papi_next_tier_lobby_starting = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Starting", ConfigValue.papi_next_tier_lobby_starting);
        ConfigValue.papi_next_tier_lobby_running = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Running", ConfigValue.papi_next_tier_lobby_running);
        ConfigValue.papi_next_tier_lobby_end_lobby = config.getString("Next-Tier-PAPI-Placeholder.Lobby-End-Lobby", ConfigValue.papi_next_tier_lobby_end_lobby);
        ConfigValue.papi_next_tier_lobby_resetting = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Resetting", ConfigValue.papi_next_tier_lobby_resetting);
        ConfigValue.papi_next_tier_lobby_stopped = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Stopped", ConfigValue.papi_next_tier_lobby_stopped);

        {
            for (String string : config.getStringList("PAPI-Arena-Modes")){
                if(string.contains(":")){
                    final String[] strings = string.split(":");

                    ConfigValue.papi_arena_mode.put(strings[0], strings[1]);
                }
            }
        }

        ConfigValue.papi_count_spectators_as_players = config.getBoolean("Player-Count-Placeholder-Count-Spectators", false);

        ConfigValue.gen_tiers_scoreboard_updating_enabled = config.getBoolean("Force-Scoreboard-Updating.Enabled", false);
        ConfigValue.gen_tiers_scoreboard_updating_interval = config.getInt("Force-Scoreboard-Updating.Interval", 5);

        ConfigValue.fireball_whitelist_enabled = config.getBoolean("FireballWhitelist.Enabled", false);
        {
            for(String blockName : config.getStringList("FireballWhitelist.Blocks")){

                final Material mat = Helper.get().getMaterialByName(blockName);

                if(mat != null)
                    ConfigValue.fireball_whitelist_blocks.add(mat);

            }
        }

        ConfigValue.disable_empty_generators = config.getBoolean("Disable-Unused-Gens.Enabled", false);
        ConfigValue.disable_empty_generators_range = config.getDouble("Disable-Unused-Gens.Range", ConfigValue.disable_empty_generators_range);
        {
            for(String string : config.getStringList("Disable-Unused-Gens.Gen-Types")){
                final DropType type = Util.getDropType(string);

                if (type != null)
                    ConfigValue.disable_empty_generators_spawners.add(type);
            }
        }

        ConfigValue.lock_team_chest_enabled = config.getBoolean("Lock-Team-Chest.Enabled", false);
        ConfigValue.lock_team_chest_range = config.getInt("Lock-Team-Chest.Range", 10);
        ConfigValue.lock_team_chest_fail_open = config.getString("Lock-Team-Chest.Fail-Open", ConfigValue.lock_team_chest_fail_open);

        ConfigValue.personal_ender_chests_enabled = config.getBoolean("Personal-Ender-Chests.Enabled", false);
        ConfigValue.personal_ender_chests_name = config.getString("Personal-Ender-Chests.Title", ConfigValue.personal_ender_chests_name);

        ConfigValue.player_limit_bypass = config.getBoolean("Bypass-PlayerLimit", false);

        ConfigValue.remove_invis_ondamage_enabled = config.getBoolean("Break-Invis.Enabled", true);
        {
            for(String string : config.getStringList("Break-Invis.Causes")){

                try {
                    ConfigValue.remove_invis_damge_causes.add(EntityDamageEvent.DamageCause.valueOf(string));
                }catch(IllegalArgumentException exception){
                    // Log failure to parse
                }
            }
        }

        ConfigValue.custom_team_colors_enabled = config.getBoolean("Custom-Team-Chat-Color.Enabled", false);
        {
            for(String string : config.getStringList("Custom-Team-Chat-Color.Teams")){
                if(string.contains(":")){
                    final String[] strings = string.split(":");
                    final Team team = Team.getByName(strings[0]);
                    final ChatColor chatColor = ChatColor.getByChar(strings[1]);

                    if(team != null && chatColor != null){
                        ConfigValue.custom_team_colors.put(team, chatColor);
                    }
                }
            }
        }

        ConfigValue.permanent_effects_enabled = config.getBoolean("Permanent-Effects.Enabled", false);
        // TODO load permanent effects

        ConfigValue.sponge_particles_enabled = config.getBoolean("Sponge-Particles", true);

        ConfigValue.prevent_liquid_build_up = config.getBoolean("Prevent-Liquid-Build-Up", true);

        ConfigValue.remove_empty_buckets = config.getBoolean("Empty-Buckets", true);
        ConfigValue.remove_empty_potions = config.getBoolean("Empty-Buckets", true);


        // auto update file if newer version
        {
            final int currentVersion = config.getInt("file-version", -1);

            if(currentVersion == -1)
                updateV1Configs(config);

            if(currentVersion != VERSION)
                save(plugin);
        }
    }

    private static void save(MBedwarsTweaksPlugin plugin) throws Exception {
        final YamlConfigurationDescriptor config = new YamlConfigurationDescriptor();

        config.addComment("Used for auto-updating the config file. Ignore it");
        config.set("file-version", VERSION);

        config.addEmptyLine();

        config.addComment("############### GEN-TIERS ###############");

        config.addEmptyLine();

        config.addComment("Tiers can be configured in the gen-tiers.yml");
        config.set("Gen-Tiers-Enabled", ConfigValue.gen_tiers_enabled);

        config.addEmptyLine();

        config.addComment("Override MBedwars Holo's");
        config.set("Gen-Tiers-Holos-Enabled", ConfigValue.gen_tiers_custom_holo_enabled);

        config.addEmptyLine();

        config.addComment("Adds 'Tier I' to spawners listed");
        config.addComment("Add the spigot name of the item being dropped");
        config.set("Tier-One-Titles.Tier-Name", ConfigValue.gen_tiers_start_tier);
        {
            final List<String> startSpawners = new ArrayList<>();

            for(DropType dropType : ConfigValue.gen_tiers_start_spawners){
                startSpawners.add(dropType.getId());
            }

            config.set("Tier-One-Titles.Spawners", startSpawners);
        }

        config.addEmptyLine();

        config.addComment("Message shown above spawners");
        config.addComment("Placeholders: {spawner} {spawner-color} {time} {tier}");
        config.set("Spawner-Title", ConfigValue.gen_tiers_spawner_holo_titles);

        config.addEmptyLine();
        
        config.addComment("############### MESSAGES ###############");

        config.addEmptyLine();

        // TODO UPDATE
        config.addComment("You may want to disable the MBedwars team name actionbar in the config.cm2");
        config.set("Action-Bar.Enabled-In-Lobby", ConfigValue.custom_action_bar_in_lobby);
        config.set("Action-Bar.Enabled-In-Game", ConfigValue.custom_action_bar_in_game);
        config.set("Action-Bar.Message", ConfigValue.custom_action_bar_message);

        config.addEmptyLine();

        // TODO UPDATE
        config.addComment("Add a suffix to the end of a message if the kill is final");
        config.set("Final-Kill-Suffix.Enabled", ConfigValue.final_kill_suffix_enabled);
        config.set("Final-Kill-Suffix.Suffix", ConfigValue.final_kill_suffix);

        config.addEmptyLine();

        config.addComment("Message sent to players when they purchase an item");
        config.addComment("Placeholders: {amount} {product}");
        // TODO UPDATE
        config.set("Buy-Message.Enabled", ConfigValue.buy_message_enabled);
        config.set("Buy-Message.Chat", ConfigValue.buy_message);

        config.addEmptyLine();

        // TODO UPDATE
        config.addComment("Message displayed when any bed is broken (to everyone in the arena)");
        config.set("Player-Bed-Break-Message.Enabled", ConfigValue.custom_bed_break_message_enabled);
        config.set("Player-Bed-Break-Message.Message", ConfigValue.custom_bed_break_message);

        // TODO UPDATE
        config.set("Auto-Bed-Break-Message.Enabled", ConfigValue.auto_bed_break_message_enabled);
        config.set("Auto-Bed-Break-Message.Message", ConfigValue.auto_bed_break_message);

        // TODO UPDATE
        config.set("Bed-Destroy-Title.Enabled", ConfigValue.auto_bed_break_message);
        config.set("Bed-Destroy-Title.BigTitle", ConfigValue.auto_bed_break_message);
        config.set("Bed-Destroy-Title.SubTitle", ConfigValue.auto_bed_break_message);

        config.addEmptyLine();

        // TODO UPDATE
        config.addComment("Message displayed when any team is eliminated (to everyone in the arena)");
        config.addComment("Placeholders: {team-color} {team-name}");
        config.set("Team-Eliminate.Enabled", ConfigValue.team_eliminate_message_enabled);
        config.set("Team-Eliminate.Message", ConfigValue.team_eliminate_message);

        config.addEmptyLine();

        // TODO TOP Killer message

        config.addEmptyLine();

        config.addComment("############### PAPI PLACEHOLDERS ###############");

        config.addEmptyLine();

        config.addComment("PAPI Placeholder: %tweaks_next-tier%");
        config.addComment("Placeholders: {next-tier} {time} {sec} {min}");
        config.set("Next-Tier-PAPI-Placeholder.Lobby-Waiting", ConfigValue.papi_next_tier_lobby_waiting);
        config.set("Next-Tier-PAPI-Placeholder.Lobby-Starting", ConfigValue.papi_next_tier_lobby_starting);
        config.set("Next-Tier-PAPI-Placeholder.Lobby-Running", ConfigValue.papi_next_tier_lobby_running);
        config.set("Next-Tier-PAPI-Placeholder.Lobby-End-Lobby", ConfigValue.papi_next_tier_lobby_end_lobby);
        config.set("Next-Tier-PAPI-Placeholder.Lobby-Resetting", ConfigValue.papi_next_tier_lobby_resetting);
        config.set("Next-Tier-PAPI-Placeholder.Lobby-Stopped", ConfigValue.papi_next_tier_lobby_stopped);

        config.addEmptyLine();

        config.addComment("PAPI Placeholder: %tweaks_arena-{mode-name}%");
        config.addComment("ArenaCondition:ModeName");
        {
            final List<String> modeString = new ArrayList<>();

            for(Map.Entry<String, String> entry : ConfigValue.papi_arena_mode.entrySet()){
                modeString.add(entry.getKey() + ":" + entry.getValue());
            }

            config.set("PAPI-Arena-Modes", modeString);
        }

        config.addEmptyLine();

        config.addComment("Whether to count spectators for the player count placeholders");
        config.set("Player-Count-Placeholder-Count-Spectators", ConfigValue.papi_count_spectators_as_players);

        config.addEmptyLine();

        config.addComment("PAPI Placeholder: %tweaks_team-status-{TeamName}%");
        config.addComment("To be used on the scoreboard as is above");
        config.set("Team-Status-Placeholder.Your-Team", ConfigValue.papi_team_status_your_team_suffix);
        config.set("Team-Status-Placeholder.Has-Bed", ConfigValue.papi_team_status_has_bed);
        config.set("Team-Status-Placeholder.No-Bed", ConfigValue.papi_team_status_no_bed);
        config.set("Team-Status-Placeholder.Team-Dead", ConfigValue.papi_team_status_team_dead);

        config.addEmptyLine();

        config.addComment("PAPI-Placeholder: %tweaks_team-you-{name}%");
        config.addComment("Displays specified value if the team in the placeholder matches the players current team");
        config.set("Team-You-Placeholder", ConfigValue.papi_team_you_placeholder);

        config.addEmptyLine();

        // TODO UPDATE
        config.addComment("If set to true, the scoreboard will be force updated to refresh PAPI placeholders");
        config.addComment("Scoreboard will update every X amount of seconds");
        config.addComment("We recommended keeping at 5 or 10 seconds to reduce flicker");
        config.addComment("WARNING: Force updating the MBedwars scoreboard may cause scoreboard flicker");
        config.set("Force-Scoreboard-Updating.Enabled", ConfigValue.gen_tiers_scoreboard_updating_enabled);
        config.set("Force-Scoreboard-Updating.Interval", ConfigValue.gen_tiers_scoreboard_updating_interval);

        config.addEmptyLine();

        config.addComment("############### MISCELLANEOUS ###############");

        config.addEmptyLine();

        config.addComment("Blocks that fireballs will not destroy (Overrides MBedwars' BlackList)");
        config.set("FireballWhitelist.Enabled", ConfigValue.fireball_whitelist_enabled);
        {
            final List<String> blocks = new ArrayList<>();

            for(Material mat : ConfigValue.fireball_whitelist_blocks){
                blocks.add(mat.name());
            }

            config.set("FireballWhitelist.Blocks", blocks);
        }

        config.addEmptyLine();

        config.addComment("Disable generators in empty bases");
        config.addComment("Range = distance from team spawn to spawner");
        config.set("Disable-Unused-Gens.Enabled", ConfigValue.disable_empty_generators);
        config.set("Disable-Unused-Gens.Range", ConfigValue.disable_empty_generators_range);
        {
            final List<String> dropType = new ArrayList<>();

            for(DropType spawner : ConfigValue.disable_empty_generators_spawners){
                dropType.add(spawner.getId());
            }

            config.set("Disable-Unused-Gens.Gen-Types", dropType);
        }

        config.addEmptyLine();

        // TODO UPDATE
        config.addComment("Prevents player from opening a bases' chest if bases team is still alive");
        config.set("Lock-Team-Chest.Enabled", ConfigValue.lock_team_chest_enabled);
        config.set("Lock-Team-Chest.Range", ConfigValue.lock_team_chest_range);
        config.set("Lock-Team-Chest.Fail-Open", ConfigValue.lock_team_chest_fail_open);

        config.addEmptyLine();

        // TODO UPDATE
        config.addComment("Personal Ender Chests. Overrides MBedwars Team Ender Chests");
        config.set("Personal-Ender-Chests.Enabled", ConfigValue.personal_ender_chests_enabled);
        config.set("Personal-Ender-Chests.Title", ConfigValue.personal_ender_chests_name);

        config.addEmptyLine();

        config.addComment("If enabled you still join a server even if the Player Limit is reached.");
        config.set("Bypass-PlayerLimit", ConfigValue.player_limit_bypass);

        config.addComment("Removes invisibility on specified damage causes");
        config.set("Break-Invis.Enabled", ConfigValue.remove_invis_ondamage_enabled);
        {
            final List<String> causes = new ArrayList<>();

            for (EntityDamageEvent.DamageCause cause : ConfigValue.remove_invis_damge_causes){
                causes.add(cause.name());
            }

            config.set("Break-Invis.Causes", causes);
        }

        config.addEmptyLine();

        config.addComment("Change default team chat colors");
        config.addComment("Add teams like: \"DefaultTeamName:ChatColorCar\"");
        config.set("Custom-Team-Chat-Color.Enabled", ConfigValue.custom_team_colors_enabled);
        {
            final List<String> customTeamColors = new ArrayList<>();

            for(Map.Entry<Team, ChatColor> entry : ConfigValue.custom_team_colors.entrySet()){
                customTeamColors.add(entry.getKey().name().toLowerCase() + ":" + entry.getValue().name().toLowerCase());
            }

            config.set("Custom-Team-Chat-Color.Teams", customTeamColors);
        }
        config.set("Custom-Team-Chat-Color.Teams", null);

        config.addEmptyLine();

        config.addComment("Permanent effects players have while playing");
        config.addComment("ArenaName:PotionEffectName:amplifier");
        config.addComment("Supports arena conditions");
        config.set("Permanent-Effects.Enabled", ConfigValue.permanent_effects_enabled);
        // TODO Update + improve value
        /*
        {
            for(ConfigValue.permanent_effects_arenas)
        }
         */

        config.addEmptyLine();

        config.addComment("Cool particle effect when you place a sponge");
        config.set("Sponge-Particles", ConfigValue.sponge_particles_enabled);

        config.addEmptyLine();

        config.addComment("Prevents liquids from going outside the arena border");
        config.addComment("Will not affect other gamemodes");
        config.set("Prevent-Liquid-Build-Up", ConfigValue.prevent_liquid_build_up);

        config.addEmptyLine();

        config.addComment("Remove items on use");
        config.set("Empty-Buckets", ConfigValue.remove_empty_buckets);
        config.set("Empty-Potions", ConfigValue.remove_empty_buckets);


        config.addEmptyLine();

        config.addComment("############### UNSUPPORTED ###############");

        config.addEmptyLine();

        config.addComment("The features may produce unforeseen issues. Use at your own risk.");

        config.addEmptyLine();

        config.addComment("If this is enabled, MBedwars dealers/upgrade-dealers");
        config.addComment("Will look at the closest players");
        // TODO config.set("Friendly-Villagers", ConfigValue.friendly_villagers);

        config.addEmptyLine();

        config.addComment("Add a height cap for specific MBedwars arenas");
        config.addComment("Add height cap like 'arenaName:70' (supports arena conditions)");
        config.set("Height-Cap.Enabled", ConfigValue.custom_height_cap);
        config.set("Height-Cap.Message", ConfigValue.custom_height_cap_warn);
        // TODO defaults
        config.set("Height-Cap.Arenas", new ArrayList<String>());

    }

    public static void updateV2Configs(FileConfiguration config){

    }

    public static void updateV1Configs(FileConfiguration config){

    }
}
