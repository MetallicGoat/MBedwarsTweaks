package me.metallicgoat.tweaksaddon.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.metallicgoat.tweaksaddon.config.ConfigManager.Config;
import org.bukkit.Material;

public class SwordsToolsConfig {

  @Config(
      description = {
          "Items that cannot be placed inside chests"
      }
  )
  public static boolean anti_chest_enabled = false;
  @Config public static List<Material> anti_chest_materials = MainConfig.getDefaultMaterials();

  @Config(
      description = {
          "Items that cannot be dropped on the ground"
      }
  )
  public static boolean anti_drop_enabled = true;
  @Config public static List<Material> anti_drop_materials = MainConfig.getDefaultMaterials();

  @Config(
      description = {
          "When you die with a tool, you will get one tier lower when you respawn",
          "WARNING: May not work properly with keep-on-death enabled"
      }
  )
  public static boolean degrading_buygroups_enabled = false;
  @Config public static List<String> degrading_buygroups = Arrays.asList("pickaxe", "axe");

  @Config(
      description = {
          "With this feature enabled players can only have one tool of a specific type at a time",
          "Players will not be able to downgrade their tool if the is enabled",
          "If force ordered is true, players MUST buy every tier in order"
      }
  )
  public static boolean advanced_tool_replacement_enabled = false;
  @Config public static boolean advanced_tool_replacement_force_ordered = false;
  @Config public static String advanced_tool_replacement_force_ordered_problem = "&cYou need to have a previous tier first";
  @Config public static List<String> advanced_tool_replacement_buygroups = Arrays.asList("pickaxe", "axe");

  @Config(
      description = {
          "Removes a Wooden-Sword if you buy a better sword",
          "If 'all-type' is set to TRUE, ALL sword types will get replaced",
          "If false, only wooden swords will get replaced (Like Hypixel)"
      }
  )
  public static boolean replace_sword_on_buy_enabled = false;
  @Config public static boolean replace_sword_on_buy_all_type = false;

  @Config(
      description = {
          "Chest: If you add your sword to a chest, and have no other sword, you will be given a wooden sword (Like Hypixel)",
          "Drop: Gives players a Wooden Sword if no sword is detected, after they drop a sword (Like Hypixel)"
      }
  )
  public static boolean always_sword_chest_enabled = false;
  @Config public static boolean always_sword_drop_enabled = true;

  @Config(
      description = {
          "Add Items here that you do not want to be effected by Advanced Swords and Tools",
          "For example, A CUSTOM special item that is Golden Sword",
          "ADD ITEMS BY THEIR DISPLAY NAME (DO NOT include color codes)"
      }
  )
  public static List<String> tools_swords_do_not_effect = new ArrayList<>();

}
