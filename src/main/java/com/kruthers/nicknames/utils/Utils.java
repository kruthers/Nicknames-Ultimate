package com.kruthers.nicknames.utils;

import com.kruthers.nicknames.Nicknames;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static Nicknames plugin = Nicknames.getPlugin(Nicknames.class);

    public static boolean checkIllegalFormatting(String string, CommandSender player){
        char colourChar = plugin.getConfig().getString("colour_character").toCharArray()[0];
        boolean blockFormatting = plugin.getConfig().getBoolean("block_formatting");

        Pattern colourCodes = Pattern.compile("("+colourChar+"[0-9a-f])");
        Pattern formatCodes = Pattern.compile("("+colourChar+"[l-or])");
        Pattern normalChars = Pattern.compile("[^(a-zA-Z0-9_!\"£$%^&*\\(\\)\\[\\]{}\\-+=\\\\|<>,./?`¬:;'@#~"+colourChar+")]");

        if ((!player.hasPermission("nicknames.colour") || blockFormatting) && colourCodes.matcher(string).find()){
            return true;
        } else if ((!player.hasPermission("nicknames.formatting") || blockFormatting) && formatCodes.matcher(string).find()){
            return true;
        } else return !player.hasPermission("nicknames.anycharacter") && normalChars.matcher(string).find();
    }

    public static String removeAllFormatting(String string){
        char colourChar = plugin.getConfig().getString("colour_character").toCharArray()[0];
        string = string.replaceAll(colourChar+"([0-9a-fk-or])", "");
        return string;
    }

    public static boolean checkBannedWorks(String string){
        List<String> bannedWorks= plugin.getConfig().getStringList("blocked_words");
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
        return ChatColor.translateAlternateColorCodes(config.getString("colour_character").toCharArray()[0],errorMessage);
    }

    public static boolean checkUsername(String nickname,UUID exclude){
        for (World world: Bukkit.getServer().getWorlds()){
            File playerData = new File(world.getWorldFolder()+"/playerdata/");
            for (File file: playerData.listFiles()){
                UUID uuid = UUID.fromString(file.getName().replaceAll(".dat$", ""));
                if(!uuid.equals(exclude)){
                    OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                    if (p.getName()==null){
                        return false;
                    } else if (p.getName().equalsIgnoreCase(nickname)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static String formatPlaceHolders(String string, Player player){
        char colourChar = plugin.getConfig().getString("colour_character").toCharArray()[0];
        string=string.replace("{display_name}",player.getDisplayName());
        string=string.replace("{name}",player.getName());

        if (Nicknames.vault){
            Chat chat = Nicknames.getChat();
            string=string.replace("{vault_prefix}",chat.getPlayerPrefix(player));
            string=string.replace("{vault_suffix",chat.getPlayerSuffix(player));
            string=string.replace("{vault_group}",chat.getPrimaryGroup(player));
        }
        if (Nicknames.placeholderAPI){
            string=PlaceholderAPI.setPlaceholders(player,string);
        }

        string=ChatColor.translateAlternateColorCodes(colourChar,string);
        return string;
    }

    public static void updateName(Player player, String name){
        FileConfiguration config = plugin.getConfig();
        char colourChar = plugin.getConfig().getString("colour_character").toCharArray()[0];

        player.setDisplayName(ChatColor.translateAlternateColorCodes(colourChar,name));

        if (plugin.getConfig().getBoolean("set_player_list_name")){
            String displayName = formatPlaceHolders(config.getString("player_list_name_format"),player);
            displayName=ChatColor.translateAlternateColorCodes(colourChar,displayName);
            player.setPlayerListName(displayName);
        }
    }
}
