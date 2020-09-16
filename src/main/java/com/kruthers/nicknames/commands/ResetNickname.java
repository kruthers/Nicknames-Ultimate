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
import org.bukkit.entity.Player;

public class ResetNickname implements CommandExecutor {
    private NicknamesUltimate plugin;
    public ResetNickname(NicknamesUltimate coreClass) { plugin=coreClass; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length==0){
            if (sender instanceof Player){
                Player player = (Player)sender;
                NicknameManager.removeNick(player);
                Utils.updateName(player,player.getName());
                sender.sendMessage(ChatColor.GREEN+"Reset your nickname");
            } else {
                sender.sendMessage(ChatColor.RED+"Only players can use this instead: /resetnick [user]");
            }
        } else if (args.length==1){
            String playerName=args[0];
            if (sender.hasPermission("nicknames.nickname.others")){
                Player player = plugin.getServer().getPlayer(playerName);
                if (player == null){
                    sender.sendMessage(ChatColor.RED+"Invalid Username");
                } else {
                    Utils.updateName(player, player.getName());
                    NicknameManager.removeNick(player);
                    sender.sendMessage(ChatColor.GREEN+"Reset nickname for "+playerName);
                }
            } else {
                sender.sendMessage(Utils.getErrorMessage(2));
            }
        } else {
            sender.sendMessage(ChatColor.RED+"Invalid arguments correct use: /resetnick [user]");
        }

        return true;
    }
}
