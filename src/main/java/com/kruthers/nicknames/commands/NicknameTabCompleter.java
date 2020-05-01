package com.kruthers.nicknames.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NicknameTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("nickname")) {
            if (sender instanceof Player) {
                if (args.length == 1) {
                    return new ArrayList<>();
                } else if (args.length == 2 && sender.hasPermission("nicknames.nickname.others")) {
                    List<String> list=new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()){
                        if (!player.isOp()){
                            String name = player.getName();
                            if (name.contains(args[1])){
                                list.add(name);
                            }
                        }
                    }
                    return list;
                }
            }
        }
        return null;
    }
}
