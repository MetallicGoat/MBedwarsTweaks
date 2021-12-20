package me.metallicgoat.tweaks.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import me.metallicgoat.tweaks.MBedwarsTweaks;
import me.metallicgoat.tweaks.utils.ServerManager;
import me.metallicgoat.tweaks.utils.XSeries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class AlwaysSword implements Listener {

    private static final MBedwarsTweaks plugin = MBedwarsTweaks.getInstance();

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        assert XMaterial.WOODEN_SWORD.parseMaterial() != null;

        final Player p = (Player) e.getWhoClicked();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);

        if (arena != null && ServerManager.getSwordsToolsConfig().getBoolean("Always-Sword")) {
            if(p.getGameMode() != GameMode.SPECTATOR && arena.getStatus() == ArenaStatus.RUNNING) {
                Bukkit.getServer().getScheduler().runTaskLater((plugin), () -> {
                    final Inventory pi = p.getInventory();
                    final int i = getSwordsAmount(p);
                    if (i >= 2) {
                        for (ItemStack itemStack : pi.getContents()) {
                            if (itemStack != null && itemStack.getType().equals(XMaterial.WOODEN_SWORD.parseMaterial()) &&
                                    ToolSwordHelper.isNotToIgnore(itemStack)) {
                                pi.remove(itemStack);
                                if(!hasBetterSword(p)) {
                                    break;
                                }
                            }
                        }
                        // Give player a wood sword if they don't have one
                    } else if (i == 0 &&
                            (e.getCurrentItem() == null || e.getCurrentItem() != null && !e.getCurrentItem().getType().name().endsWith("SWORD") && ToolSwordHelper.isNotToIgnore(e.getCurrentItem())) &&
                            (e.getCursor() == null || e.getCursor() != null && !e.getCursor().getType().name().endsWith("SWORD") && ToolSwordHelper.isNotToIgnore(e.getCursor()))) {
                        pi.addItem(new ItemStack(XMaterial.WOODEN_SWORD.parseMaterial()));
                    }
                }, 25L);
            }
        }
    }

    // checks how many swords a player has
    private int getSwordsAmount(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if(item != null)
                if (item.getType().name().endsWith("SWORD") && ToolSwordHelper.isNotToIgnore(item)){
                    count++;
                }
        }
        return count;
    }
    // returns true if a player has a sword that is better than wood
    private boolean hasBetterSword(Player player){
        final Inventory pi = player.getInventory();
        for(ItemStack s:pi.getContents()){
            if(s != null){
                if(s.getType().name().endsWith("SWORD") && ToolSwordHelper.isNotToIgnore(s)){
                    if(!s.equals(new ItemStack(Objects.requireNonNull(XMaterial.WOODEN_SWORD.parseItem())))){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
