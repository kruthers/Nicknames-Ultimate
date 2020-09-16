package com.kruthers.nicknames.commands;

import com.kruthers.nicknames.utils.NicknameManager;
import com.kruthers.nicknames.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SinglePlayerArgTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list=new ArrayList<>();
        if (args.length==0){
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                list.add(player.getName());
            }
        } else if (args.length==1){
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (player.getName().contains(args[0])){
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}
