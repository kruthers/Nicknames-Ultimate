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

package com.kruthers.nicknames.events;

import com.kruthers.nicknames.NicknamesUltimate;
import com.kruthers.nicknames.utils.NicknameManager;
import com.kruthers.nicknames.utils.storage.FileStorage;
import com.kruthers.nicknames.utils.storage.MySQL;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {
    private NicknamesUltimate plugin;
    public JoinEvent(NicknamesUltimate coreClass){ plugin=coreClass; }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if (NicknamesUltimate.storageMethod.equals("file")) {
            if (FileStorage.getNicknameData().keySet().contains(player.getUniqueId())){
                String nick = FileStorage.getNickname(player.getUniqueId());
                updateUser(player,nick);
            }
        } if (NicknamesUltimate.storageMethod.equals("mysql")) {
            String[] playerData = MySQL.getPlayerData(player.getUniqueId());
            if (playerData==null) {
                MySQL.insertUser(player);
            } else {
                updateUser(player, playerData[1]);
            }
        }

    }

    private void updateUser(Player player, String nick){
        boolean check = plugin.getConfig().getBoolean("nickname_settings.check_on_join");

        if (check && !player.hasPermission("nicknames.bypass")){
            if (player.hasPermission("nicknames.nickname")){
                int error_code=NicknameManager.setNickname(player,nick,player,true);
                if (error_code==0){
                    player.sendMessage(ChatColor.GREEN+"Remember you are still nicknamed as "+ChatColor.translateAlternateColorCodes('&',nick));
                } else {
                    player.sendMessage(ChatColor.RED+"You previously applied nickname is no longer valid, feel free to chose a new one though!");
                    NicknameManager.removeNick(player);
                }
            } else {
                NicknameManager.removeNick(player);
            }
        } else {
            int error_code=NicknameManager.setNickname(player,nick,player,false);
            if (error_code==0){
                player.sendMessage(ChatColor.GREEN+"Remember you are still nicknamed as "+ChatColor.translateAlternateColorCodes('&',nick));
            } else {
                player.sendMessage(ChatColor.RED+"An unknown error occurred when applying your previous nickname");
                NicknameManager.removeNick(player);
            }
        }

    }

}
