package com.kruthers.nicknames.events;

import com.kruthers.nicknames.Nicknames;
import com.kruthers.nicknames.utils.FileManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

public class WorldSave implements Listener {
    private Nicknames plugin;
    public WorldSave(Nicknames coreClass){ plugin=coreClass; }

    @EventHandler
    public void onSave(WorldSaveEvent event){
        if (plugin.getConfig().getBoolean("autosave_data")){
            FileManager.save();
        }
    }
}
