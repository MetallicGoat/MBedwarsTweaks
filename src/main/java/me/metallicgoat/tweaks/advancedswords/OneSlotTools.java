package me.metallicgoat.tweaks.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.event.ShopGUIPostProcessEvent;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.event.player.PlayerOpenShopEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.ShopOpenCause;
import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.tools.gui.GUIItem;
import de.marcely.bedwars.tools.gui.type.ChestGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.HashMap;

public class OneSlotTools implements Listener {

    @EventHandler
    public void onPostProcess(ShopGUIPostProcessEvent e){

        final HashMap<GUIItem, Integer> guiItemIntegerHashMap = new HashMap<>();

        if(e.getGUI() instanceof ChestGUI){

            final Player player = e.getPlayer();
            final ChestGUI chestGUI = (ChestGUI) e.getGUI();

            for(int i = chestGUI.getSize() - 1; i > 0; i--) {
                GUIItem item = chestGUI.getItem(i);
                if (item != null && item.getAttachement() != null) {
                    final Object attachment = item.getAttachement();
                    if(attachment instanceof ShopItem){
                        ShopItem shopItem = (ShopItem) attachment;
                        BuyGroup group = shopItem.getBuyGroup();
                        if(group != null && (group.getName().equalsIgnoreCase("axe")
                                || group.getName().equalsIgnoreCase("pickaxe"))) {

                            int shopCurrentLevel = shopItem.getBuyGroupLevel();
                            int playerCurrent = getPlayerToolLevel(group, player) + 1; //next level
                            int highestLevel = getHighestBuyGroupLevel(group);

                            if(playerCurrent >= highestLevel || playerCurrent == shopCurrentLevel){
                                if(group.getName().equalsIgnoreCase("axe")){
                                    guiItemIntegerHashMap.put(item, 29);
                                }else{
                                    guiItemIntegerHashMap.put(item, 30);
                                }
                            }
                            chestGUI.setItem((GUIItem) null, i);
                        }
                    }
                }
            }
            guiItemIntegerHashMap.forEach(chestGUI::setItem);
        }
    }

    @EventHandler
    public void onBuy(PlayerBuyInShopEvent e){
        ShopPage shopPage = e.getItem().getPage();
        if(e.getProblems().isEmpty() && isToolPage(shopPage)){
            e.getPlayer().closeInventory();
            BedwarsAPI.getGameAPI().openShop(e.getPlayer(), ShopOpenCause.PLUGIN);
        }
    }

    @EventHandler
    public void onOpen(PlayerOpenShopEvent e){
        if(e.getCause() == ShopOpenCause.PLUGIN) {
            for (ShopPage shopPage : BedwarsAPI.getGameAPI().getShopPages()) {
                if (isToolPage(shopPage)) {
                    e.setPage(shopPage);
                }
            }
        }
    }

    private static boolean isToolPage(ShopPage shopPage){
        for(ShopItem item : shopPage.getItems()){
            BuyGroup group = item.getBuyGroup();
            if(group != null && (group.getName().equalsIgnoreCase("axe")
                    || group.getName().equalsIgnoreCase("pickaxe"))){
                return true;
            }
        }
        return false;
    }

    private static int getPlayerToolLevel(BuyGroup group, Player player){
        if(group.getName().equalsIgnoreCase("pickaxe")){
            return DowngradeTools.pickaxeHashMap.get(player);
        }else{
            return DowngradeTools.axeHashMap.get(player);
        }
    }

    private static int getHighestBuyGroupLevel(BuyGroup current){
        for(BuyGroup buyGroup:BedwarsAPI.getGameAPI().getBuyGroups()){
            if(buyGroup.getName().equals(current.getName())){
                return Collections.max(buyGroup.getLevels());
            }
        }
        return 0;
    }
}
