package com.kruthers.nicknames.commands;

import com.kruthers.nicknames.NicknamesUltimate;
import com.kruthers.nicknames.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TogglePrefix implements CommandExecutor {
    public static List<UUID> hidden_players = new ArrayList<>();
    private NicknamesUltimate plugin;
    public TogglePrefix(NicknamesUltimate pl){
        this.plugin=pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length==0) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                if (toggleNicknamePrefix(player)){
                    sender.sendMessage(ChatColor.GREEN+"The prefix is now visible in front of your nickname");
                } else {
                    sender.sendMessage(ChatColor.GREEN+"The prefix is no longer visible in front of your nickname");
                }
            } else {
                sender.sendMessage(ChatColor.RED+"Sorry this command only supports players, try using /toggleprefix <player>");
            }
        } else if (args.length==1) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null || !player.isOnline()) {
                sender.sendMessage(ChatColor.RED+"Unable to find player, \""+args[0]+"\", make sure you spelt their name right and they are online");
            } else {
                if (toggleNicknamePrefix(player)){
                    sender.sendMessage(ChatColor.GREEN+"The prefix is now visible in front of "+player.getName()+" nickname");
                    player.sendMessage(ChatColor.GREEN+player.getName()+", has enabled your nickname prefix");
                } else {
                    sender.sendMessage(ChatColor.GREEN+"The prefix is no longer visible in front of "+player.getName()+" nickname");
                    player.sendMessage(ChatColor.GREEN+player.getName()+", has disabled your nickname prefix");
                }
            }
        }
        return true;
    }

    private boolean toggleNicknamePrefix(Player player){
        if (hidden_players.contains(player.getUniqueId())){
            hidden_players.remove(player.getUniqueId());
            String nick = player.getDisplayName();
            String prefix = plugin.getConfig().getString("nickname_settings.prefix");
            Utils.updateName(player,prefix+nick);
            return true;
        } else {
            hidden_players.add(player.getUniqueId());
            String nick = player.getDisplayName();
            String prefix = plugin.getConfig().getString("nickname_settings.prefix");
            nick = nick.substring(prefix.length());
            Utils.updateName(player,nick);
            return false;
        }
    }
}
