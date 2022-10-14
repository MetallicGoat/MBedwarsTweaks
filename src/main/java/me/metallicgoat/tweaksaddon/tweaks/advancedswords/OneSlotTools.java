package me.metallicgoat.tweaksaddon.tweaks.advancedswords;

import de.marcely.bedwars.api.event.player.PlayerOpenShopEvent;
import de.marcely.bedwars.api.game.shop.BuyGroup;
import de.marcely.bedwars.api.game.shop.ShopItem;
import de.marcely.bedwars.api.game.shop.ShopPage;
import me.metallicgoat.tweaksaddon.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;

public class OneSlotTools implements Listener {

    @EventHandler
    public void onShopOpen(PlayerOpenShopEvent event) {
        final ShopPage page = event.getPage();
        if (!ConfigValue.one_slot_tools_enabled || page == null)
            return;

        final Player player = event.getPlayer();
        final Iterator<? extends ShopItem> it = page.getItems().iterator();
        final List<String> reAddGroups = new ArrayList<>();
        final List<ShopItem> removeItems = new ArrayList<>();

        while (it.hasNext()) {
            final ShopItem item = it.next();
            final BuyGroup group = item.getBuyGroup();
            if (group == null)
                continue;

            if (ToolSwordHelper.oneSlotItemGroups.containsKey(group.getName())) {
                removeItems.add(item);

                if (!reAddGroups.contains(group.getName()))
                    reAddGroups.add(group.getName());
            }
        }

        for (ShopItem item : removeItems)
            page.removeShopItem(item);

        Collections.reverse(reAddGroups);

        // TODO force set force slot to config values (maybe when loading do this)

        for (String string : reAddGroups)
            page.addShopItem(ToolSwordHelper.getNextTierButton(string, player));

    }

    // Reorder items after one slot tools does its thing
    /*
    @EventHandler
    public void onPostProcess(ShopGUIPostProcessEvent event) {
        if (!ConfigValue.one_slot_tools_enabled)
            return;

        final Player player = event.getPlayer();
        final Arena arena = GameAPI.get().getArenaByPlayer(player);

        if (arena == null || event.getLayout().getType() != ShopLayoutType.HYPIXEL_V2 || !(event.getGUI() instanceof ChestGUI))
            return;

        final ChestGUI chestGUI = (ChestGUI) event.getGUI();

        chestGUI.

        for (int i = chestGUI.getSize() - 1; i > 0; i--) {
            final GUIItem item = chestGUI.getItem(i);
            if (item == null || item.getAttachement() == null)
                continue;

            final Object attachment = item.getAttachement();
            if (attachment instanceof ShopItem) {
                final ShopItem shopItem = (ShopItem) attachment;
                final BuyGroup group = shopItem.getBuyGroup();

            }
        }
    }


    @EventHandler
    public void onBuy(PlayerBuyInShopEvent e) {
        final BuyGroup group = e.getItem().getBuyGroup();
        if(group == null)
            return;

        System.out.println(group.getName() + e.getArena().getBuyGroupLevel(e.getPlayer(), group));
    }

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
            return DowngradeTools.pickaxeHashMap.getOrDefault(player, 0);
        } else {
            return DowngradeTools.axeHashMap.getOrDefault(player, 0);
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

     */
}
