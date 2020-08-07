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

package com.kruthers.nicknames.commands;

import com.kruthers.nicknames.NicknamesUltimate;
import com.kruthers.nicknames.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Properties;
import java.util.logging.Logger;

public class CoreCommand implements CommandExecutor {
    private NicknamesUltimate plugin;
    private Properties properties = NicknamesUltimate.properties;
    private final Logger LOGGER = NicknamesUltimate.LOGGER;
    public CoreCommand(NicknamesUltimate mainClass){
        plugin=mainClass;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("nicknamesultimate")) {
            if (args.length==0){
                sender.sendMessage("Nicknames Ultimate is currently running "+properties.getProperty("version"));
            } else if (args.length==1) {
                switch (args[0].toLowerCase()) {
                    case "version":
                        sender.sendMessage("Nicknames Ultimate is currently running "+properties.getProperty("version"));
                        break;
                    case "reloadconfig":
                        if (sender.hasPermission("nicknames.reload")){
                            LOGGER.info("Reloading config");
                            try {
                                plugin.reloadConfig();
                                sender.sendMessage(ChatColor.GREEN+"Reloaded Nicknames' config");
                                LOGGER.info("Reloaded config");
                            } catch (Error err){
                                LOGGER.warning(err.toString());
                                sender.sendMessage(ChatColor.RED+"Failed to reload Nicknames, view the console for more info.");
                                LOGGER.severe("Failed to reload config.yml");
                            }
                        } else {
                            sender.sendMessage(Utils.getErrorMessage(2));
                        }
                        break;
                    default:
                        sender.sendMessage("Invalid arguments");
                        break;
                }
            } else {
                sender.sendMessage(ChatColor.RED+"Invalid arguments");
            }
        }

        return true;
    }
}
