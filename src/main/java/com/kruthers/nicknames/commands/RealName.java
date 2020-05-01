package com.kruthers.nicknames.commands;

import com.kruthers.nicknames.Nicknames;
import com.kruthers.nicknames.utils.NicknameManager;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RealName implements CommandExecutor {
    private static final Nicknames plugin = Nicknames.getPlugin(Nicknames.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("realname")){
            if (args.length==1) {
                String nick = args[0].toLowerCase();
                if (Nicknames.getNicknames().contains(nick)){
                    OfflinePlayer player = NicknameManager.getUsername(nick);
                    if (player==null){
                        sender.sendMessage(ChatColor.RED+"Unable to find nickname on database");
                    } else {
                        String formattedNick= Nicknames.getNicknameData().get(player.getUniqueId());
                        formattedNick = ChatColor.translateAlternateColorCodes(plugin.getConfig().getString("colour_character").toCharArray()[0],formattedNick);
                        sender.sendMessage(formattedNick+ChatColor.GREEN+", is actually "+player.getName());
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
