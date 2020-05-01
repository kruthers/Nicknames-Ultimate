package com.kruthers.nicknames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CoreCommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("nicknames")){
            if (sender instanceof Player && args.length==1){
                List<String> list=new ArrayList<>();
                list.add("version");
                if (sender.hasPermission("nicknames.reload")){
                    list.add("reloadconfig");
                }

                return list;
            }
        }
        return null;
    }
}
