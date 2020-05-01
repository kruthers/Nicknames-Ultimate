package com.kruthers.nicknames.commands;

import com.kruthers.nicknames.Nicknames;
import com.kruthers.nicknames.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RealNameTabCompleter implements TabCompleter{
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("realname")){
            if (sender instanceof Player && args.length==1){
                List<String> list=new ArrayList<>();
                for (String nick: Nicknames.getNicknames()){
                    nick = Utils.removeAllFormatting(nick);
                    if (args[0].length()==0){
                        list.add(nick);
                    } else if (nick.contains(args[0].toLowerCase())){
                        list.add(nick);
                    }
                }
                return list;
            }
        }

        return null;
    }
}
