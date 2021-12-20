package me.metallicgoat.MBedwarsTweaks.utils.cmd;

import me.metallicgoat.MBedwarsTweaks.MBedwarsTweaks;
import me.metallicgoat.MBedwarsTweaks.utils.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        MBedwarsTweaks plugin = MBedwarsTweaks.getInstance();
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("mbedwars-tweaks.admin")) {
                    ServerManager.reload();
                    sender.sendMessage(ChatColor.GREEN + "[Tweaks] Config reloaded!");
                    return true;
                }
            } else {
                ServerManager.reload();
                plugin.getLogger().info("[Tweaks] Config reloaded!");
                return true;
            }
        }
        return false;
    }
}
