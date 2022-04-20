package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.ShopGUIPostProcessEvent;
import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.ShopOpenCause;
import de.marcely.bedwars.api.game.shop.ShopPage;
import de.marcely.bedwars.api.game.shop.layout.ShopLayout;
import de.marcely.bedwars.api.game.shop.product.ItemShopProduct;
import de.marcely.bedwars.api.game.shop.product.ShopProduct;
import de.marcely.bedwars.tools.gui.GUIItem;
import de.marcely.bedwars.tools.gui.type.ChestGUI;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import me.metallicgoat.tweaksaddon.tweaks.advancedswords.DowngradeTools;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;

public class OneSlotTools implements Listener {

    @EventHandler
    public void onPostProcess(ShopGUIPostProcessEvent e) {

        if (!ConfigValue.one_slot_tools_enabled)
            return;

        final Player player = e.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

        if (arena != null && e.getGUI() instanceof ChestGUI) {

            final HashMap<GUIItem, Integer> guiItemIntegerHashMap = new HashMap<>();
            final ChestGUI chestGUI = (ChestGUI) e.getGUI();

            for (int i = chestGUI.getSize() - 1; i > 0; i--) {
                final GUIItem item = chestGUI.getItem(i);
                if (item != null && item.getAttachement() != null) {
                    final Object attachment = item.getAttachement();
                    if (attachment instanceof ShopItem) {
                        final ShopItem shopItem = (ShopItem) attachment;
                        final BuyGroup group = shopItem.getBuyGroup();
                        if (group != null && (group.getName().equalsIgnoreCase("axe")
                                || group.getName().equalsIgnoreCase("pickaxe"))) {

                            final int shopCurrentLevel = shopItem.getBuyGroupLevel();
                            final int nextPlayerLevel = getPlayerToolLevel(group, player) + 1; // next level
                            final int highestLevel = getHighestBuyGroupLevel(group);

                            if ((shopCurrentLevel == highestLevel && nextPlayerLevel >= highestLevel)
                                    || nextPlayerLevel == shopCurrentLevel) {

                                if (group.getName().equalsIgnoreCase("axe"))
                                    guiItemIntegerHashMap.put(item, ConfigValue.one_slot_tools_axe);
                                else
                                    guiItemIntegerHashMap.put(item, ConfigValue.one_slot_tools_pickaxe);

                            }
                            chestGUI.setItem((GUIItem) null, i);
                            continue;
                        }

                        for (ShopProduct rawProduct : shopItem.getProducts()) {
                            if (rawProduct instanceof ItemShopProduct) {
                                final ItemStack[] is = ((ItemShopProduct) rawProduct).getItemStacks();
                                for (ItemStack itemStack : is) {
                                    if (itemStack.getType() == Material.SHEARS) {
                                        guiItemIntegerHashMap.put(item, ConfigValue.one_slot_tools_shears);
                                        chestGUI.setItem((GUIItem) null, i);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            guiItemIntegerHashMap.forEach(chestGUI::setItem);
        }
    }

    // Refresh shop If player buys item with buy group
    @EventHandler
    public void onBuyInShop(PlayerBuyInShopEvent e) {

        if (!ConfigValue.one_slot_tools_enabled)
            return;

        final ShopPage shopPage = e.getItem().getPage();
        final ShopLayout layout = BedwarsAPI.getGameAPI().getDefaultShopLayout();

        if (e.getProblems().isEmpty() && isToolPage(shopPage) && e.getItem().hasBuyGroup()) {
            BedwarsAPI.getGameAPI().openShop(e.getPlayer(), layout, ShopOpenCause.PLUGIN, shopPage);
        }
    }

    private static boolean isToolPage(ShopPage shopPage) {
        for (ShopItem item : shopPage.getItems()) {
            final BuyGroup group = item.getBuyGroup();
            if (group != null && (group.getName().equalsIgnoreCase("axe")
                    || group.getName().equalsIgnoreCase("pickaxe"))) {
                return true;
            }
        }
        return false;
    }

    private static int getPlayerToolLevel(BuyGroup group, Player player) {
        if (group.getName().equalsIgnoreCase("pickaxe")) {
            return DowngradeTools.pickaxeHashMap.get(player);
        } else {
            return DowngradeTools.axeHashMap.get(player);
        }
    }

    private static int getHighestBuyGroupLevel(BuyGroup current) {
        for (BuyGroup buyGroup : BedwarsAPI.getGameAPI().getBuyGroups()) {
            if (buyGroup.getName().equals(current.getName())) {
                return Collections.max(buyGroup.getLevels());
            }
        }
        return 0;
    }
}
