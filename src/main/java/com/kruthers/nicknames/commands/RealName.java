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

import com.kruthers.nicknames.NicknamesUltimate;
import com.kruthers.nicknames.utils.NicknameManager;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RealName implements CommandExecutor {
    private static final NicknamesUltimate plugin = NicknamesUltimate.getPlugin(NicknamesUltimate.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("realname")){
            if (args.length==1) {
                String query = args[0];
                if (NicknameManager.getNicknames().contains(query)){
                    List<OfflinePlayer> players = NicknameManager.getPlayerFromNick(query);
                    if (players.size()==0){
                        sender.sendMessage(ChatColor.RED+"Unable to find nickname on database");
                    } else if (players.size()==1){
                        OfflinePlayer player = players.get(0);
                        sender.sendMessage(ChatColor.GOLD+query+ChatColor.GREEN+", is actually "+player.getName());
                    } else if (players.size()>1) {
                        String names = "";
                        int i =0;
                        for (OfflinePlayer player : players){
                            names = names +player.getName();
                            if (i<players.size()-1){
                                names = names+", ";
                            } else {
                                names = names + " and ";
                            }
                        }

                        sender.sendMessage(ChatColor.GREEN+"Found "+players.size()+" players with that nickname: "+names);
                    }

                } else {
                    sender.sendMessage(ChatColor.RED+"Invalid nickname, unable to find player");
                }
            } else {
                sender.sendMessage(ChatColor.RED+" Invalid arguments, use: /realname [nickname]");
            }
        }

        return true;
    }
}
