/*
 * Nickname Ultimate - A comprehensive  nickname plugin for spigot
 * Copyright (C) 2020 kruthers
 *
 * This Program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The program  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kruthers.nicknames.commands;

import com.kruthers.nicknames.utils.NicknameManager;
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
            if (sender instanceof Player){
                if (args.length==0){
                    return new ArrayList<>(NicknameManager.getNicknames());
                } else if (args.length==1){
                    List<String> list=new ArrayList<>();
                    for (String nick : NicknameManager.getNicknames()){
                        nick = Utils.removeAllFormatting(nick);
                        if (nick.contains(args[0].toLowerCase())){
                            list.add(nick);
                        }
                    }
                    return list;
                }
            }
        }

        return null;
    }
}
