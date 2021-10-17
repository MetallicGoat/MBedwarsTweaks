package me.metallicgoat.MBedwarsTweaks.tweaks.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import de.marcely.bedwars.api.event.player.PlayerIngameRespawnEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;

public class DowngradeTools implements Listener {

    private final HashMap<Arena, Collection<Player>> playerArenaHashMap = new HashMap<>();
    private final HashMap<Player, Integer> scoreHashMap = new HashMap<>();







    @EventHandler
    public void onGameStart(RoundStartEvent e){
        playerArenaHashMap.put(e.getArena(), e.getArena().getPlayers());
        e.getArena().getPlayers().forEach(player -> scoreHashMap.put(player, 0));
    }



    @EventHandler
    public void onRespawn(PlayerIngameRespawnEvent e){

        Player player = e.getPlayer();
        Arena arena = e.getArena();
        if(ServerManager.getConfig().getBoolean("Degraded-BuyGroups.Enabled")) {
            ServerManager.getConfig().getStringList("Degraded-BuyGroups.Names").forEach(s -> {
                BuyGroup buyGroup = BedwarsAPI.getGameAPI().getBuyGroup(s);
                int buyGroupLevel = arena.getBuyGroupLevel(player, buyGroup);
                System.out.println(buyGroupLevel);
                if (buyGroupLevel < 1) {
                    buyGroupLevel = buyGroupLevel  - 1;
                    arena.setBuyGroupLevel(player, buyGroup, buyGroupLevel);
                }

                assert buyGroup != null;
                buyGroup.getItems(buyGroupLevel).forEach(shopItem -> shopItem.getProducts().forEach(shopProduct -> {
                    if (shopProduct instanceof ItemShopProduct) {
                        final ItemStack[] is = ((ItemShopProduct) shopProduct).getItemStacks();
                        for (ItemStack item : is) {
                            //player.getInventory().addItem(item);
                        }
                    }
                }));
            });
        }
    }
}
