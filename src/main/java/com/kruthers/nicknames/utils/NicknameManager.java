package com.kruthers.nicknames.utils;

import com.kruthers.nicknames.NicknamesUltimate;
import com.kruthers.nicknames.utils.storage.FileStorage;
import com.kruthers.nicknames.utils.storage.MySQL;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class NicknameManager {
    private static final NicknamesUltimate plugin = NicknamesUltimate.getPlugin(NicknamesUltimate.class);
    private static final Logger LOGGER = plugin.getLogger();

    public static List<OfflinePlayer> getPlayerFromNick(String nick){
        if (NicknamesUltimate.storageMethod.equalsIgnoreCase("file")) {
            return FileStorage.getPlayers(nick);
        } else if (NicknamesUltimate.storageMethod.equalsIgnoreCase("mysql")) {
            return MySQL.getPlayers(nick);
        }

        return null;
    }

    public static int setNickname(Player target, String newNick, CommandSender executor, boolean performChecks){
        String unformattedNick = Utils.removeAllFormatting(newNick).toLowerCase();
        boolean checkDuplicate = plugin.getConfig().getBoolean("nickname_settings.check_duplicate");
        boolean checkPlayer = plugin.getConfig().getBoolean("nickname_settings.check_if_user");

        if (performChecks) {
            if (unformattedNick.length()<plugin.getConfig().getInt("nickname_settings.min_length")){
                return 2;
            } else if (unformattedNick.length()>plugin.getConfig().getInt("nickname_settings.max_length")){
                return 3;
            }  else if (Utils.checkIllegalFormatting(newNick,executor)){
                return 5;
            } else if (Utils.checkBannedWorks(unformattedNick)){
                return 6;
            }

            if (NicknamesUltimate.storageMethod.equalsIgnoreCase("file")) {
                if (checkDuplicate && FileStorage.checkNicknameList(unformattedNick,target.getUniqueId())) {
                    return 4;
                } else if (checkPlayer && FileStorage.checkUsername(unformattedNick,target.getUniqueId())) {
                    return 7;
                }
            } else if (NicknamesUltimate.storageMethod.equalsIgnoreCase("mysql") && checkDuplicate) {
                try {
                    if (checkDuplicate && MySQL.checkNick(unformattedNick,target.getUniqueId())){
                        return 4;
                    } else if (checkPlayer && MySQL.checkUsername(unformattedNick,target.getUniqueId())) {
                        return 8;
                    }
                } catch (SQLException err){
                    LOGGER.warning("An unknown exception occurred when checking a nickname against the database: \n"+err.getMessage());
                    return -1;
                }
            }

        }

        switch (NicknamesUltimate.storageMethod){
            case "file":
                FileStorage.saveNick(target,newNick);
                break;
            case "mysql":
                try {
                    MySQL.update_nick(newNick,target);
                } catch (SQLException err){
                    LOGGER.warning("An unknown exception occurred when saving a players new nickname: \n"+err.getMessage());
                    return -1;
                }
        }

        if (!target.hasPermission("nicknames.bypassprefix")) {
            newNick = plugin.getConfig().getString("nickname_settings.prefix") + newNick;
        }
        newNick=newNick+ ChatColor.RESET;
        Utils.updateName(target,newNick);

        return 0;
    }

    public static void removeNick(Player player){
        switch (NicknamesUltimate.storageMethod){
            case "file":
                FileStorage.removeNick(player);
                break;
            case "mysql":
                try {
                    MySQL.update_nick(null,player);
                } catch (SQLException err) {
                    LOGGER.warning("An unknown exception occurred when removing a players nickname: \n"+err.getMessage());
                }
        }
    }

    public static Collection<String> getNicknames(){
        switch (NicknamesUltimate.storageMethod){
            case "file":
                return FileStorage.getNicknames();
            case "mysql":
                return MySQL.getNicknames();
            default:
                return new ArrayList<>();
        }
    }

}
