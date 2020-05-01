package com.kruthers.nicknames;

import com.kruthers.nicknames.commands.*;
import com.kruthers.nicknames.events.JoinEvent;
import com.kruthers.nicknames.events.WorldSave;
import com.kruthers.nicknames.utils.FileManager;
import com.kruthers.nicknames.utils.NicknameManager;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public final class Nicknames extends JavaPlugin {
    public static Logger LOGGER;
    public static Properties properties = new Properties();
    public static boolean placeholderAPI = false;
    public static boolean vault = false;

    private static HashMap<UUID,String> nicknameData = new HashMap<>();
    private static ArrayList<String> nicknameList = new ArrayList<>();
    private static Chat chat = null;

    @Override
    public void onEnable() {
        
        LOGGER = this.getLogger();
        try {
            properties.load(this.getClassLoader().getResourceAsStream(".properties"));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe("Failed to load plugin properties");
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

        LOGGER.info("Plugins hooked. Loading Configs...");
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        LOGGER.info("Loaded config. Loading nickname Data...");

        FileManager.setup();
        NicknameManager.updateNicknameList();
        LOGGER.info("Stored UUIDs and nicknames. Initializing commands");

        //load commands
        this.getCommand("realname").setExecutor(new RealName());
        this.getCommand("nicknames").setExecutor(new CoreCommand(this));
        this.getCommand("nickname").setExecutor(new Nickname(this));
        this.getCommand("resetnickname").setExecutor(new ResetNickname(this));

        this.getCommand("realname").setTabCompleter(new RealNameTabCompleter());
        this.getCommand("nicknames").setTabCompleter(new CoreCommandTabCompleter());
        this.getCommand("nickname").setTabCompleter(new NicknameTabCompleter());
        LOGGER.info("Commands set, registering events");

        //load events
        this.getServer().getPluginManager().registerEvents(new JoinEvent(this),this);
        this.getServer().getPluginManager().registerEvents(new WorldSave(this),this);


        this.getServer().getConsoleSender().sendMessage(ChatColor.GREEN+"Enabled Nicknames by kruthers Version "+properties.getProperty("version"));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        LOGGER.info("Saving nickname Data");
        FileManager.save();
        LOGGER.info("Saved to file");

        this.getServer().getConsoleSender().sendMessage(ChatColor.RED+"Disabled Nicknames for Gamemode 4");
    }

    private boolean setupChat(){
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (rsp != null) {
            chat = rsp.getProvider();
        }
        return chat != null;
    }

    //Handel all variables
    public static HashMap<UUID, String> getNicknameData() { return nicknameData; }

    public static Collection<String> getNicknames() { return nicknameList; }

    public static Chat getChat() { return chat; }

    public static void setNicknameData(HashMap<UUID, String> data) { nicknameData =data; }

    public static void setNicknames(ArrayList<String> data){ nicknameList=data; }
}
