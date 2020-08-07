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
                        String name = player.getName();
                        if (name.contains(args[1])){
                            list.add(name);
                        }
                    }
                    return list;
                }
            }
        }
        return null;
    }
}
