package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import me.metallicgoat.tweaksaddon.MBedwarsTweaksPlugin;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AlwaysSword implements Listener {

    // TODO keep track of running instances
    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if(!ConfigValue.always_sword_enabled)
            return;

        final Player player = (Player) e.getWhoClicked();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

        if (arena != null
                && arena.getStatus() == ArenaStatus.RUNNING
                && player.getGameMode() != GameMode.SPECTATOR) {

            Bukkit.getServer().getScheduler().runTaskLater((MBedwarsTweaksPlugin.getInstance()), () -> {
                final Inventory pi = player.getInventory();
                final int i = ToolSwordHelper.getSwordsAmount(player);

                if (i >= 2) {
                    for (ItemStack itemStack : pi.getContents()) {
                        if (itemStack != null
                                && itemStack.getType() == ToolSwordHelper.WOOD_SWORD
                                && ToolSwordHelper.isNotToIgnore(itemStack)) {

                            pi.remove(itemStack);

                            if(!ToolSwordHelper.hasBetterSword(player))
                                break;
                        }
                    }
                    // Give player a wood sword if they don't have one
                } else if (i == 0 &&
                        (e.getCurrentItem() == null || e.getCurrentItem() != null && !e.getCurrentItem().getType().name().endsWith("SWORD") && ToolSwordHelper.isNotToIgnore(e.getCurrentItem())) &&
                        (e.getCursor() == null || e.getCursor() != null && !e.getCursor().getType().name().endsWith("SWORD") && ToolSwordHelper.isNotToIgnore(e.getCursor()))) {

                    pi.addItem(ToolSwordHelper.getDefaultWoodSword(player, arena));

                }
            }, 25L);
        }
    }
}
