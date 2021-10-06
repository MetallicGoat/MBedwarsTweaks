package me.metallicgoat.MBedwarsTweaks.tweaks.advancedswords;

import org.bukkit.inventory.PlayerInventory;

import java.util.concurrent.atomic.AtomicBoolean;

public class ToolSwordHelper {
    public static int getSwordLevel(String tool){
        if(tool.contains("WOOD")){
            return 1;
        }else if(tool.contains("STONE")){
            return 2;
        }else if(tool.contains("IRON")){
            return 3;
        }else if(tool.contains("GOLD")){
            return 4;
        }else if(tool.contains("DIAMOND")){
            return 5;
        }else if(tool.contains("NETHERITE")){
            return 6;
        }else{
            return 0;
        }
    }

    public static boolean doesInventoryContain(PlayerInventory playerInventory, String material){
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        playerInventory.forEach(itemStack -> {
            if(itemStack.getType().name().contains(material)){
                atomicBoolean.set(true);
            }
        });
        return atomicBoolean.get();
    }
}
