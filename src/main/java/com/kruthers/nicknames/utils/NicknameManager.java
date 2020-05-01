package com.kruthers.nicknames.utils;

import com.kruthers.nicknames.Nicknames;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class NicknameManager {
    private static final Nicknames plugin = Nicknames.getPlugin(Nicknames.class);

    public static OfflinePlayer getUsername(String nick){
        HashMap<UUID,String> nicknameData = Nicknames.getNicknameData();
        for (UUID uuid : nicknameData.keySet()){
            String checkNick = nicknameData.get(uuid);
            checkNick=Utils.removeAllFormatting(checkNick);
            if (checkNick.equalsIgnoreCase(nick)){
                return Bukkit.getPlayer(uuid);
            }
        }

        return null;
    }

    public static String getNickname(UUID checkUUID){
        HashMap<UUID,String> nicknameData = Nicknames.getNicknameData();
        if (nicknameData.size()==0) { return null; }

        return nicknameData.get(checkUUID);
    }

    public static void updateNicknameList(){
        ArrayList<String> nicknameList = new ArrayList<>();
        for (String nick : Nicknames.getNicknameData().values()){
            nick = Utils.removeAllFormatting(nick);
            nicknameList.add(nick.toLowerCase());
        }
        Nicknames.setNicknames(nicknameList);
    }


    public static int setNickname(Player target, String newNick, CommandSender executor, boolean performChecks){
        FileConfiguration config = plugin.getConfig();
        String unformattedNick = Utils.removeAllFormatting(newNick).toLowerCase();
        boolean checkDuplicate = plugin.getConfig().getBoolean("check_duplicate");

        if (performChecks){
            if (unformattedNick.length()<config.getInt("min_length")){
                return 2;
            } else if (unformattedNick.length()>config.getInt("max_length")){
                return 3;
            } else if (checkNicknameList(unformattedNick,target.getUniqueId()) && checkDuplicate){
                return 4;
            } else if (Utils.checkIllegalFormatting(newNick,executor)){
                return 5;
            } else if (Utils.checkBannedWorks(unformattedNick)){
                return 6;
            } else if (Utils.checkUsername(unformattedNick,target.getUniqueId()) && checkDuplicate){
                return 7;
            }
        }

        HashMap<UUID,String> nicknamesData=Nicknames.getNicknameData();
        nicknamesData.put(target.getUniqueId(),newNick);
        Nicknames.setNicknameData(nicknamesData);
        updateNicknameList();

        if (!target.hasPermission("nicknames.bypassprefix")) {
            newNick = config.getString("prefix") + newNick;
        }
        newNick=newNick+ ChatColor.RESET;
        Utils.updateName(target,newNick);

        return 0;
    }

    public static void removeNick(UUID uuid){
        HashMap<UUID,String> nicknamesData=Nicknames.getNicknameData();
        nicknamesData.remove(uuid);
        Nicknames.setNicknameData(nicknamesData);
        updateNicknameList();
    }

    private static boolean checkNicknameList(String nick,UUID ignored){
        if (!Nicknames.getNicknames().contains(nick.toLowerCase())){ return false; }

        HashMap<UUID,String> nicknameDate = Nicknames.getNicknameData();
        for (UUID uuid : nicknameDate.keySet()){
            String string=Utils.removeAllFormatting(nicknameDate.get(uuid));
            if (nick.equalsIgnoreCase(string) && !ignored.toString().equals(uuid.toString())){
                return true;
            }
        }
        return false;
    }


    @Deprecated
    public static void updateNick(String uuid, String newNick){
        HashMap<UUID,String> nicknamesData=Nicknames.getNicknameData();
        nicknamesData.put(UUID.fromString(uuid),newNick);
        Nicknames.setNicknameData(nicknamesData);
        updateNicknameList();
    }

    @Deprecated
    public static boolean removeNick(String uuid){
        HashMap<UUID,String> nicknamesData=Nicknames.getNicknameData();
        if (nicknamesData.remove(UUID.fromString(uuid))!=null){
            Nicknames.setNicknameData(nicknamesData);
            updateNicknameList();
            return true;
        } else {
            return false;
        }
    }

}
