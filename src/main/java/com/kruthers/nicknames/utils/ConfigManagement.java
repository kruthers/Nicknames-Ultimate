/*
 * Nickname Ultimate - A comprehensive nickname plugin for spigot
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

package com.kruthers.nicknames.utils;

import com.kruthers.nicknames.NicknamesUltimate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class ConfigManagement {
    private static final NicknamesUltimate plugin = JavaPlugin.getPlugin(NicknamesUltimate.class);
    private static  final Logger LOGGER = plugin.getLogger();

    private static double configVersion;
    private static final File configFile = new File(plugin.getDataFolder(),"config.yml");

    public static boolean init(){

        if (!configFile.exists()) {
            LOGGER.info("No config found creating a new file");
            plugin.getConfig().options().copyDefaults(true);
        } else {

            configVersion = plugin.getConfig().getDouble("config-version");
            if (configVersion == 0.0 ){
                LOGGER.info("Found Old config, converting to new");
                old2New();
            }
        }


        plugin.getConfig().set("config-version",1.0);
        plugin.saveConfig();

        return true;
    }

    private static void old2New() {
        FileConfiguration oldConfig = plugin.getConfig();

        configFile.delete();

        plugin.reloadConfig();
        plugin.getConfig().options().copyDefaults(true);


        //copy storage options over
        plugin.getConfig().set("storage.autosave_data",oldConfig.getBoolean("autosave_data"));

        //copy nicknames settings over
        plugin.getConfig().set("nickname_settings.prefix",oldConfig.getString("prefix"));
        plugin.getConfig().set("nickname_settings.max_length",oldConfig.getInt("max_length"));
        plugin.getConfig().set("nickname_settings.min_length",oldConfig.getInt("min_length"));
        plugin.getConfig().set("nickname_settings.check_duplicate",oldConfig.getBoolean("check_duplicate"));
        plugin.getConfig().set("nickname_settings.check_if_user",oldConfig.getBoolean("check_is_user"));
        plugin.getConfig().set("nickname_settings.block_formatting",oldConfig.getBoolean("block_formatting"));
        plugin.getConfig().set("nickname_settings.blocked_words",oldConfig.getStringList("blocked_words"));
        plugin.getConfig().set("nickname_settings.check_on_join",oldConfig.getBoolean("check_on_join"));

        //copy display
        plugin.getConfig().set("display_name.set_player_list",oldConfig.getBoolean("set_player_list_name"));
        plugin.getConfig().set("display_name.player_list_format",oldConfig.getString("player_list_name_format"));

        //messages
        plugin.getConfig().set("messages.unknown_error",oldConfig.getString("messages.unknown_error"));
        plugin.getConfig().set("messages.no_permission",oldConfig.getString("messages.no_permission"));
        plugin.getConfig().set("messages.nickname.too_short",oldConfig.getString("messages.too_short"));
        plugin.getConfig().set("messages.nickname.too_long",oldConfig.getString("messages.too_long"));
        plugin.getConfig().set("messages.nickname.taken",oldConfig.getString("messages.taken"));
        plugin.getConfig().set("messages.nickname.illegal_formatting",oldConfig.getString("messages.illegal_formatting"));
        plugin.getConfig().set("messages.nickname.banned_word",oldConfig.getString("messages.banned_word"));
        plugin.getConfig().set("messages.nickname.username",oldConfig.getString("messages.username"));

        plugin.saveConfig();

    }

}
