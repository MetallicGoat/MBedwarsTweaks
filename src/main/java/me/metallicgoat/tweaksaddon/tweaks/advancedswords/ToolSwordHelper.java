package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.api.arena.Team;
import de.marcely.bedwars.api.event.arena.ArenaDeleteEvent;
import de.marcely.bedwars.api.event.arena.ArenaStatusChangeEvent;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import de.marcely.bedwars.tools.Helper;
import java.util.IdentityHashMap;
import java.util.Map;
import me.metallicgoat.hotbarmanageraddon.HotbarManagerTools;
import me.metallicgoat.tweaksaddon.integration.DependType;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.SwordsToolsConfig;
import me.metallicgoat.tweaksaddon.integration.DependencyLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ToolSwordHelper implements Listener {

  private static final Map<Arena, BuyGroupTracker> buyGroupTrackerMap = new IdentityHashMap<>();
  public static Material WOOD_SWORD;

  public static void load() {
    // Get wooden sword given on spawn
    {
      Material sword = Helper.get().getMaterialByName("WOODEN_SWORD");

      if (sword == null)
        sword = Material.AIR;

      WOOD_SWORD = sword;
    }
  }

  @EventHandler
  public static void onRoundStart(RoundStartEvent event) {
    final Arena arena = event.getArena();

    buyGroupTrackerMap.put(arena, new BuyGroupTracker(arena));
  }

  @EventHandler
  public void onShopBuy(PlayerBuyInShopEvent event) {
    final BuyGroup group = event.getItem().getBuyGroup();

    if (group == null || !event.getProblems().isEmpty())
      return;

    final BuyGroupTracker tracker = buyGroupTrackerMap.get(event.getArena());

    if(tracker != null)
      tracker.upgradeLevel(group, event.getItem().getBuyGroupLevel(), event.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onArenaStatusChangeEvent(ArenaStatusChangeEvent event) {
    if (event.getOldStatus() == ArenaStatus.RUNNING)
      removeArena(event.getArena());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onArenaDeleteEvent(ArenaDeleteEvent event) {
    removeArena(event.getArena());
  }

  private void removeArena(Arena arena) {
    buyGroupTrackerMap.remove(arena);
  }

  public static BuyGroupTracker getBuyGroupTracker(Player player) {
    return getBuyGroupTracker(GameAPI.get().getArenaByPlayer(player));
  }

  public static BuyGroupTracker getBuyGroupTracker(Arena arena) {
    if(arena == null || !buyGroupTrackerMap.containsKey(arena))
      return new BuyGroupTracker(null);

    return buyGroupTrackerMap.get(arena);
  }

  public static ItemStack getDefaultWoodSword(Player player, Arena arena) {
    if (player == null || arena == null)
      return new ItemStack(WOOD_SWORD);

    final Team team = arena.getPlayerTeam(player);
    if (team == null)
      return new ItemStack(WOOD_SWORD);

    for (ItemStack is : arena.getItemsGivenOnSpawn(player, team, true, true)) {
      if (is.getType() == WOOD_SWORD)
        return is.clone();
    }

    return new ItemStack(WOOD_SWORD);
  }

  public static boolean isSword(Material material) {
    return material.name().contains("SWORD");
  }

  public static boolean isTool(Material material) {
    return material.name().contains("AXE");
  }

  public static boolean isAxe(Material material) {
    return isTool(material) && !material.name().contains("_AXE");
  }

  public static boolean isPickaxe(Material material) {
    return material.name().contains("_PICKAXE");
  }

  public static int getSwordToolLevel(Material tool) {

    final String toolName = tool.name();

    if (toolName.contains("WOOD")) {
      return 1;
    } else if (toolName.contains("STONE")) {
      return 2;
    } else if (toolName.contains("IRON")) {
      return 3;
    } else if (toolName.contains("GOLD")) {
      return 4;
    } else if (toolName.contains("DIAMOND")) {
      return 5;
    } else if (toolName.contains("NETHERITE")) {
      return 6;
    } else {
      return 0;
    }
  }

  public static Material getToolInShopProduct(ShopItem shopItem) {
    for (ShopProduct rawProduct : shopItem.getProducts()) {
      if (!(rawProduct instanceof ItemShopProduct))
        continue;

      final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
      for (ItemStack item : is) {
        if (!isTool(item.getType()) || !isNotToIgnore(ChatColor.stripColor(shopItem.getDisplayName())))
          continue;

        return item.getType();
      }
    }
    return Material.AIR;
  }

  // Some items may look like a sword, but not be one. Example: Special Items
  public static boolean isNotToIgnore(ItemStack itemStack) {
    final ItemMeta meta = itemStack.getItemMeta();
    boolean isNotToIgnore = true;
    if (meta != null) {
      for (String s : SwordsToolsConfig.tools_swords_do_not_effect) {
        if (s.equals(ChatColor.stripColor(meta.getDisplayName())) && !s.equals("")) {
          isNotToIgnore = false;
        }
      }
    }
    return isNotToIgnore;
  }

  public static boolean isNotToIgnore(String name) {
    boolean isNotToIgnore = true;
    for (String s : SwordsToolsConfig.tools_swords_do_not_effect) {
      final String formatted = ChatColor.translateAlternateColorCodes('&', s);
      if (formatted.equals(name) && !s.equals("")) {
        isNotToIgnore = false;
      }
    }
    return isNotToIgnore;
  }

  // Checks how many swords a player has
  public static int getSwordsAmount(Player player) {
    int count = 0;
    for (ItemStack item : player.getInventory().getContents()) {
      if (item != null && isSword(item.getType())
          && ToolSwordHelper.isNotToIgnore(item)) {
        count++;
      }
    }
    return count;
  }

  // returns true if a player has a sword that is better than wood
  public static boolean hasBetterSword(Player player) {
    final Inventory pi = player.getInventory();
    for (ItemStack itemStack : pi.getContents()) {
      if (itemStack != null
          && isSword(itemStack.getType())
          && isNotToIgnore(itemStack)
          && itemStack.getType() == ToolSwordHelper.WOOD_SWORD) {

        return true;
      }
    }
    return false;
  }

  public static void givePlayerShopItem(Arena arena, Team team, Player player, ShopItem item) {
    Bukkit.getScheduler().runTaskLater(MBedwarsTweaksPlugin.getInstance(), () -> item.getProducts().forEach(shopProduct -> {
      if (DependType.HOTBAR_MANAGER.isEnabled()) {
        for (ShopProduct product : item.getProducts()) {
          for (ItemStack itemStack : product.getGivingItems(player, team, arena, 1))
            HotbarManagerTools.giveItemsProperly(itemStack, player, item.getPage(), null, true);
        }
      } else {
        shopProduct.give(player, arena.getPlayerTeam(player), arena, 1);
      }
    }), 1L);
  }
}
