package me.metallicgoat.tweaksaddon.config;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.game.spawner.Spawner;
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

    public static boolean custom_action_bar_lobby = false;
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
    public static HashMap<Arena, Collection<PotionEffect>> permanent_effects_arenas = new HashMap<>();

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
    public static List<EntityDamageEvent.DamageCause> remove_invis_remove_causes = new ArrayList<>();

    public static boolean disable_empty_generators = false;
    public static double disable_empty_generators_range = 6;
    public static List<Spawner> disable_empty_generators_spawners = new ArrayList<>();

    // Advanced Swords Tools
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

}