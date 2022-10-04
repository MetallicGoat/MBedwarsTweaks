package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.YamlConfigurationDescriptor;
import me.metallicgoat.tweaksaddon.Console;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainConfig {

    public static final byte VERSION = 1;
    public static int CURRENT_VERSION = -1;

    private static File getFile(){
        return new File(MBedwarsTweaksPlugin.getAddon().getDataFolder(), "config.yml");
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
        ConfigValue.gen_tiers_enabled = config.getBoolean("Gen-Tiers-Enabled", false);
        ConfigValue.gen_tiers_custom_holo_enabled = config.getBoolean("Gen-Tiers-Holos-Enabled", false);

        ConfigValue.gen_tiers_custom_holo_enabled = config.getBoolean("Gen-Tiers-Holos-Enabled", ConfigValue.gen_tiers_custom_holo_enabled);

        ConfigValue.gen_tiers_start_tier = config.getString("Tier-One-Titles.Tier-Name", ConfigValue.gen_tiers_start_tier);
        {
            if(config.contains("Tier-One-Titles.Spawners")) {
                ConfigValue.gen_tiers_start_spawners.clear();

                for (String string : config.getStringList("Tier-One-Titles.Spawners")) {

                    final DropType type = Util.getDropType(string);

                    if (type != null)
                        ConfigValue.gen_tiers_start_spawners.add(type);

                }
            }
        }

        if (config.contains("Spawner-Title"))
            ConfigValue.gen_tiers_spawner_holo_titles = config.getStringList("Spawner-Title");

        ConfigValue.custom_action_bar_in_game = config.getBoolean("Action-Bar.Enabled-In-Lobby", false);
        ConfigValue.custom_action_bar_in_lobby = config.getBoolean("Action-Bar.Enabled-In-Game", false);
        ConfigValue.custom_action_bar_message = config.getString("Action-Bar.Message", ConfigValue.custom_action_bar_message);

        ConfigValue.final_kill_suffix_enabled = config.getBoolean("Final-Kill-Suffix.Enabled", true);
        ConfigValue.final_kill_suffix = config.getString("Final-Kill-Suffix.Suffix", ConfigValue.final_kill_suffix);

        ConfigValue.buy_message_enabled = config.getBoolean("Buy-Message.Enabled", false);
        ConfigValue.buy_message = config.getString("Buy-Message.Message", ConfigValue.buy_message);

        ConfigValue.custom_bed_break_message_enabled = config.getBoolean("Player-Bed-Break-Message.Enabled", true);
        if (config.contains("Player-Bed-Break-Message.Message"))
            ConfigValue.custom_bed_break_message = config.getStringList("Player-Bed-Break-Message.Message");

        ConfigValue.auto_bed_break_message_enabled = config.getBoolean("Auto-Bed-Break-Message.Enabled", true);
        if (config.contains("Auto-Bed-Break-Message.Message"))
            ConfigValue.auto_bed_break_message = config.getStringList("Auto-Bed-Break-Message.Message");

        ConfigValue.bed_destroy_title_enabled = config.getBoolean("Bed-Destroy-Title.Enabled", true);
        ConfigValue.bed_destroy_title = config.getString("Bed-Destroy-Title.BigTitle", ConfigValue.bed_destroy_title);
        ConfigValue.bed_destroy_subtitle = config.getString("Bed-Destroy-Title.SubTitle", ConfigValue.bed_destroy_subtitle);

        ConfigValue.team_eliminate_message_enabled = config.getBoolean("Team-Eliminate.Enabled", true);
        if (config.contains("Team-Eliminate.Message"))
            ConfigValue.team_eliminate_message = config.getStringList("Team-Eliminate.Message");

        ConfigValue.top_killer_message_enabled = config.getBoolean("Top-Killer-Message.Enabled", false);
        if(config.contains("Top-Killer-Message.Pre-Lines"))
            ConfigValue.top_killer_pre_lines = config.getStringList("Top-Killer-Message.Pre-Lines");
        {
            final ConfigurationSection section = config.getConfigurationSection("Top-Killer-Message.Lines");

            if(section != null) {

                final HashMap<Integer, String> map = new HashMap<>();

                ConfigValue.top_killer_lines.clear();

                for (String key : section.getKeys(false)){
                    final Integer placeValue = Helper.get().parseInt(key);

                    if(placeValue == null)
                        continue;

                    final String line = config.getString("Top-Killer-Message.Lines." + key);

                    if(line != null)
                        map.put(placeValue, line);

                }

                ConfigValue.top_killer_lines = map;
            }
        }

        if(config.contains("Top-Killer-Message.Sub-Lines"))
            ConfigValue.top_killer_sub_lines = config.getStringList("Top-Killer-Message.Sub-Lines");

        ConfigValue.no_top_killer_message_enabled = config.getBoolean("No-Top-Killer-Message.Enabled", false);
        if(config.contains("No-Top-Killer-Message.Message"))
            ConfigValue.no_top_killer_message = config.getStringList("No-Top-Killer-Message.Message");

        ConfigValue.papi_next_tier_lobby_waiting = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Waiting", ConfigValue.papi_next_tier_lobby_waiting);
        ConfigValue.papi_next_tier_lobby_starting = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Starting", ConfigValue.papi_next_tier_lobby_starting);
        ConfigValue.papi_next_tier_lobby_running = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Running", ConfigValue.papi_next_tier_lobby_running);
        ConfigValue.papi_next_tier_lobby_end_lobby = config.getString("Next-Tier-PAPI-Placeholder.Lobby-End-Lobby", ConfigValue.papi_next_tier_lobby_end_lobby);
        ConfigValue.papi_next_tier_lobby_resetting = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Resetting", ConfigValue.papi_next_tier_lobby_resetting);
        ConfigValue.papi_next_tier_lobby_stopped = config.getString("Next-Tier-PAPI-Placeholder.Lobby-Stopped", ConfigValue.papi_next_tier_lobby_stopped);

        {
            if(config.contains("PAPI-Arena-Modes")) {

                final HashMap<String, String> map = new HashMap<>();

                for (String string : config.getStringList("PAPI-Arena-Modes")) {
                    if (string.contains(":")) {
                        final String[] strings = string.split(":");

                        map.put(strings[0], strings[1]);
                    }
                }

                ConfigValue.papi_arena_mode = map;
            }
        }

        ConfigValue.papi_count_spectators_as_players = config.getBoolean("Player-Count-Placeholder-Count-Spectators", false);

        ConfigValue.papi_team_status_has_bed = config.getString("Team-Status-Placeholder.Has-Bed", ConfigValue.papi_team_status_has_bed);
        ConfigValue.papi_team_status_no_bed = config.getString("Team-Status-Placeholder.No-Bed", ConfigValue.papi_team_status_no_bed);
        ConfigValue.papi_team_status_team_dead = config.getString("Team-Status-Placeholder.Team-Dead", ConfigValue.papi_team_status_team_dead);
        ConfigValue.papi_team_status_your_team_suffix = config.getString("Team-Status-Placeholder.Your-Team", ConfigValue.papi_team_status_your_team_suffix);

        ConfigValue.papi_team_you_placeholder = config.getString("Team-You-Placeholder", ConfigValue.papi_team_you_placeholder);

        ConfigValue.papi_player_arena_running_time = config.getString("Running-Time-Placeholder", ConfigValue.papi_player_arena_running_time);

        ConfigValue.gen_tiers_scoreboard_updating_enabled_in_game = config.getBoolean("Force-Scoreboard-Updating.Enabled-In-Game", false);
        ConfigValue.gen_tiers_scoreboard_updating_enabled_in_lobby = config.getBoolean("Force-Scoreboard-Updating.Enabled-In-Lobby", false);
        ConfigValue.gen_tiers_scoreboard_updating_interval = config.getInt("Force-Scoreboard-Updating.Interval", 5);

        ConfigValue.fireball_whitelist_enabled = config.getBoolean("FireballWhitelist.Enabled", false);
        {
            if(config.contains("FireballWhitelist.Blocks")) {

                final List<Material> mats = new ArrayList<>();

                for (String blockName : config.getStringList("FireballWhitelist.Blocks")) {

                    final Material mat = Helper.get().getMaterialByName(blockName);

                    if (mat != null)
                        mats.add(mat);

                }

                ConfigValue.fireball_whitelist_blocks = mats;
            }
        }

        ConfigValue.fireball_cooldown_enabled = config.getBoolean("Fireball-Cooldown.Enabled", true);
        ConfigValue.fireball_cooldown_time = config.getLong("Fireball-Cooldown.Time", 20L);

        ConfigValue.fireball_throw_effects_enabled = config.getBoolean("Fireball-Throw-Effects.Enabled", true);
        {
            if (config.getStringList("Fireball-Throw-Effects.Effects") != null) {
                ConfigValue.fireball_throw_effects = new ArrayList<>();

                for (String raw : config.getStringList("Fireball-Throw-Effects.Effects")) {
                    final String[] parts = raw.split(":");

                    if (parts.length == 3) {
                        if (Util.isInteger(parts[1]) && Util.isInteger(parts[2])) {
                            final PotionEffectType type = PotionEffectType.getByName(parts[0]);

                            if (type != null) {
                                ConfigValue.fireball_throw_effects.add(new PotionEffect(type, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]) - 1));
                            } else
                                Console.printConfigWarn("\"Fireball-Throw-Effects.Effects\": " + parts[0] + " isn't a valid effect type", "Main");
                        } else
                            Console.printConfigWarn("\"Fireball-Throw-Effects.Effects\": " + parts[1] + " or " + parts[2] + " isn't a valid number", "Main");
                    } else
                        Console.printConfigWarn("\"Fireball-Throw-Effects.Effects\": \"" + raw + "\" has " + parts.length + " :, but 3 are needed", "Main");
                }
            }
        }

        ConfigValue.disable_empty_generators = config.getBoolean("Disable-Unused-Gens.Enabled", false);
        ConfigValue.disable_empty_generators_range = config.getDouble("Disable-Unused-Gens.Range", ConfigValue.disable_empty_generators_range);
        {
            if (config.contains("Disable-Unused-Gens.Gen-Types")) {

                final List<DropType> dropTypes = new ArrayList<>();

                for (String string : config.getStringList("Disable-Unused-Gens.Gen-Types")) {
                    final DropType type = Util.getDropType(string);

                    if (type != null)
                        dropTypes.add(type);
                }

                ConfigValue.disable_empty_generators_spawners = dropTypes;
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
            if(config.contains("Break-Invis.Causes")) {

                final List<EntityDamageEvent.DamageCause> damageCauses = new ArrayList<>();

                for (String string : config.getStringList("Break-Invis.Causes")) {

                    try {
                        damageCauses.add(EntityDamageEvent.DamageCause.valueOf(string));
                    } catch (IllegalArgumentException exception) {
                        // Log failure to parse
                    }
                }

                ConfigValue.remove_invis_damge_causes = damageCauses;
            }
        }

        ConfigValue.custom_team_colors_enabled = config.getBoolean("Custom-Team-Chat-Color.Enabled", false);
        {
            if(config.contains("Custom-Team-Chat-Color.Teams")) {

                final HashMap<Team, ChatColor> map = new HashMap<>();

                for (String string : config.getStringList("Custom-Team-Chat-Color.Teams")) {

                    if (!string.contains(":"))
                        continue;

                    final String[] strings = string.split(":");
                    final Team team = Team.getByName(strings[0]);
                    final ChatColor chatColor = ChatColor.getByChar(strings[1]);

                    if (team != null && chatColor != null) {
                        map.put(team, chatColor);
                    }
                }

                ConfigValue.custom_team_colors = map;
            }
        }

        ConfigValue.permanent_effects_enabled = config.getBoolean("Permanent-Effects.Enabled", false);
        loadPermanentEffects(config, "Permanent-Effects.Effects");

        ConfigValue.sponge_particles_enabled = config.getBoolean("Sponge-Particles", true);

        ConfigValue.prevent_liquid_build_up = config.getBoolean("Prevent-Liquid-Build-Up", true);

        ConfigValue.remove_empty_buckets = config.getBoolean("Empty-Buckets", true);
        ConfigValue.remove_empty_potions = config.getBoolean("Empty-Potions", true);

        ConfigValue.friendly_villagers = config.getBoolean("Friendly-Villagers", false);

        ConfigValue.custom_height_cap_enabled = config.getBoolean("Height-Cap.Enabled", false);
        ConfigValue.custom_height_cap_warn = config.getString("Height-Cap.Message", ConfigValue.custom_height_cap_warn);
        {
            if(config.contains("Height-Cap.Arenas")) {

                final HashMap<String, Integer> map = new HashMap<>();

                for (String string : config.getStringList("Height-Cap.Arenas")) {
                    if (!string.contains(":"))
                        continue;

                    final String[] strings = string.split(":");
                    final Integer capInt = Helper.get().parseInt(strings[1]);

                    if (capInt == null)
                        continue;

                    map.put(strings[0], capInt);
                }

                ConfigValue.custom_height_cap_arenas = map;
            }
        }


        // auto update file if newer version
        {
            CURRENT_VERSION = config.getInt("file-version", -1);

            if(CURRENT_VERSION == -1) {
                updateV1Configs(config);
                save();
                return;
            }

            if(CURRENT_VERSION != VERSION) {
                updateV2Configs(config);
                save();
            }
        }
    }

    private static void save() throws Exception {
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
        config.addComment("Add the spawner id of the item being dropped");
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

        config.addComment("You may want to disable the MBedwars team name actionbar in the config.cm2");
        config.set("Action-Bar.Enabled-In-Lobby", ConfigValue.custom_action_bar_in_lobby);
        config.set("Action-Bar.Enabled-In-Game", ConfigValue.custom_action_bar_in_game);
        config.set("Action-Bar.Message", ConfigValue.custom_action_bar_message);

        config.addEmptyLine();

        config.addComment("Add a suffix to the end of a message if the kill is final");
        config.set("Final-Kill-Suffix.Enabled", ConfigValue.final_kill_suffix_enabled);
        config.set("Final-Kill-Suffix.Suffix", ConfigValue.final_kill_suffix);

        config.addEmptyLine();

        config.addComment("Message sent to players when they purchase an item");
        config.addComment("Placeholders: {amount} {product}");
        config.set("Buy-Message.Enabled", ConfigValue.buy_message_enabled);
        config.set("Buy-Message.Message", ConfigValue.buy_message);

        config.addEmptyLine();

        config.addComment("Message displayed when any bed is broken (to everyone in the arena)");
        config.set("Player-Bed-Break-Message.Enabled", ConfigValue.custom_bed_break_message_enabled);
        config.set("Player-Bed-Break-Message.Message", ConfigValue.custom_bed_break_message);

        config.addEmptyLine();

        config.addComment("Message displayed when all beds are broken by the gen tiers system");
        config.set("Auto-Bed-Break-Message.Enabled", ConfigValue.auto_bed_break_message_enabled);
        config.set("Auto-Bed-Break-Message.Message", ConfigValue.auto_bed_break_message);

        config.addEmptyLine();

        config.addComment("Titles displayed when a bed is broken. Overrides the MBedwars titles");
        config.set("Bed-Destroy-Title.Enabled", ConfigValue.bed_destroy_title_enabled);
        config.set("Bed-Destroy-Title.BigTitle", ConfigValue.bed_destroy_title);
        config.set("Bed-Destroy-Title.SubTitle", ConfigValue.bed_destroy_subtitle);

        config.addEmptyLine();

        config.addComment("Message displayed when any team is eliminated (to everyone in the arena)");
        config.addComment("Placeholders: {team-color} {team-name}");
        config.set("Team-Eliminate.Enabled", ConfigValue.team_eliminate_message_enabled);
        config.set("Team-Eliminate.Message", ConfigValue.team_eliminate_message);

        config.addEmptyLine();

        config.addComment("Top killer message displayed at the end of a round");
        config.set("Top-Killer-Message.Enabled", ConfigValue.top_killer_message_enabled);
        config.set("Top-Killer-Message.Pre-Lines", ConfigValue.top_killer_pre_lines);
        {
            for(Map.Entry<Integer, String> entry : ConfigValue.top_killer_lines.entrySet()){
                config.set("Top-Killer-Message.Lines." + entry.getKey(), entry.getValue());
            }
        }
        config.set("Top-Killer-Message.Lines", ConfigValue.top_killer_lines);
        config.set("Top-Killer-Message.Sub-Lines", ConfigValue.top_killer_sub_lines);

        config.addEmptyLine();

        config.addComment("Displayed if Top-Killer-Message IS enabled, but there are no top killers");
        config.set("No-Top-Killer-Message.Enabled", ConfigValue.no_top_killer_message_enabled);
        config.set("No-Top-Killer-Message.Message", ConfigValue.no_top_killer_message);

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
        config.set("Team-Status-Placeholder.Has-Bed", ConfigValue.papi_team_status_has_bed);
        config.set("Team-Status-Placeholder.No-Bed", ConfigValue.papi_team_status_no_bed);
        config.set("Team-Status-Placeholder.Team-Dead", ConfigValue.papi_team_status_team_dead);
        config.set("Team-Status-Placeholder.Your-Team", ConfigValue.papi_team_status_your_team_suffix);

        config.addEmptyLine();

        config.addComment("PAPI-Placeholder: %tweaks_team-you-{name}%");
        config.addComment("Displays specified value if the team in the placeholder matches the players current team");
        config.set("Team-You-Placeholder", ConfigValue.papi_team_you_placeholder);

        config.addEmptyLine();

        config.addComment("PAPI Placeholder: %tweaks_player-arena-running-time%");
        config.addComment("Displays how long an arena has been running for");
        config.set("Running-Time-Placeholder", ConfigValue.papi_player_arena_running_time);

        config.addEmptyLine();

        config.addComment("If set to true, the scoreboard will be force updated to refresh PAPI placeholders");
        config.addComment("Scoreboard will update every X amount of seconds");
        config.addComment("We recommended keeping at 5 or 10 seconds to reduce flicker");
        config.addComment("WARNING: Force updating the MBedwars scoreboard may cause scoreboard flicker");
        config.set("Force-Scoreboard-Updating.Enabled-In-Game", ConfigValue.gen_tiers_scoreboard_updating_enabled_in_game);
        config.set("Force-Scoreboard-Updating.Enabled-In-Lobby", ConfigValue.gen_tiers_scoreboard_updating_enabled_in_lobby);
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

        config.addComment("Fireball use cool down (20 ticks = 1 sec)");
        config.set("Fireball-Cooldown.Enabled", ConfigValue.fireball_cooldown_enabled);
        config.set("Fireball-Cooldown.Time", ConfigValue.fireball_cooldown_time);

        config.addEmptyLine();

        config.addComment("Effects given when a fireball is thrown (Default is like hypixel)");
        config.set("Fireball-Throw-Effects.Enabled", true);
        {
            final List<String> lines = new ArrayList<>();

            for(PotionEffect effect:ConfigValue.fireball_throw_effects)
                lines.add(effect.getType().getName() + ":" + effect.getDuration() + ":" + (effect.getAmplifier() + 1));

            config.addComment("Specify here which potion effects the player shall gain after throwing a fireball");
            config.addComment("Usage: <potion effect name>:<duration in ticks (20 ticks = 1 sec):<level>");
            config.set("Fireball-Throw-Effects.Effects", lines);
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

        config.addComment("Prevents player from opening a bases' chest if bases team is still alive");
        config.set("Lock-Team-Chest.Enabled", ConfigValue.lock_team_chest_enabled);
        config.set("Lock-Team-Chest.Range", ConfigValue.lock_team_chest_range);
        config.set("Lock-Team-Chest.Fail-Open", ConfigValue.lock_team_chest_fail_open);

        config.addEmptyLine();

        config.addComment("Personal Ender Chests. Overrides MBedwars Team Ender Chests");
        config.set("Personal-Ender-Chests.Enabled", ConfigValue.personal_ender_chests_enabled);
        config.set("Personal-Ender-Chests.Title", ConfigValue.personal_ender_chests_name);

        config.addEmptyLine();

        config.addComment("If enabled you still join a server even if the Player Limit is reached.");
        config.set("Bypass-PlayerLimit", ConfigValue.player_limit_bypass);

        config.addEmptyLine();

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
                customTeamColors.add(entry.getKey().name().toLowerCase() + ":" + entry.getValue().getChar());
            }

            config.set("Custom-Team-Chat-Color.Teams", customTeamColors);
        }

        config.addEmptyLine();

        config.addComment("Permanent effects players have while playing");
        config.addComment("ArenaName:PotionEffectName:amplifier");
        config.addComment("Supports arena conditions");
        config.set("Permanent-Effects.Enabled", ConfigValue.permanent_effects_enabled);
        {
            final List<String> values = new ArrayList<>();

            for(Map.Entry<String, PotionEffect> entry : ConfigValue.permanent_effects_arenas.entrySet()){
                final String effectName = entry.getValue().getType().getName().toLowerCase();
                final String amplifier = String.valueOf(entry.getValue().getAmplifier());

                values.add(entry.getKey() + ":" + effectName + ":" + amplifier);

            }
            config.set("Permanent-Effects.Effects", values);
        }

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
        config.set("Friendly-Villagers", ConfigValue.friendly_villagers);

        config.addEmptyLine();

        config.addComment("Add a height cap for specific MBedwars arenas");
        config.addComment("Add height cap like 'arenaName:70' (supports arena conditions)");
        config.set("Height-Cap.Enabled", ConfigValue.custom_height_cap_enabled);
        config.set("Height-Cap.Message", ConfigValue.custom_height_cap_warn);
        {
            final List<String> arenas = new ArrayList<>();

            for(Map.Entry<String, Integer> entry : ConfigValue.custom_height_cap_arenas.entrySet()){
                arenas.add(entry.getKey() + ":" + entry.getValue());
            }

            config.set("Height-Cap.Arenas", arenas);
        }

        config.save(getFile());

    }

    // Use when the name of a config changes or something
    public static void updateV2Configs(FileConfiguration config) {
        // No updates yet :)
    }

    public static void updateV1Configs(FileConfiguration config) {

        {
            if(config.contains("Tier-One-Titles.Spawners")) {
                ConfigValue.gen_tiers_start_spawners.clear();

                for (String string : config.getStringList("Tier-One-Titles.Spawners")) {
                    final Material material = Helper.get().getMaterialByName(string);

                    if (material == null)
                        continue;

                    for (DropType type : GameAPI.get().getDropTypes()) {
                        final ItemStack[] droppingItemStacks = type.getDroppingMaterials();

                        for (ItemStack itemStack : droppingItemStacks) {
                            if (itemStack.getType() == material) {
                                ConfigValue.gen_tiers_start_spawners.add(type);
                                break;
                            }
                        }
                    }
                }
            }
        }

        ConfigValue.custom_action_bar_in_game = config.getBoolean("Action-Bar-Enabled-In-Game", false);
        ConfigValue.custom_action_bar_in_lobby = config.getBoolean("Action-Bar-Enabled-In-Lobby", false);
        ConfigValue.custom_action_bar_message = config.getString("Action-Bar-Message", ConfigValue.custom_action_bar_message);

        ConfigValue.final_kill_suffix_enabled = config.getBoolean("Final-Kill-Message", true);

        if(config.contains("Player-Destroy-Message"))
            ConfigValue.custom_bed_break_message = config.getStringList("Player-Destroy-Message");
        if(config.contains("Auto-Destroy-Message"))
            ConfigValue.auto_bed_break_message = config.getStringList("Auto-Destroy-Message");

        ConfigValue.bed_destroy_title = config.getString("Notification.Big-Title", ConfigValue.bed_destroy_title);
        ConfigValue.bed_destroy_subtitle = config.getString("Notification.Small-Title", ConfigValue.bed_destroy_subtitle);

        ConfigValue.team_eliminate_message_enabled = config.getBoolean("Team-Eliminate-Message-Enabled", true);
        if(config.contains("Team-Eliminate-Message"))
            ConfigValue.team_eliminate_message = config.getStringList("Team-Eliminate-Message");

        if(config.contains("No-Top-Killers-Message"))
            ConfigValue.no_top_killer_message = config.getStringList("No-Top-Killers-Message");

        ConfigValue.gen_tiers_scoreboard_updating_enabled_in_game = config.getBoolean("Scoreboard-Updating");
        ConfigValue.gen_tiers_scoreboard_updating_interval = config.getInt("Scoreboard-Updating-Interval", 5);

        ConfigValue.lock_team_chest_enabled = config.getBoolean("Lock-Team-Chest", false);
        ConfigValue.lock_team_chest_range = config.getDouble("Team-Chest-Distance", 8);
        ConfigValue.lock_team_chest_fail_open = config.getString("Prevent-Chest-Open-Message", ConfigValue.lock_team_chest_fail_open);

        ConfigValue.personal_ender_chests_enabled = config.getBoolean("Personal-Ender-Chests");

        loadPermanentEffects(config, "Permanent-Effects");

    }

    public static void loadPermanentEffects(FileConfiguration config, String path){

        if(config.contains(path)) {

            ConfigValue.permanent_effects_arenas.clear();

            for (String string : config.getStringList(path)) {

                if (!string.contains(":"))
                    continue;

                final String[] strings = string.split(":");

                final String arenaId = strings[0];
                final String effectName = strings[1];
                Integer amplifier = null;

                if (strings.length > 2)
                    amplifier = Helper.get().parseInt(strings[2]);

                if (effectName == null || arenaId == null)
                    continue;

                if (amplifier == null)
                    amplifier = 1;

                final PotionEffectType type = PotionEffectType.getByName(effectName);

                if (type == null)
                    continue;

                final PotionEffect effect = new PotionEffect(type, Integer.MAX_VALUE, amplifier);

                ConfigValue.permanent_effects_arenas.put(arenaId, effect);
            }
        }
    }
}
