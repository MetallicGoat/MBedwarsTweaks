package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.game.spawner.Spawner;
import me.metallicgoat.tweaksaddon.tweaks.gentiers.GenTierLevel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ConfigValue {

    // Main Tweaks Config
    public static boolean sponge_particles_enabled = false;

    public static boolean remove_empty_buckets = false;
    public static boolean remove_empty_potions = false;

    public static boolean custom_action_bar_in_lobby = false;
    public static boolean custom_action_bar_in_game = false;
    public static String custom_action_bar_message = "Tweaks Action-Bar";

    public static boolean final_strike_enabled = false;

    public static boolean fireball_whitelist_enabled = false;
    public static List<Material> fireball_whitelist_blocks = new ArrayList<>();

    public static boolean prevent_liquid_build_up = false;

    public static boolean player_limit_bypass = false;

    public static boolean custom_team_colors_enabled = false;
    public static HashMap<Team, ChatColor> custom_team_colors = new HashMap<>();

    public static boolean permanent_effects_enabled = false;
    public static HashMap<String, Collection<PotionEffect>> permanent_effects_arenas = new HashMap<>();

    public static boolean lock_team_chest_enabled = false;
    public static double lock_team_chest_range = 8;
    public static String lock_team_chest_fail_open = "&cYou cannot open this chest until {team} &chas been eliminated.";

    public static boolean personal_ender_chests_enabled = false;
    public static String personal_ender_chests_name = "Ender Chest";

    public static boolean team_eliminate_message_enabled = false;
    public static List<String> team_eliminate_message = new ArrayList<>();

    public static boolean final_kill_suffix_enabled = false;
    public static String final_kill_suffix = " &b&lFINAL KILL!";

    public static boolean buy_message_enabled = false;
    public static String buy_message = "&aYou Purchased &6{product} x{amount}";

    public static boolean remove_invis_ondamage_enabled = false;
    public static List<EntityDamageEvent.DamageCause> remove_invis_damge_causes = new ArrayList<>();

    public static boolean disable_empty_generators = false;
    public static double disable_empty_generators_range = 6;
    public static List<Spawner> disable_empty_generators_spawners = new ArrayList<>();

    public static boolean top_killer_message_enabled = false;
    public static List<String> top_killer_pre_lines = new ArrayList<>();
    public static HashMap<Integer, String> top_killer_lines = new HashMap<>();
    public static List<String> top_killer_sub_lines = new ArrayList<>();
    public static boolean no_top_killer_message_enabled = false;
    public static List<String> no_top_killer_message = new ArrayList<>();

    public static boolean custom_bed_break_message = false;
    public static List<String> player_break_bed_message = new ArrayList<>();
    public static boolean auto_destroy_bed_message_enabled = false;
    public static List<String> auto_destroy_bed_message = new ArrayList<>();
    public static boolean bed_break_title_enabled = false;
    public static String bed_destroy_title = "";
    public static String bed_destroy_subtitle = "";

    public static boolean custom_height_cap = false;
    public static HashMap<String, Integer> custom_height_cap_arenas = new HashMap<>();
    public static String custom_height_cap_warn = "";

    // Gen Tiers
    public static boolean gen_tiers_enabled = false;
    public static HashMap<Integer, GenTierLevel> gen_tier_levels = new HashMap<>();
    public static boolean gen_tiers_custom_holo_enabled = false;
    public static List<String> gen_tiers_spawner_holo_titles = new ArrayList<>();
    public static List<Spawner> gen_tiers_start_spawners = new ArrayList<>();
    public static String gen_tiers_start_tier = "";

    public static boolean gen_tiers_scoreboard_updating_enabled = false;
    public static int gen_tiers_scoreboard_updating_interval = 5;


    // Advanced Swords Tools
    public static List<String> tools_swords_do_not_effect = new ArrayList<>();

    public static boolean always_sword_enabled = false;

    public static boolean anti_drop_enabled = false;
    public static List<Material> anti_drop_materials = new ArrayList<>();

    public static boolean sword_drop_enabled = false;
    public static List<Material> sword_drop_materials = new ArrayList<>();

    public static boolean ordered_sword_buy_enabled = false;
    public static String ordered_sword_buy_problem= "";

    public static boolean replace_sword_on_buy_enabled = false;
    public static boolean replace_sword_on_buy_all_type = false;

    public static boolean advanced_tool_replacement_enabled = false;
    public static boolean advanced_tool_replacement_force_ordered = false;
    public static String advanced_tool_replacement_force_ordered_problem = "";
    public static String advanced_tool_replacement_regular_problem = "";

    public static boolean degrading_tool_groups = false;

    public static boolean one_slot_tools_enabled = false;
    public static int one_slot_tools_pickaxe = 20;
    public static int one_slot_tools_axe = 21;
    public static int one_slot_tools_shears = 22;

    public static List<Material> anti_chest_materials = new ArrayList<>();

    // PAPI
    public static String papi_next_tier_lobby_starting = "";
    public static String papi_next_tier_lobby_waiting = "";
    public static String papi_next_tier_lobby_end_lobby = "";
    public static String papi_next_tier_lobby_stopped = "";
    public static String papi_next_tier_lobby_resetting = "";
    public static String papi_next_tier_lobby_running = "";

    public static String papi_team_status_has_bed = "";
    public static String papi_team_status_team_dead = "";
    public static String papi_team_status_no_bed = "";
    public static String papi_team_status_your_team_suffix = "";

    public static boolean papi_count_spectators_as_players = false;

    public static String papi_team_you_placeholder = ""; // for tab or smth? (Was a request)

    // Condition + Mode Name
    public static HashMap<String, String> papi_arena_mode = new HashMap<>();
}