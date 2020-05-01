package com.kruthers.nicknames.events;

import com.kruthers.nicknames.Nicknames;
import com.kruthers.nicknames.utils.NicknameManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {
    private Nicknames plugin;
    public JoinEvent(Nicknames coreClass){ plugin=coreClass; }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (Nicknames.getNicknameData().containsKey(player.getUniqueId())){
            boolean check = plugin.getConfig().getBoolean("check_on_join");
            String nickname = NicknameManager.getNickname(player.getUniqueId());
            char colourChar = plugin.getConfig().getString("colour_character").toCharArray()[0];
            if (check && !player.hasPermission("nicknames.bypass")){
                if (player.hasPermission("nicknames.nickname")){
                    int error_code=NicknameManager.setNickname(player,nickname,player,true);
                    if (error_code==0){
                        player.sendMessage(ChatColor.GREEN+"Remember you are still nicknamed as "+ChatColor.translateAlternateColorCodes(colourChar,nickname));
                    } else {
                        player.sendMessage(ChatColor.RED+"You previously applied nickname is no longer valid, feel free to chose a new one though!");
                        NicknameManager.removeNick(player.getUniqueId());
                    }
                } else {
                    NicknameManager.removeNick(player.getUniqueId());
                }
            } else {
                int error_code=NicknameManager.setNickname(player,nickname,player,false);
                if (error_code==0){
                    player.sendMessage(ChatColor.GREEN+"Remember you are still nicknamed as "+ChatColor.translateAlternateColorCodes(colourChar,nickname));
                } else {
                    player.sendMessage(ChatColor.RED+"An unknown error occurred when applying your previous nickname");
                    NicknameManager.removeNick(player.getUniqueId());
                }
            }
        }

    }

}
