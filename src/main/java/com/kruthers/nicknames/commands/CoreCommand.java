package com.kruthers.nicknames.commands;

import com.kruthers.nicknames.Nicknames;
import com.kruthers.nicknames.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Properties;
import java.util.logging.Logger;

public class CoreCommand implements CommandExecutor {
    private Nicknames plugin;
    private Properties properties = Nicknames.properties;
    private final Logger LOGGER = Nicknames.LOGGER;
    public CoreCommand(Nicknames mainClass){
        plugin=mainClass;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("nicknames")) {
            if (args.length==0){
                sender.sendMessage("Nicknames is currently running "+properties.getProperty("full_name"));
            } else if (args.length==1) {
                switch (args[0].toLowerCase()) {
                    case "version":
                        sender.sendMessage("Nicknames is currently running "+properties.getProperty("full_name"));
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
