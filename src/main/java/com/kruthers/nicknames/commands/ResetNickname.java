package com.kruthers.nicknames.commands;

import com.kruthers.nicknames.Nicknames;
import com.kruthers.nicknames.utils.NicknameManager;
import com.kruthers.nicknames.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetNickname implements CommandExecutor {
    private Nicknames plugin;
    public ResetNickname(Nicknames coreClass) { plugin=coreClass; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length==0){
            if (sender instanceof Player){
                Player player = (Player)sender;
                NicknameManager.removeNick(player.getUniqueId());
                Utils.updateName(player,player.getName());
                sender.sendMessage(ChatColor.GREEN+"Reset your nickname");
            } else {
                sender.sendMessage(ChatColor.RED+"Only players can use this instead: /resetnick <user>");
            }
        } else if (args.length==1){
            String playerName=args[0];
            if (sender.hasPermission("nicknames.nickname.others")){
                Player player = plugin.getServer().getPlayer(playerName);
                if (player == null){
                    sender.sendMessage(ChatColor.RED+"Invalid Username");
                } else {
                    Utils.updateName(player, player.getName());
                    NicknameManager.removeNick(player.getUniqueId());
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
