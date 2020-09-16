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

package com.kruthers.nicknames;

import com.kruthers.nicknames.commands.*;
import com.kruthers.nicknames.events.*;
import com.kruthers.nicknames.utils.ConfigManagement;
import com.kruthers.nicknames.utils.storage.*;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public final class NicknamesUltimate extends JavaPlugin {
    public static Logger LOGGER;
    public static Properties properties = new Properties();
    public static boolean placeholderAPI = false;
    public static boolean vault = false;
    public static String storageMethod = "file";

    private static Chat chat = null;

    @Override
    public void onEnable() {
        
        LOGGER = this.getLogger();
        try {
            properties.load(this.getClassLoader().getResourceAsStream(".properties"));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe("Failed to load plugin properties");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        LOGGER.info("Hooking into plugins...");
        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI")!=null){
            placeholderAPI=true;
            LOGGER.info("Located and hooked into PlaceholderAPI");
        }
        if (this.getServer().getPluginManager().getPlugin("Vault")!=null){
            if (setupChat()){
                LOGGER.info("Located and hooked into Vault");
                vault=true;
            } else {
                LOGGER.warning("Found vault but failed to hook");
            }
        }

        LOGGER.info("Plugins hooked. Loading and verifying config...");
        ConfigManagement.init();
        storageMethod=getConfig().getString("storage.type");
        LOGGER.info("Loaded config. Loading nickname Data...");

        switch (storageMethod){
            case "file":
                if(FileStorage.setup()){
                    LOGGER.info("Successfully initialized using file storage method");
                } else {
                    LOGGER.severe("Failed to setup storage management, aborting plugin launch");
                    Bukkit.getServer().getPluginManager().disablePlugin(this);
                    return;
                }
                break;
            case "mysql":
                if (MySQL.init()){
                    LOGGER.info("Successfully initialized using sql storage method");
                } else {
                    LOGGER.severe("Failed to setup mysql storage management, aborting plugin launch");
                    Bukkit.getServer().getPluginManager().disablePlugin(this);
                    return;
                }
                break;
            case "mariadb":
                LOGGER.warning("MariaDB is not yet support, please use mysql for now");
                Bukkit.getServer().getPluginManager().disablePlugin(this);
                return;
            default:
                LOGGER.severe("Unknown storage method has been set, if this happens multiple times, please report it on discord with a link to your config");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
        }
        LOGGER.info("Stored UUIDs and nicknames. Initializing commands");

        //load commands
        this.getCommand("realname").setExecutor(new RealName());
        this.getCommand("nicknamesultimate").setExecutor(new CoreCommand(this));
        this.getCommand("nickname").setExecutor(new Nickname(this));
        this.getCommand("resetnickname").setExecutor(new ResetNickname(this));
        this.getCommand("toggleprefix").setExecutor(new TogglePrefix(this));

        this.getCommand("realname").setTabCompleter(new RealNameTabCompleter());
        this.getCommand("nicknamesultimate").setTabCompleter(new CoreCommandTabCompleter());
        this.getCommand("nickname").setTabCompleter(new NicknameTabCompleter());
        this.getCommand("resetnickname").setTabCompleter(new SinglePlayerArgTabCompleter());
        this.getCommand("toggleprefix").setTabCompleter(new SinglePlayerArgTabCompleter());
        LOGGER.info("Commands set, registering events");

        //load events
        this.getServer().getPluginManager().registerEvents(new JoinEvent(this),this);
        this.getServer().getPluginManager().registerEvents(new WorldSave(this),this);


        this.getServer().getConsoleSender().sendMessage(ChatColor.GREEN+"Enabled Nicknames Ultimate by kruthers Version "+properties.getProperty("version"));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        switch (storageMethod){
            case "file":
                LOGGER.info("Saving nickname Data");
                FileStorage.save();
                LOGGER.info("Saved to file");
                break;
            case "mysql":
                LOGGER.info("Terminating mysql connection");
                try {
                    MySQL.end();
                    LOGGER.info("Terminated mysql connection successfully");
                } catch (SQLException err){
                    LOGGER.severe("Failed to close mysql connection check database before starting server next time! \n"+err.getMessage());
                }
        }

        this.getServer().getConsoleSender().sendMessage(ChatColor.RED+"Disabled Nicknames Ultimate");
    }

    private boolean setupChat(){
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (rsp != null) {
            chat = rsp.getProvider();
        }
        return chat != null;
    }

    //Handel all variables
    public static Chat getChat() { return chat; }
}
