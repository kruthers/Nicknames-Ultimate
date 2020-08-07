package com.kruthers.nicknames.utils;

import com.kruthers.nicknames.NicknamesUltimate;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

public class Utils {
    private static NicknamesUltimate plugin = NicknamesUltimate.getPlugin(NicknamesUltimate.class);

    public static boolean checkIllegalFormatting(String string, CommandSender player){
        boolean blockFormatting = plugin.getConfig().getBoolean("nickname_settings.block_formatting");

        Pattern colourCodes = Pattern.compile("(&[0-9a-f])");
        Pattern formatCodes = Pattern.compile("(&[l-or])");
        Pattern normalChars = Pattern.compile("[^(a-zA-Z0-9_!\"£$%^&*\\(\\)\\[\\]{}\\-+=\\\\|<>,./?`¬:;'@#~&)]");

        if ((!player.hasPermission("nicknames.colour") || blockFormatting) && colourCodes.matcher(string).find()){
            return true;
        } else if ((!player.hasPermission("nicknames.formatting") || blockFormatting) && formatCodes.matcher(string).find()){
            return true;
        } else return !player.hasPermission("nicknames.anycharacter") && normalChars.matcher(string).find();
    }

    public static String removeAllFormatting(String string){
        string = string.replaceAll("&([0-9a-fk-or])", "");
        return string;
    }

    public static boolean checkBannedWorks(String string){
        List<String> bannedWorks= plugin.getConfig().getStringList("nickname_settings.blocked_words");
        for (String word:bannedWorks){
            if (string.toLowerCase().contains(word)){
                return true;
            }
        }

        return false;
    }

    public static String getErrorMessage(int errorCode){
        FileConfiguration config=plugin.getConfig();
        String errorMessage;
        switch (errorCode){
            default:
                errorMessage = config.getString("messages.unknown_error");
                break;
            case 1:
                errorMessage = config.getString("messages.no_permission");
                break;
            case 2:
                errorMessage = config.getString("messages.nickname.too_short");
                break;
            case 3:
                errorMessage = config.getString("messages.nickname.too_long");
                break;
            case 4:
                errorMessage = config.getString("messages.nickname.taken");
                break;
            case 5:
                errorMessage = config.getString("messages.nickname.illegal_formatting");
                break;
            case 6:
                errorMessage = config.getString("messages.nickname.banned_word");
                break;
            case 7:
                errorMessage = config.getString("messages.nickname.username");
                break;
        }
        return ChatColor.translateAlternateColorCodes('&',errorMessage);
    }

    public static String formatPlaceHolders(String string, Player player){
        string=string.replace("{display_name}",player.getDisplayName());
        string=string.replace("{name}",player.getName());

        if (NicknamesUltimate.vault){
            Chat chat = NicknamesUltimate.getChat();
            string=string.replace("{vault_prefix}",chat.getPlayerPrefix(player));
            string=string.replace("{vault_suffix",chat.getPlayerSuffix(player));
            string=string.replace("{vault_group}",chat.getPrimaryGroup(player));
        }
        if (NicknamesUltimate.placeholderAPI){
            string=PlaceholderAPI.setPlaceholders(player,string);
        }

        string=ChatColor.translateAlternateColorCodes('&',string);
        return string;
    }

    public static void updateName(Player player, String name){

        player.setDisplayName(ChatColor.translateAlternateColorCodes('&',name));

        if (plugin.getConfig().getBoolean("display_name.set_player_list")){
            String displayName = formatPlaceHolders(plugin.getConfig().getString("display_name.player_list_format"),player);
            displayName=ChatColor.translateAlternateColorCodes('&',displayName);
            player.setPlayerListName(displayName);
        }
    }
}
