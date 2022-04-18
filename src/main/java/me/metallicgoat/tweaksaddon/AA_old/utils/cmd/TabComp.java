package me.metallicgoat.tweaksaddon.AA_old.utils.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TabComp implements TabCompleter {
    List<String> arguments = new ArrayList<>();

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command c, @NotNull String s, String[] args) {
        if (this.arguments.isEmpty() &&
                sender.hasPermission("mbedwars-tweaks.admin")) {
            this.arguments.add("reload");
        }
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : this.arguments) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }
        return null;
    }
}
