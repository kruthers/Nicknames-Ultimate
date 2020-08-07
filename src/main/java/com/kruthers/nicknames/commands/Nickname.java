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
import com.kruthers.nicknames.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Nickname implements CommandExecutor{
    private NicknamesUltimate plugin;
    public Nickname(NicknamesUltimate coreClass){
        plugin=coreClass;
    }

    private List<String> alias = Arrays.asList("nickname","nick");


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();
        if (alias.contains(command.getLabel().toLowerCase())){
            if (args.length==0) {
                if (sender instanceof Player){
                    Player player = (Player)sender;
                    NicknameManager.removeNick(player);
                    Utils.updateName(player,player.getName());
                    sender.sendMessage(ChatColor.GREEN+"Reset your nickname");
                } else {
                    sender.sendMessage(ChatColor.RED+"Only players can use this command, instead use: /resetnick <player>");
                }
            } else if (args.length==1){
                //check its a player
                if (sender instanceof Player){
                    Player player = (Player)sender;
                    String nick = args[0];
                    if (nick.equalsIgnoreCase(player.getName()) || nick.equalsIgnoreCase("reset")) {
                        NicknameManager.removeNick(player);
                        Utils.updateName(player,player.getName());
                        sender.sendMessage(ChatColor.GREEN+"Reset your nickname");
                        return true;
                    }

                    int error_code=NicknameManager.setNickname(player,nick,player,true);
                    if (error_code==0){
                        sender.sendMessage(ChatColor.GREEN+"Your nickname has successfully been set to: "+ChatColor.translateAlternateColorCodes('&',args[0]));
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',Utils.getErrorMessage(error_code)));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Only players can use this command, instead use: /nickname <nickname> [player]");
                }
                return true;
            } else if (args.length==2){
                if (sender.hasPermission("nicknames.nickname.others")) {
                    String playerName=args[1];
                    String nickname = args[0];
                    Player target = plugin.getServer().getPlayer(playerName);
                    if (target==null){
                        sender.sendMessage(ChatColor.RED+"Failed to find the targeted player");
                        return true;
                    }

                    int error_code;
                    if (sender.hasPermission("nicknames.bypass")){
                        error_code=NicknameManager.setNickname(target,nickname,sender,false);
                    } else {
                        error_code=NicknameManager.setNickname(target,nickname,sender,true);
                    }

                    if (error_code==0){
                        sender.sendMessage(ChatColor.GREEN+"Successfully changed "+target.getName()+" nickname too "+nickname);
                        target.sendMessage(ChatColor.GREEN+"Your nickname has been changed to: "+ChatColor.translateAlternateColorCodes('&',nickname)+" by "+sender.getName());
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',Utils.getErrorMessage(error_code)));
                    }
                    return true;
                } else {
                    sender.sendMessage(Utils.getErrorMessage(1));
                }

            } else {
                sender.sendMessage(ChatColor.RED+"Invalid amount of arguments use /nickname [nickname]");
            }
        }

        return true;
    }
}
