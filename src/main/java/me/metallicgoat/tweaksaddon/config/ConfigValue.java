package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.NMSHelper;
import de.marcely.bedwars.tools.VarParticle;
import me.metallicgoat.tweaksaddon.Util;
import me.metallicgoat.tweaksaddon.tweaks.spawners.GenTierLevel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ConfigValue {

    public static boolean goldenGG = false;
    public static boolean arenaLeaveDelayEnabled = false;
    public static int arenaLeaveDelay = 3;
    public static boolean defaultDropTypesExist = false;

    private static final List<Material> defaultMaterials = Arrays.asList(
            Helper.get().getMaterialByName("WOODEN_SWORD"),
            Helper.get().getMaterialByName("SHEARS"),
            Helper.get().getMaterialByName("WOODEN_PICKAXE"),
            Helper.get().getMaterialByName("STONE_PICKAXE"),
            Helper.get().getMaterialByName("IRON_PICKAXE"),
            Helper.get().getMaterialByName("GOLDEN_PICKAXE"),
            Helper.get().getMaterialByName("DIAMOND_PICKAXE"),
            Helper.get().getMaterialByName("WOODEN_AXE"),
            Helper.get().getMaterialByName("STONE_AXE"),
            Helper.get().getMaterialByName("IRON_AXE"),
            Helper.get().getMaterialByName("GOLDEN_AXE"),
            Helper.get().getMaterialByName("DIAMOND_AXE")
    );

    // Main Tweaks Config
    public static boolean sponge_particles_enabled = true;

    public static boolean heal_pool_particle_enabled = true;
    public static boolean heal_pool_particle_team_view_only = true;
    public static boolean prestiges_level_on_exp_bar = false;
    public static int heal_pool_particle_range = 15;
    public static VarParticle heal_pool_particle = VarParticle.newInstanceByName(NMSHelper.get().getVersion() > 8 ? "VILLAGER_HAPPY" : "HAPPY_VILLAGER");

    public static boolean remove_empty_buckets = true;
    public static boolean remove_empty_potions = true;

    public static boolean custom_action_bar_in_lobby = false;
    public static boolean custom_action_bar_in_game = false;
    public static String custom_action_bar_message = "%tweaks_next-tier%";

    public static boolean final_strike_enabled = false;

    public static boolean fireball_whitelist_enabled = false;
    public static List<Material> fireball_whitelist_blocks = Collections.singletonList(Helper.get().getMaterialByName("END_STONE"));

    public static boolean prevent_liquid_build_up = true;

    public static boolean player_limit_bypass = false;

    public static boolean custom_team_colors_enabled = false;
    public static HashMap<Team, ChatColor> custom_team_colors = new HashMap<Team, ChatColor>() {{
        put(Team.CYAN, ChatColor.DARK_AQUA);
    }};

    public static boolean permanent_effects_enabled = false;
    public static HashMap<String, PotionEffect> permanent_effects_arenas = new HashMap<String, PotionEffect>() {{
        put("Ruins", new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
    }};

    public static boolean lock_team_chest_enabled = true;
    public static double lock_team_chest_range = 8;
    public static String lock_team_chest_fail_open = "&cYou cannot open this chest until {team} &chas been eliminated.";

    public static boolean personal_ender_chests_enabled = false;
    public static String personal_ender_chests_name = "Ender Chest";

    public static boolean fireball_cooldown_enabled = true;
    public static long fireball_cooldown_time = 20L;

    public static boolean fireball_throw_effects_enabled = true;
    public static List<PotionEffect> fireball_throw_effects = Collections.singletonList(
            new PotionEffect(PotionEffectType.SLOW, 25, 0)
    );

    public static boolean team_eliminate_message_enabled = true;
    public static List<String> team_eliminate_message = Arrays.asList(
            " ",
            "&f&lTEAM ELIMINATED > {team-color}{team-name} Team &chas been eliminated!",
            " "
    );

    public static boolean final_kill_suffix_enabled = true;
    public static String final_kill_suffix = " &b&lFINAL KILL!";

    public static boolean buy_message_enabled = true;
    public static String buy_message = "&aYou Purchased &6{product} x{amount}";

    public static boolean remove_invis_ondamage_enabled = true;
    public static List<EntityDamageEvent.DamageCause> remove_invis_damge_causes = Arrays.asList(
            EntityDamageEvent.DamageCause.ENTITY_ATTACK,
            EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
            EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
    );

    public static boolean disable_empty_generators = false;
    public static double disable_empty_generators_range = 6;
    public static List<DropType> disable_empty_generators_spawners = defaultDropTypesExist
            ? new ArrayList<>(Arrays.asList(
            Util.getDropType("iron"),
            Util.getDropType("gold")
    )) : new ArrayList<>();

    public static boolean top_killer_message_enabled = true;
    public static List<String> top_killer_pre_lines = Arrays.asList(
            "&a&l-------------------------------",
            "                &lBedWars",
            " "
    );
    public static HashMap<Integer, String> top_killer_lines = new HashMap<Integer, String>() {{
        put(1, "    &e&l1st Killer &7- {killer-name} - {kill-amount}");
        put(2, "    &6&l2nd Killer &7- {killer-name} - {kill-amount}");
        put(3, "    &c&l3rd Killer &7- {killer-name} - {kill-amount}");
    }};
    public static List<String> top_killer_sub_lines = Arrays.asList(
            " ",
            "&a&l-------------------------------"
    );
    public static boolean no_top_killer_message_enabled = false;
    public static List<String> no_top_killer_message = Arrays.asList(
            " ",
            "&eNo Top Killers This Round",
            " "
    );

    public static boolean custom_bed_break_message_enabled = false;
    public static List<String> custom_bed_break_message = Arrays.asList(
            " ",
            "&f&lBED DESTRUCTION > {team-color}{team-name} Bed &7was destroyed by {destroyer-color}{destroyer-name}",
            " "
    );
    public static boolean auto_bed_break_message_enabled = false;
    public static List<String> auto_bed_break_message = Arrays.asList(
            " ",
            "&c&lALL BEDS HAVE BEEN DESTROYED",
            " "
    );
    public static boolean bed_destroy_title_enabled = false;
    public static String bed_destroy_title = "&cBED DESTROYED";
    public static String bed_destroy_subtitle = "&fYou will no longer respawn!";

    public static boolean custom_height_cap_enabled = false;
    public static HashMap<String, Integer> custom_height_cap_arenas = new HashMap<String, Integer>() {{
        put("ArenaName", 70);
    }};
    public static String custom_height_cap_warn = "&cYou cannot build any higher";

    public static boolean friendly_villagers_enabled = true;
    public static int friendly_villagers_range = 20;
    public static boolean friendly_villagers_check_visibility = true;

    // Gen Tiers
    public static boolean gen_tiers_enabled = false;
    // TODO find better loading solution
    public static HashMap<Integer, GenTierLevel> gen_tier_levels = new HashMap<>(); // Defaults set in Load Configs class
    public static boolean gen_tiers_custom_holo_enabled = false;
    public static List<String> gen_tiers_spawner_holo_titles = Arrays.asList(
            "{spawner-color}{spawner}",
            "{tier}",
            "&eSpawning in &c{time} &eseconds!"
    );
    public static List<DropType> gen_tiers_start_spawners = defaultDropTypesExist
            ? Arrays.asList(
            Util.getDropType("emerald"),
            Util.getDropType("diamond")
    ) : new ArrayList<>();
    public static String gen_tiers_start_tier = "&eTier &cI";

    public static String papi_player_arena_running_time = "{min}:{sec}";

    public static boolean gen_tiers_scoreboard_updating_enabled_in_game = false;
    public static boolean gen_tiers_scoreboard_updating_enabled_in_lobby = false;
    public static int gen_tiers_scoreboard_updating_interval = 5;

    public static boolean block_stat_change_enabled = false;
    public static List<String> block_stat_change_arenas = new ArrayList<>();

    // Advanced Swords Tools
    public static boolean anti_chest_enabled = false;
    public static List<Material> anti_chest_materials = defaultMaterials;

    public static boolean anti_drop_enabled = true;
    public static List<Material> anti_drop_materials = defaultMaterials;

    public static boolean degrading_buygroups_enabled = false;
    public static List<String> degrading_buygroups = Arrays.asList("pickaxe", "axe");

    public static List<String> advanced_tool_replacement_buygroups = Arrays.asList("pickaxe", "axe");
    public static boolean advanced_tool_replacement_enabled = false;
    public static boolean advanced_tool_replacement_force_ordered = false;
    public static String advanced_tool_replacement_force_ordered_problem = "&cYou need to have a previous tier first";

    public static boolean replace_sword_on_buy_enabled = false;
    public static boolean replace_sword_on_buy_all_type = false;

    public static boolean always_sword_chest_enabled = false;
    public static boolean always_sword_drop_enabled = true;

    public static List<String> tools_swords_do_not_effect = new ArrayList<>();

    public static boolean advanced_forge_enabled = false;
    public static int advanced_forge_range = 20;
    public static int advanced_forge_level = 3;
    public static String advanced_forge_effected_spawner = "iron";
    public static String advanced_forge_new_drop = "emerald";
    public static int advanced_forge_drop_rate = 15;

    // PAPI
    public static String papi_next_tier_lobby_starting = "&fStarting in &a{time}s";
    public static String papi_next_tier_lobby_waiting = "&fWaiting...";
    public static String papi_next_tier_lobby_end_lobby = "&rGame Over";
    public static String papi_next_tier_lobby_stopped = "&rArena Stopped";
    public static String papi_next_tier_lobby_resetting = "&rArena Regenerating";
    public static String papi_next_tier_lobby_running = "{next-tier} in &a{time}";

    public static String papi_team_status_has_bed = "&a✔";
    public static String papi_team_status_team_dead = "&c✘";
    public static String papi_team_status_no_bed = "&a{player-amount}";
    public static String papi_team_status_your_team_suffix = " &7You";

    public static boolean papi_count_spectators_as_players = false;

    public static String papi_team_you_placeholder = " &7You"; // for tab or smth? (Was a request)

    public static HashMap<String, String> papi_arena_mode = new HashMap<String, String>() {{
        put("[players_per_team=1]", "Solos");
        put("[players_per_team=2]", "Doubles");
        put("[players_per_team=3]", "Trios");
        put("[players_per_team=4]", "Quads");
    }};

}